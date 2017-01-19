package com.mycompany.chservicetime.presentation.timeslotlist;

import com.mycompany.chservicetime.model.TimeSlot;

/**
 * Created by szhx on 1/19/2017.
 */

public interface TimeSlotItemListener {

    void onTimeSlotClick(TimeSlot clickedTimeSlot);

    void onCompleteTimeSlotClick(TimeSlot completedTimeSlot);

    void onActivateTimeSlotClick(TimeSlot activatedTimeSlot);
}
