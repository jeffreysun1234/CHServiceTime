package com.mycompany.chservicetime.business.data;

/**
 * Created by szhx on 1/16/2017.
 */

public class BackupAndRestore {

//    public ArrayList<TimeSlotItem> backupAllTimeSlots() {
//        // Get all TimeSlot from DB
//        Cursor cursor = mTimeSlotDataSource.getAllTimeSlot();
//        if (cursor == null)
//            return null;
//
//        ArrayList<TimeSlotItem> timeSlotItems = new ArrayList<TimeSlotItem>();
//
//        ColumnIndexCache columnIndexCache = new ColumnIndexCache();
//        TimeSlotItem tsItem;
//
//        cursor.moveToPosition(-1);
//        while (cursor.moveToNext()) {
//            // convert cursor to TimeSlotItem model
//            tsItem = ModelConverter.cursorToTimeSlotItem(cursor, columnIndexCache);
//
//            timeSlotItems.add(tsItem);
//        }
//
//        cursor.close();
//
//        return timeSlotItems;
//    }
//
//    public void restoreAllTimeSlots(Collection<TimeSlotItem> timeSlotItems) {
//        String currentTimeSlotId;
//
//        // clear DB
//        deleteAllTimeSlot();
//        for (TimeSlotItem tsItem : timeSlotItems) {
//            // add a timeslot, timeSlotId will be a new value.
//            currentTimeSlotId = saveTimeSlot(ModelConverter.firebaseTimeSlotItemToTimeSlot(tsItem));
//        }
//    }
}
