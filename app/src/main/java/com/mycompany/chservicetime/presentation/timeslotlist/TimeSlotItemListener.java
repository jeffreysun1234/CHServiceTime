package com.mycompany.chservicetime.presentation.timeslotlist;

import com.mycompany.chservicetime.util.CHLog;

import static android.content.ContentValues.TAG;

/**
 * Created by szhx on 1/19/2017.
 */

public class TimeSlotItemListener implements ItemActionListenerInterface {

    TimeSlotListPresenter mPresenter;

    public TimeSlotItemListener(TimeSlotListPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onTimeSlotClick(String timeSlotId) {

    }

    @Override
    public void onItemLongClicked(String timeSlotId) {
        //editItem(timeSlotId);
    }

    @Override
    public void deleteItem(String timeSlotId) {
        CHLog.d(TAG, "deleteItem(): timeSlotId=" + timeSlotId);
        mPresenter.deleteTimeSlot(timeSlotId);
    }

    @Override
    public void editItem(String timeSlotId) {
        CHLog.d(TAG, "onItemLongClicked(): timeSlotId=" + timeSlotId);
        mPresenter.addEditTimeSlot(timeSlotId);
    }

    @Override
    public void onActiveFlagSwitchClicked(String timeSlotId, boolean activeFlag) {
        CHLog.d(TAG, "onActiveFlagSwitchClicked(): timeSlotId=" + timeSlotId + " ; activeFlag=" + activeFlag);
        mPresenter.activateTimeSlot(timeSlotId, activeFlag);
    }
}
