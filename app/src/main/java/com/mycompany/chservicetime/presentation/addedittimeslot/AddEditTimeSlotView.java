package com.mycompany.chservicetime.presentation.addedittimeslot;

import com.mycompany.chservicetime.model.TimeSlot;

import net.grandcentrix.thirtyinch.TiView;
import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;

/**
 * Created by szhx on 1/15/2017.
 */

public interface AddEditTimeSlotView extends TiView {

    @CallOnMainThread
    void showError(int error);

    @CallOnMainThread
    void showTimeSlotList();

    @CallOnMainThread
    void setTimeSlotFields(TimeSlot timeSlot);
}
