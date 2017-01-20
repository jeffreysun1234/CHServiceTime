package com.mycompany.chservicetime.presentation.timeslotlist;

/**
 * Created by szhx on 1/19/2017.
 */

public interface ItemActionListenerInterface {

    void onTimeSlotClick(String timeSlotId);

    void onItemLongClicked(String timeSlotId);

    void deleteItem(String timeSlotId);

    void editItem(String timeSlotId);

    void onActiveFlagSwitchClicked(String timeSlotId, boolean activeFlag);
}
