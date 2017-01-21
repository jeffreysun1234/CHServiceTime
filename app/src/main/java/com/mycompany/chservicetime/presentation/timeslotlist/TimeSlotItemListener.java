package com.mycompany.chservicetime.presentation.timeslotlist;

import com.mycompany.chservicetime.CHApplication;
import com.mycompany.chservicetime.service.SchedulingIntentService;

import static android.content.ContentValues.TAG;
import static com.mycompany.chservicetime.util.LogUtils.LOGD;

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
        LOGD(TAG, "deleteItem(): timeSlotId=" + timeSlotId);
        mPresenter.deleteTimeSlot(timeSlotId);
    }

    @Override
    public void editItem(String timeSlotId) {
        LOGD(TAG, "onItemLongClicked(): timeSlotId=" + timeSlotId);
        mPresenter.addEditTimeSlot(timeSlotId);
    }

    @Override
    public void onActiveFlagSwitchClicked(String timeSlotId, boolean activeFlag) {
        LOGD(TAG, "onActiveFlagSwitchClicked(): timeSlotId=" + timeSlotId + " ; activeFlag=" + activeFlag);
        mPresenter.activateTimeSlot(timeSlotId, activeFlag);
        // Send the open and close sound alarms based on the current data.
        SchedulingIntentService.startActionSetAlarm(CHApplication.getContext());
    }
}
