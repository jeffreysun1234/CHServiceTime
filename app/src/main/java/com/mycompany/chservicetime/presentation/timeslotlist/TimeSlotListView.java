package com.mycompany.chservicetime.presentation.timeslotlist;

import android.support.annotation.StringRes;

import com.mycompany.chservicetime.model.TimeSlot;

import net.grandcentrix.thirtyinch.TiView;

import java.util.List;

/**
 * Created by szhx on 12/10/2016.
 */

public interface TimeSlotListView extends TiView {

    void setLoadingIndicator(boolean active);

    void showTimeSlots(List<TimeSlot> timeSlots);

    void showAddEditTimeSlot(String id);

    void showLoadingTimeSlotsError();

    void showNoTimeSlots();

    void showSuccessfullySavedMessage();

    void showTimeSlotActivationFlagMessage(boolean activationFlag);

    void showTimeSlotsClearedMessage();

    void showTimeSlotDeletedMessage();

    void showFeedbackMessage(@StringRes int messageRes);
}
