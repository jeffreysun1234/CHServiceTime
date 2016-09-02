package com.mycompany.chservicetime.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.mycompany.chservicetime.data.source.local.CHServiceTimeContract.TimeSlots;
import com.mycompany.chservicetime.data.source.local.helper.TimeSlotContentValues;

import static com.mycompany.chservicetime.util.LogUtils.makeLogTag;

/**
 * Created by szhx on 3/24/2016.
 */
public class ModelConverter {
    private static final String TAG = makeLogTag("ModelConverter");

    public static TimeSlot cursorToTimeSlotModel(final Cursor cursor,
                                                 final ColumnIndexCache mColumnIndexCache) {
        TimeSlot timeSlotItem = new TimeSlot(
                cursor.getInt(mColumnIndexCache.getColumnIndex(cursor, TimeSlots._ID)),
                cursor.getString(mColumnIndexCache.getColumnIndex(cursor, TimeSlots.TIME_SLOT_ID)),
                cursor.getString(mColumnIndexCache.getColumnIndex(cursor, TimeSlots.NAME)),
                cursor.getString(mColumnIndexCache.getColumnIndex(cursor, TimeSlots.DESCRIPTION)),
                cursor.getInt(mColumnIndexCache.getColumnIndex(cursor, TimeSlots.BEGIN_TIME_HOUR)),
                cursor.getInt(mColumnIndexCache.getColumnIndex(cursor, TimeSlots.BEGIN_TIME_MINUTE)),
                cursor.getInt(mColumnIndexCache.getColumnIndex(cursor, TimeSlots.END_TIME_HOUR)),
                cursor.getInt(mColumnIndexCache.getColumnIndex(cursor, TimeSlots.END_TIME_MINUTE)),
                cursor.getString(mColumnIndexCache.getColumnIndex(cursor, TimeSlots.DAYS)),
                cursor.getInt(mColumnIndexCache.getColumnIndex(cursor, TimeSlots.REPEAT_FLAG)) == 1 ? true : false,
                cursor.getInt(mColumnIndexCache.getColumnIndex(cursor, TimeSlots.ACTIVATION_FLAG)) == 1 ? true : false,
                cursor.getLong(mColumnIndexCache.getColumnIndex(cursor, TimeSlots.UPDATED_TIMESTAMP))
        );

        return timeSlotItem;
    }

    /**
     * @param timeSlot If a field is null, the field will NOT be added to the ContentValues.
     * @return
     */
    public static ContentValues timeSlotToContentValues(TimeSlot timeSlot) {
        TimeSlotContentValues timeSlotContentValues = new TimeSlotContentValues();
        timeSlotContentValues.putTimeSlotId(timeSlot.timeSlotId);
        timeSlotContentValues.putName(timeSlot.name);
        timeSlotContentValues.putDescription(timeSlot.description);
        timeSlotContentValues.putBeginTimeHour(timeSlot.beginTimeHour);
        timeSlotContentValues.putBeginTimeMinute(timeSlot.beginTimeMinute);
        timeSlotContentValues.putEndTimeHour(timeSlot.endTimeHour);
        timeSlotContentValues.putEndTimeMinute(timeSlot.endTimeMinute);
        timeSlotContentValues.putDays(timeSlot.days);
        timeSlotContentValues.putRepeatFlag(timeSlot.repeatFlag);
        timeSlotContentValues.putActivationFlag(timeSlot.activationFlag);
        timeSlotContentValues.putUpdatedTimestamp(System.currentTimeMillis());

        return timeSlotContentValues.values();
    }
}
