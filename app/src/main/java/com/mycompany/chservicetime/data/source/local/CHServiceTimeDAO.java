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
public class CHServiceTimeDAO implements TimeSlotDataSource{
    private static final String TAG = makeLogTag("CHServiceTimeDAO");

    private Context mContext;

    private ContentResolver mContentResolver;

    private static CHServiceTimeDAO INSTANCE;

    private CHServiceTimeDAO(Context context) {
        mContext = context;
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

        return  ModelConverter.cursorToTimeSlotModel(cursor, new ColumnIndexCache());
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
            mContext.getContentResolver()
                    .update(TimeSlots.buildTimeSlotUri(timeSlotId), values, null, null);
        }
    }

//    public ArrayList<int[]> getNextAlarmTime(boolean silentFlag) {
//        ArrayList<int[]> timeSectors = new ArrayList<int[]>();
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        int currentDayInWeek = calendar.get(Calendar.DAY_OF_WEEK);
//
//        Cursor cursor = mContext.getContentResolver().query(TimeSlots.buildTimeSlotsUri(),
//                TimeSlots.DEFAULT_PROJECTION,
//                "substr(" + TimeSlots.DAYS + "," + currentDayInWeek + ",1) = ? and "
//                        + TimeSlots.SERVICE_FLAG + " = ? ",
//                new String[]{"1", "1"},
//                TimeSlots.BEGIN_TIME_HOUR + "," + TimeSlots.BEGIN_TIME_MINUTE + ","
//                        + TimeSlots.END_TIME_HOUR + "," + TimeSlots.END_TIME_MINUTE);
//        if (cursor != null) {
//            int beginTimeHour;
//            int beginTimeMinute;
//            int endTimeHour;
//            int endTimeMinute;
//            while (cursor.moveToNext()) {
//                beginTimeHour = cursor.getInt(cursor.getColumnIndex(TimeSlots.BEGIN_TIME_HOUR));
//                beginTimeMinute = cursor
//                        .getInt(cursor.getColumnIndex(TimeSlots.BEGIN_TIME_MINUTE));
//                endTimeHour = cursor.getInt(cursor.getColumnIndex(TimeSlots.END_TIME_HOUR));
//                endTimeMinute = cursor.getInt(cursor.getColumnIndex(TimeSlots.END_TIME_MINUTE));
//                timeSectors.add(new int[]{
//                        beginTimeHour * 100 + beginTimeMinute, endTimeHour * 100 + endTimeMinute});
//            }
//        }
//
//        return timeSectors;
//    }
//
//    public void restoreAllTimeSlots(Collection<TimeSlotItem> timeSlotItems) {
//        String currentTimeSlotId;
//
//        // clear DB
//        deleteAllTimeSlot();
//        for (TimeSlotItem tsItem : timeSlotItems) {
//            // add a timeslot, timeSlotId will be a new value.
//            currentTimeSlotId = createOrUpdateTimeSlot("", tsItem.getName(),
//                    tsItem.getBeginTimeHour(), tsItem.getBeginTimeMinute(),
//                    tsItem.getEndTimeHour(), tsItem.getEndTimeMinute(),
//                    tsItem.getDays(), tsItem.isRepeatFlag());
//            // restore the service flag
//            updateServiceFlag(currentTimeSlotId, tsItem.isServiceFlag());
//        }
//    }
//
//    public ArrayList<TimeSlotItem> backupAllTimeSlots() {
//        // Get all TimeSlot from DB
//        Cursor cursor = CHServiceTimeDAO.create(mContext).getAllTimeSlot();
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
}
