/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mycompany.chservicetime.presentation.timeslotlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.model.TimeSlot;
import com.mycompany.chservicetime.presentation.addedittimeslot.AddEditTimeSlotActivity;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Display a grid of {@link TimeSlot}s. User can choose to view all, active or completed timeSlots.
 */
public class TimeSlotListFragment extends Fragment implements TimeSlotListView {

    private TimeSlotsAdapter mListAdapter;

    private View mNoTimeSlotsView;

    private ImageView mNoTimeSlotIcon;

    private TextView mNoTimeSlotMainView;

    private TextView mNoTimeSlotAddView;

    private LinearLayout mTimeSlotsView;

    TimeSlotListPresenter mTimeSlotListPresenter;

    public TimeSlotListFragment() {
        // Requires empty public constructor
    }

    public static TimeSlotListFragment newInstance() {
        return new TimeSlotListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new TimeSlotsAdapter(new ArrayList<TimeSlot>(0), mItemListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getPresenter().result(requestCode, resultCode);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.timeslots_frag, container, false);

        // Set up timeSlots view
        ListView listView = (ListView) root.findViewById(R.id.timeslot_list);
        listView.setAdapter(mListAdapter);
        mTimeSlotsView = (LinearLayout) root.findViewById(R.id.timeslotsLL);

        // Set up  no timeSlots view
        mNoTimeSlotsView = root.findViewById(R.id.noTimeSlots);
        mNoTimeSlotIcon = (ImageView) root.findViewById(R.id.noTimeSlotsIcon);
        mNoTimeSlotMainView = (TextView) root.findViewById(R.id.noTimeSlotsMain);
        mNoTimeSlotAddView = (TextView) root.findViewById(R.id.noTimeSlotsAdd);
        mNoTimeSlotAddView.setOnClickListener(v -> showAddEditTimeSlot(null));

        // Set up floating action button
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_add_timeslot);

        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(v -> getPresenter().addEditTimeSlot(null));

