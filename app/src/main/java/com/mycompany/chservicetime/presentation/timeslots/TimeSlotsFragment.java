package com.mycompany.chservicetime.presentation.timeslots;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.data.preference.PreferenceSupport;
import com.mycompany.chservicetime.model.TimeSlot;
import com.mycompany.chservicetime.presentation.addedittimeslot.AddEditTimeSlotActivity;
import com.mycompany.chservicetime.presentation.addedittimeslot.AddEditTimeSlotFragment;
import com.mycompany.chservicetime.util.EspressoIdlingResource;

import java.io.IOException;

import static com.mycompany.chservicetime.util.LogUtils.LOGD;
import static com.mycompany.chservicetime.util.LogUtils.makeLogTag;


/**
 * Display a grid of {@link TimeSlot}s. User can choose to view all TimeSlots.
 */
public class TimeSlotsFragment extends Fragment implements TimeSlotsContract.View,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = makeLogTag("TimeSlotsFragment");

    private TimeSlotsContract.Presenter mPresenter;

    TimeSlotCursorRecyclerAdapter mAdapter;

    RecyclerView mRecyclerView;
    LinearLayout mEmptyView;

    TextView mNextAlarmTextView;
    SharedPreferences sp;

    public TimeSlotsFragment() {
        // Requires empty public constructor
    }

    public static TimeSlotsFragment newInstance() {
        return new TimeSlotsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_time_slots, container, false);

        initViews(root);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
        sp.registerOnSharedPreferenceChangeListener(this);
        // always display the newest value.
        mNextAlarmTextView.setText(PreferenceSupport.getNextAlarmDetail(getContext()));
    }

    @Override
    public void onPause() {
        super.onPause();
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode);
    }

    private void initViews(View root) {
        mEmptyView = (LinearLayout) root.findViewById(R.id.empty_layout);
        mNextAlarmTextView = (TextView) root.findViewById(R.id.nextOperationTextView);

        mRecyclerView = (RecyclerView) root.findViewById(R.id.timeSlotListRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(getLayoutManager());

        mAdapter = new TimeSlotCursorRecyclerAdapter(mItemListener, null);
        mRecyclerView.setAdapter(mAdapter);

        // add item animation
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        mRecyclerView.setItemAnimator(itemAnimator);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_time_slots, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_time_slot: {
                mPresenter.addNewTimeSlot();
                return true;
            }
//            case R.id.backup_time_slot_list: {
//                backupTimeSlotList();
//                return true;
//            }
//            case R.id.restore_time_slot_list: {
//                restoreTimeSlotList();
//                return true;
//            }
            default:
                return false;
        }
    }

