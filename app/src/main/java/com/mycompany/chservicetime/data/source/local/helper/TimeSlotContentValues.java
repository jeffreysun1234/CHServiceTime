package com.mycompany.chservicetime.data.source.local.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mycompany.chservicetime.data.source.local.CHServiceTimeContract.TimeSlots;

/**
 * Content values wrapper for the {@code time_slot} table.
 */
public class TimeSlotContentValues extends AbstractContentValues {

    public TimeSlotContentValues putTimeSlotId(String value) {
        if (value != null)
            mContentValues.put(TimeSlots.TIME_SLOT_ID, value);
        return this;
    }

    public TimeSlotContentValues putName(String value) {
        if (value != null)
            mContentValues.put(TimeSlots.NAME, value);
        return this;
    }


    public TimeSlotContentValues putDescription(String value) {
        if (value != null)
            mContentValues.put(TimeSlots.DESCRIPTION, value);
        return this;
    }

    public TimeSlotContentValues putBeginTimeHour(Integer value) {
        if (value != null)
            mContentValues.put(TimeSlots.BEGIN_TIME_HOUR, value);
        return this;
    }


    public TimeSlotContentValues putBeginTimeMinute(Integer value) {
        if (value != null)
            mContentValues.put(TimeSlots.BEGIN_TIME_MINUTE, value);
        return this;
    }


    public TimeSlotContentValues putEndTimeHour(Integer value) {
        if (value != null)
            mContentValues.put(TimeSlots.END_TIME_HOUR, value);
        return this;
    }


    public TimeSlotContentValues putEndTimeMinute(Integer value) {
        if (value != null)
            mContentValues.put(TimeSlots.END_TIME_MINUTE, value);
        return this;
    }


    public TimeSlotContentValues putRepeatFlag(Boolean value) {
        if (value != null)
            mContentValues.put(TimeSlots.REPEAT_FLAG, value);
        return this;
    }


    public TimeSlotContentValues putActivationFlag(Boolean value) {
        if (value != null)
            mContentValues.put(TimeSlots.ACTIVATION_FLAG, value);
        return this;
    }


    public TimeSlotContentValues putDays(String value) {
        if (value != null)
            mContentValues.put(TimeSlots.DAYS, value);
        return this;
    }


    public TimeSlotContentValues putUpdatedTimestamp(long value) {
        mContentValues.put(TimeSlots.UPDATED_TIMESTAMP, value);
        return this;
    }

}