        // Set up progress indicator
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout =
                (ScrollChildSwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(listView);

        swipeRefreshLayout.setOnRefreshListener(() -> getPresenter().loadTimeSlots(false));

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                getPresenter().clearTimeSlots();
                break;
            case R.id.menu_refresh:
                getPresenter().loadTimeSlots(true);
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.timeslots_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Listener for clicks on timeSlots in the ListView.
     */
    TimeSlotItemListener mItemListener = new TimeSlotItemListener() {
        @Override
        public void onTimeSlotClick(TimeSlot clickedTimeSlot) {
            //getPresenter().openTimeSlotDetails(clickedTimeSlot);
        }

        @Override
        public void onCompleteTimeSlotClick(TimeSlot completedTimeSlot) {
            //getPresenter().completeTimeSlot(completedTimeSlot);
        }

        @Override
        public void onActivateTimeSlotClick(TimeSlot activatedTimeSlot) {
            //getPresenter().activateTimeSlot(activatedTimeSlot);
        }
    };

    @Override
    public void setLoadingIndicator(final boolean active) {
        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl =
                (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);

        // Make sure setRefreshing() is called after the layout is done with everything else.
        srl.post(() -> srl.setRefreshing(active));
    }

    @Override
    public void showTimeSlots(List<TimeSlot> timeSlots) {
        mListAdapter.replaceData(timeSlots);

        mTimeSlotsView.setVisibility(View.VISIBLE);
        mNoTimeSlotsView.setVisibility(View.GONE);
    }

    @Override
    public void showNoTimeSlots() {
        showNoTimeSlotsViews(
                getResources().getString(R.string.no_timeslots_all),
                R.drawable.ic_assignment_turned_in_24dp,
                false
        );
    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_timeslot));
    }

    @Override
    public void showTimeSlotActivationFlagMessage(boolean activationFlag) {
        if (activationFlag)
            showMessage(String.format(getString(R.string.timeslot_marked_active), ""));
        else
            showMessage(String.format(getString(R.string.timeslot_marked_inactive), ""));
    }

    @Override
    public void showTimeSlotsClearedMessage() {
        showMessage(getString(R.string.timeslots_cleared));
    }

    private void showNoTimeSlotsViews(String mainText, int iconRes, boolean showAddView) {
        mTimeSlotsView.setVisibility(View.GONE);
        mNoTimeSlotsView.setVisibility(View.VISIBLE);

        mNoTimeSlotMainView.setText(mainText);
        mNoTimeSlotIcon.setImageDrawable(getResources().getDrawable(iconRes));
        mNoTimeSlotAddView.setVisibility(showAddView ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showAddEditTimeSlot(String id) {
        Intent intent = new Intent(getContext(), AddEditTimeSlotActivity.class);
        startActivityForResult(intent, AddEditTimeSlotActivity.REQUEST_ADD_TIME_SLOT);
    }

//    @Override
//    public void showTimeSlotDetailsUi(String timeSlotId) {
//        // in it's own Activity, since it makes more sense that way and it gives us the flexibility
//        // to show some Intent stubbing.
//        Intent intent = new Intent(getContext(), TimeSlotDetailActivity.class);
//        intent.putExtra(TimeSlotDetailActivity.EXTRA_TASK_ID, timeSlotId);
//        startActivity(intent);
//    }

    @Override
    public void showLoadingTimeSlotsError() {
        showMessage(getString(R.string.loading_timeslots_error));
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    private static class TimeSlotsAdapter extends BaseAdapter {

        private List<TimeSlot> mTimeSlots;
        private TimeSlotItemListener mItemListener;

        public TimeSlotsAdapter(List<TimeSlot> timeSlots, TimeSlotItemListener itemListener) {
            setList(timeSlots);
            mItemListener = itemListener;
        }

        public void replaceData(List<TimeSlot> timeSlots) {
            setList(timeSlots);
            notifyDataSetChanged();
        }

        private void setList(List<TimeSlot> timeSlots) {
            mTimeSlots = checkNotNull(timeSlots);
        }

        @Override
        public int getCount() {
            return mTimeSlots.size();
        }

        @Override
        public TimeSlot getItem(int i) {
            return mTimeSlots.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = view;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.timeslot_item, viewGroup, false);
            }

            final TimeSlot timeSlot = getItem(i);

            TextView titleTV = (TextView) rowView.findViewById(R.id.title);
            titleTV.setText(timeSlot.name());

            CheckBox completeCB = (CheckBox) rowView.findViewById(R.id.complete);

//            // Active/completed timeSlot UI
//            completeCB.setChecked(timeSlot.isCompleted());
//            if (timeSlot.isCompleted()) {
//                rowView.setBackgroundDrawable(viewGroup.getContext()
//                        .getResources().getDrawable(R.drawable.list_completed_touch_feedback));
//            } else {
//                rowView.setBackgroundDrawable(viewGroup.getContext()
//                        .getResources().getDrawable(R.drawable.touch_feedback));
//            }

            completeCB.setOnClickListener(v -> {
                if (!timeSlot.activation_flag()) {
                    mItemListener.onCompleteTimeSlotClick(timeSlot);
                } else {
                    mItemListener.onActivateTimeSlotClick(timeSlot);
                }
            });

            rowView.setOnClickListener(__ -> mItemListener.onTimeSlotClick(timeSlot));

            return rowView;
        }
    }

    public interface TimeSlotItemListener {

        void onTimeSlotClick(TimeSlot clickedTimeSlot);

        void onCompleteTimeSlotClick(TimeSlot completedTimeSlot);

        void onActivateTimeSlotClick(TimeSlot activatedTimeSlot);
    }

    public TimeSlotListPresenter getPresenter() {
        return mTimeSlotListPresenter;
    }

    public void setPresenter(@NonNull TimeSlotListPresenter timeSlotListPresenter) {
        mTimeSlotListPresenter = checkNotNull(timeSlotListPresenter);
    }
}
