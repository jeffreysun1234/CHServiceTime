package com.mycompany.chservicetime.data.source.local;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.mycompany.chservicetime.data.source.TimeSlotDataSource;
import com.mycompany.chservicetime.data.source.local.CHServiceTimeContract.TimeSlots;
import com.mycompany.chservicetime.model.ColumnIndexCache;
import com.mycompany.chservicetime.model.ModelConverter;
import com.mycompany.chservicetime.model.TimeSlot;

import static com.mycompany.chservicetime.util.LogUtils.LOGD;
import static com.mycompany.chservicetime.util.LogUtils.makeLogTag;


/**
 * Created by szhx on 11/10/2015.
 */
public class CHServiceTimeDAO implements TimeSlotDataSource {
    private static final String TAG = makeLogTag("CHServiceTimeDAO");

    private ContentResolver mContentResolver;

    private static CHServiceTimeDAO INSTANCE;

    private CHServiceTimeDAO(Context context) {
        mContentResolver = context.getApplicationContext().getContentResolver();
    }

    public static CHServiceTimeDAO getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new CHServiceTimeDAO(context);
        }
        return INSTANCE;
    }

    /**
     * If the timeSlotId field of the timeSlot parameter is empty, do Insert operation.
     * If the timeSlotId field of the timesSlot parameter is NOT empty, do Update operation.
     *
     * @return timeSlotId
     */
    @Override
    public String createOrUpdateTimeSlot(TimeSlot timeSlot) {
        String returnTimeSlotId;

        ContentValues values = ModelConverter.timeSlotToContentValues(timeSlot);

        if (TextUtils.isEmpty(timeSlot.timeSlotId)) {
            returnTimeSlotId = TimeSlots.generateTimeSlotId();
            values.put(TimeSlots.TIME_SLOT_ID, returnTimeSlotId);
            mContentResolver.insert(TimeSlots.buildTimeSlotsUri(), values);
        } else {
            returnTimeSlotId = timeSlot.timeSlotId;
            mContentResolver.update(
                    TimeSlots.buildTimeSlotUri(timeSlot.timeSlotId),
                    values, null, null);
        }

        return returnTimeSlotId;
    }

    @Override
    public void deleteTimeSlot(String timeSlotId) {
        mContentResolver.delete(TimeSlots.buildTimeSlotUri(timeSlotId), null, null);
    }

    @Override
    public void deleteAllTimeSlot() {
        mContentResolver.delete(TimeSlots.buildTimeSlotsUri(), null, null);
    }

    @Override
    public Cursor getAllTimeSlot() {
        Cursor cursor = mContentResolver.query(TimeSlots.CONTENT_URI,
                null, null, null, null);
        return cursor;
    }

    /**
     * @param timeSlotId
     * @return null if not found.
     */
    @Override
    public TimeSlot getTimeSlot(String timeSlotId) {
        TimeSlot timeSlot = new TimeSlot();
        Cursor cursor = mContentResolver.query(TimeSlots.buildTimeSlotUri(timeSlotId),
                null, null, null, null);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();

        return ModelConverter.cursorToTimeSlotModel(cursor, new ColumnIndexCache());
    }

    @Override
    public void getAllTimeSlot(@NonNull LoadTimeSlotsCallback callback) {
        // no-op since the data is loaded via Cursor Loader
    }

    @Override
    public void getTimeSlot(@NonNull String timeSlotId, @NonNull GetTimeSlotCallback callback) {
        // no-op since the data is loaded via Cursor Loader
    }

    @Override
    public void updateActivationFlag(@NonNull String timeSlotId, boolean activationFlag) {
        LOGD(TAG, "activateTimeSlot: timeSlotId=" + timeSlotId + ", activationFlag=" + activationFlag);
        ContentValues values = new ContentValues();
        values.put(TimeSlots.ACTIVATION_FLAG, activationFlag);

        if (!TextUtils.isEmpty(timeSlotId)) {
            mContentResolver.update(TimeSlots.buildTimeSlotUri(timeSlotId), values, null, null);
        }
    }
}