//    private void backupTimeSlotList() {
//        if (((BaseActivity) getActivity()).isLogin) {
//            new AccessFirebaseAsyn(getContext(), new AccessFirebaseAsyn.BackgroundAction() {
//
//                @Override
//                public void doActionInBackground() {
//                    try {
//                        String encodedUserEmail = PreferenceSupport.getEncodedEmail(getContext());
//                        String authToken = PreferenceSupport.getAuthToken(getContext());
//
//                        FirebaseRestDAO.create().backupTimeSlotItemList(
//                                encodedUserEmail,
//                                authToken,
//                                CHServiceTimeDAO.create(getContext()).backupAllTimeSlots());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void doOnPostExecute() {
//                    Toast.makeText(getContext(), "Backup done.", Toast.LENGTH_SHORT).show();
//                }
//            }).execute();
//        } else {
//            ((BaseActivity) getActivity()).showLoginHint();
//        }
//    }
//
//    private void restoreTimeSlotList() {
//        if (((BaseActivity) getActivity()).isLogin) {
//            new AccessFirebaseAsyn(getContext(), new AccessFirebaseAsyn.BackgroundAction() {
//
//                @Override
//                public void doActionInBackground() {
//                    try {
//                        String encodedUserEmail = PreferenceSupport.getEncodedEmail(getContext());
//                        String authToken = PreferenceSupport.getAuthToken(getContext());
//                        CHServiceTimeDAO.create(getContext()).restoreAllTimeSlots(
//                                FirebaseRestDAO.create().restoreTimeSlotItemList(
//                                        encodedUserEmail, authToken));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void doOnPostExecute() {
//                    Toast.makeText(getContext(), "Restore done.", Toast.LENGTH_SHORT).show();
//                }
//            }).execute();
//        } else {
//            ((BaseActivity) getActivity()).showLoginHint();
//        }
//    }

    /******
     * Preference Listener's method
     ******/
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferenceSupport.NEXT_ALARM_DETAIL)) {
            mNextAlarmTextView.setText(PreferenceSupport.getNextAlarmDetail(getContext()));
        }

    }

    /******
     * Custom RecyclerAdapter Callback interface's implements. It is a listener for operations on TimeSlots
     * in the RecyclerView.
     ******/
    TimeSlotCursorRecyclerAdapter.ItemActionListener mItemListener = new TimeSlotCursorRecyclerAdapter.ItemActionListener() {
        @Override
        public void onItemLongClicked(String timeSlotId) {
            LOGD(TAG, "onItemLongClicked(): timeSlotId=" + timeSlotId);
            mPresenter.openTimeSlotDetail(timeSlotId);
        }

        @Override
        public void onActiveFlagSwitchClicked(String timeSlotId, boolean activeFlag) {
            LOGD(TAG, "onActiveFlagSwitchClicked(): timeSlotId=" + timeSlotId + " ; activeFlag=" + activeFlag);
            TimeSlot timeSlot = new TimeSlot();
            timeSlot.timeSlotId = timeSlotId;
            timeSlot.activationFlag = activeFlag;
            mPresenter.activateTimeSlot(timeSlot);
        }

        @Override
        public void deleteItem(String timeSlotId) {
            LOGD(TAG, "deleteItem(): timeSlotId=" + timeSlotId);
            mPresenter.deleteTimeSlot(timeSlotId);
        }
    };

    // Here is the method we extract to override in our testable subclass
    public LinearLayoutManager getLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    ////// Implements of Contract interface //////

    @Override
    public void setLoadingIndicator(boolean active) {
        if (getView() == null) {
            return;
        }

        // TODO: show indicator.

    }

    @Override
    public void showTimeSlots(Cursor cursor) {
        mAdapter.changeCursor(cursor);

        mRecyclerView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
    }

    @Override
    public void showAddTimeSlotUI() {
        Intent intent = new Intent(getContext(), AddEditTimeSlotActivity.class);
        startActivityForResult(intent, AddEditTimeSlotActivity.REQUEST_ADD_TIME_SLOT);
    }

    @Override
    public void showEditTimeSlotUi(String timeSlotId) {
        // for test. App is busy until further notice
        EspressoIdlingResource.increment();

        // in it's own Activity, since it makes more sense that way and it gives us the flexibility
        // to show some Intent stubbing.
        Intent intent = new Intent(getContext(), AddEditTimeSlotActivity.class);
        intent.putExtra(AddEditTimeSlotFragment.ARGUMENT_EDIT_TIME_SLOT_ID, timeSlotId);
        startActivityForResult(intent, AddEditTimeSlotActivity.REQUEST_ADD_TIME_SLOT);
    }

    @Override
    public void showTimeSlotMarkedActive() {
        showMessage(getString(R.string.timeslot_marked_active));
    }

    @Override
    public void showTimeSlotDeleted() {
        showMessage(getString(R.string.timeslot_deleted));
    }

    @Override
    public void showLoadingTimeSlotsError() {
        showMessage(getString(R.string.loading_timeslots_error));
    }

    @Override
    public void showNoTimeSlots() {
        mRecyclerView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_timeslot));
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(TimeSlotsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }
}
