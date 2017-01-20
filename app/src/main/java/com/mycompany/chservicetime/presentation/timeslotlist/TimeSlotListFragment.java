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
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.model.TimeSlot;
import com.mycompany.chservicetime.presentation.addedittimeslot.AddEditTimeSlotActivity;
import com.mycompany.chservicetime.presentation.addedittimeslot.AddEditTimeSlotFragment;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Display a grid of {@link TimeSlot}s. User can choose to view all, active or completed timeSlots.
 */
public class TimeSlotListFragment extends Fragment implements TimeSlotListView {

    View mNoTimeSlotsView;
    ImageView mNoTimeSlotIcon;
    TextView mNoTimeSlotMainView;
    TextView mNoTimeSlotAddView;
    LinearLayout mTimeSlotsView;

    TimeSlotListAdapter mListAdapter;
    TimeSlotListPresenter mTimeSlotListPresenter;

    /**
     * Listener for clicks on timeSlots in the ListView.
     */
    ItemActionListenerInterface mItemListener;

    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.item_height);

            // MATCH_PARENT 自适应高度，保持和内容一样高；也可以指定菜单具体高度，也可以用WRAP_CONTENT。
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem editItem = new SwipeMenuItem(getActivity())
                        .setBackgroundDrawable(R.drawable.selector_green)
                        .setImage(R.mipmap.ic_action_edit)
                        .setText("Edit") // 文字，还可以设置文字颜色，大小等。。
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(editItem);// 添加一个按钮到右侧侧菜单。

                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity())
                        .setBackgroundDrawable(R.drawable.selector_red)
                        .setImage(R.mipmap.ic_action_delete)
                        .setText("Delete") // 文字，还可以设置文字颜色，大小等。。
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。
            }
        }
    };

    /**
     * 菜单点击监听。
     */
    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {
        /**
         * Item的菜单被点击的时候调用。
         * @param closeable       closeable. 用来关闭菜单。
         * @param adapterPosition adapterPosition. 这个菜单所在的item在Adapter中position。
         * @param menuPosition    menuPosition. 这个菜单的position。比如你为某个Item创建了2个MenuItem，那么这个position可能是是 0、1，
         * @param direction       如果是左侧菜单，值是：SwipeMenuRecyclerView#LEFT_DIRECTION，如果是右侧菜单，值是：SwipeMenuRecyclerView#RIGHT_DIRECTION.
         */
        @Override
        public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
            closeable.smoothCloseMenu();// 关闭被点击的菜单。

            if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                Toast.makeText(getActivity(), "list第" + adapterPosition + "; In the right menu, 第" + menuPosition, Toast.LENGTH_SHORT).show();
            }

            // TODO 这里特别注意，如果这里删除了Item，不要调用Adapter.notifyItemRemoved(position)，因为RecyclerView有个bug，调用这个方法后，后面的position会错误！
            // TODO 删除Item后调用Adapter.notifyDataSetChanged()，下面是事例代码：
            if (menuPosition == 0) {// click Edit button
                mItemListener.editItem(mListAdapter.mTimeSlots.get(adapterPosition)._id());
                //mListAdapter.notifyDataSetChanged();
            }
            if (menuPosition == 1) {// click Delete button
                mItemListener.deleteItem(mListAdapter.mTimeSlots.get(adapterPosition)._id());
            }
        }
    };

    public TimeSlotListFragment() {
        // Requires empty public constructor
    }

    public static TimeSlotListFragment newInstance() {
        return new TimeSlotListFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getPresenter().result(requestCode, resultCode);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.time_slot_list_frag, container, false);
        initViews(root);
        return root;
    }

    private void initViews(View root) {
        // Set up  no timeSlots view
        mNoTimeSlotsView = root.findViewById(R.id.noTimeSlots);
        mNoTimeSlotIcon = (ImageView) root.findViewById(R.id.noTimeSlotsIcon);
        mNoTimeSlotMainView = (TextView) root.findViewById(R.id.noTimeSlotsMain);
        mNoTimeSlotAddView = (TextView) root.findViewById(R.id.noTimeSlotsAdd);
        mNoTimeSlotAddView.setOnClickListener(v -> showAddEditTimeSlot(null));

        // Set up timeSlots view
        SwipeMenuRecyclerView listView = (SwipeMenuRecyclerView) root.findViewById(R.id.timeslot_list);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // add item animation
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        listView.setItemAnimator(itemAnimator);
        // set a swiping menu
        listView.setSwipeMenuCreator(swipeMenuCreator);
        // set a listener for the swiping menu
        listView.setSwipeMenuItemClickListener(menuItemClickListener);

        // Inject Presenter to ItemListener. At this position, the presenter should be instanced.
        mItemListener = new TimeSlotItemListener(((TimeSlotListActivity) getActivity()).timeSlotListPresenter);

        // set an Adapter
        mListAdapter = new TimeSlotListAdapter(new ArrayList<TimeSlot>(0), mItemListener);
        listView.setAdapter(mListAdapter);

        mTimeSlotsView = (LinearLayout) root.findViewById(R.id.timeslotsLL);

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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.timeslots_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_time_slot: {
                getPresenter().addEditTimeSlot(null);
                return true;
            }
            case R.id.menu_clear:
                getPresenter().clearTimeSlots();
                return true;
            case R.id.menu_refresh:
                getPresenter().loadTimeSlots(true);
                return true;
            case R.id.backup_time_slot_list: {
                //getPresenter().backupTimeSlotList();
                return true;
            }
            case R.id.restore_time_slot_list: {
                //getPresenter().restoreTimeSlotList();
                return true;
            }
        }
        return false;
    }

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

    @Override
    public void showTimeSlotDeletedMessage() {
        showMessage(getString(R.string.timeslot_deleted));
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
        intent.putExtra(AddEditTimeSlotFragment.ARGUMENT_EDIT_TIME_SLOT_ID, id);
        startActivityForResult(intent, AddEditTimeSlotActivity.REQUEST_ADD_TIME_SLOT);
    }

    @Override
    public void showLoadingTimeSlotsError() {
        showMessage(getString(R.string.loading_timeslots_error));
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    public TimeSlotListPresenter getPresenter() {
        return mTimeSlotListPresenter;
    }

    public void setPresenter(@NonNull TimeSlotListPresenter timeSlotListPresenter) {
        mTimeSlotListPresenter = checkNotNull(timeSlotListPresenter);
    }
}
