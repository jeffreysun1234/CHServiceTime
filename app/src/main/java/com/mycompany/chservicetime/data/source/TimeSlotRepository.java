package com.mycompany.chservicetime.data.source;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.mycompany.chservicetime.data.firebase.model.TimeSlotItem;
import com.mycompany.chservicetime.data.source.local.CHServiceTimeContract;
import com.mycompany.chservicetime.data.source.local.CHServiceTimeContract.TimeSlots;
import com.mycompany.chservicetime.data.source.local.CHServiceTimeDAO;
import com.mycompany.chservicetime.model.ColumnIndexCache;
import com.mycompany.chservicetime.model.ModelConverter;
import com.mycompany.chservicetime.model.TimeSlot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by szhx on 5/1/2016.
 * <p>
 * Concrete implementation to load TimeSlots from the data sources.
 */
public class TimeSlotRepository {

    private static TimeSlotRepository INSTANCE = null;

    private final TimeSlotDataSource mTimeSlotDataSource;

    // Prevent direct instantiation.
    private TimeSlotRepository(@NonNull TimeSlotDataSource timeSlotDataSource) {
        mTimeSlotDataSource = timeSlotDataSource;
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param timeSlotDataSource the backend data source
     * @return the {@link TimeSlotRepository} instance
     */
    public static TimeSlotRepository getInstance(TimeSlotDataSource timeSlotDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new TimeSlotRepository(timeSlotDataSource);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(TimeSlotDataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Gets TimeSlots from local data source (SQLite).
     * <p>
     * Note: {@link TimeSlotDataSource.LoadTimeSlotsCallback#onDataNotAvailable()} is fired if the data sources fail to
     * get the data.
     */
    public void getAllTimeSlot(@NonNull TimeSlotDataSource.LoadTimeSlotsCallback callback) {
        try {
            callback.onTimeSlotsLoaded(mTimeSlotDataSource.getAllTimeSlot());
        } catch (Exception e) {
            e.printStackTrace();
            callback.onDataNotAvailable();
        }
    }

    public Cursor getAllTimeSlot() {
        return mTimeSlotDataSource.getAllTimeSlot();
    }

    /**
     * Gets TimeSlot from local data source (sqlite).
     * <p>
     * Note: {@link TimeSlotDataSource.GetTimeSlotCallback#onDataNotAvailable()} is fired if data sources fail to
     * get the data.
     */
    public void getTimeSlot(@NonNull String timeSlotId, @NonNull TimeSlotDataSource.GetTimeSlotCallback callback) {
        try {
            callback.onTimeSlotLoaded(mTimeSlotDataSource.getTimeSlot(timeSlotId));
        } catch (Exception e) {
            e.printStackTrace();
            callback.onDataNotAvailable();
        }
    }

    /**
     * The function will automatically decide if Insert or Update the data.
     *
     * @param timeSlot
     */
    public String createOrUpdateTimeSlot(@NonNull TimeSlot timeSlot) {
        return mTimeSlotDataSource.createOrUpdateTimeSlot(timeSlot);
    }

    public void updateActivationFlag(@NonNull String timeSlotId, boolean activationFlag) {
        mTimeSlotDataSource.updateActivationFlag(timeSlotId, activationFlag);
    }

    public void deleteAllTimeSlot() {
        mTimeSlotDataSource.deleteAllTimeSlot();
    }

    public void deleteTimeSlot(@NonNull String timeSlotId) {
        mTimeSlotDataSource.deleteTimeSlot(timeSlotId);
    }

    /**
     * @param currentDayInWeek Sunday is 1, Monday is 2, ... , Saturday is 7<p>
     *                         Code example: <p>
     *                         Calendar calendar = Calendar.getInstance();<p>
     *                         calendar.setTimeInMillis(System.currentTimeMillis());<p>
     *                         int currentDayInWeek = calendar.get(Calendar.DAY_OF_WEEK);<p>
     * @param activationFlag
     * @return a array list of begin time ( array index is 0) and end time (array index is 1).
     * Time is in 24 hour format.
     */
    public ArrayList<int[]> getRequiredTimeSlots(int currentDayInWeek, boolean activationFlag) {
        ArrayList<int[]> timeSectors = new ArrayList<int[]>();

        ColumnIndexCache mColumnIndexCache = new ColumnIndexCache();

        // Get all time slots.
        Cursor cursor = mTimeSlotDataSource.getAllTimeSlot();
        if (cursor != null) {
            TimeSlot vTimeSlot;
            int vBeginTime;
            int vEndTime;

            while (cursor.moveToNext()) {
                vTimeSlot = ModelConverter.cursorToTimeSlotModel(cursor, mColumnIndexCache);

                // Filter by day and activationFlag
                if ('1' == vTimeSlot.days.charAt(currentDayInWeek - 1) // currentDayInWeek from 1 to 7
                        && vTimeSlot.activationFlag.booleanValue() == activationFlag) {

                    vBeginTime = vTimeSlot.beginTimeHour * 100 + vTimeSlot.beginTimeMinute;
                    vEndTime = vTimeSlot.endTimeHour * 100 + vTimeSlot.endTimeMinute;

                    // Format time. If begin_time > end_time, then end_time + 24 hours. It means next day.
                    if (vBeginTime > vEndTime) {
                        vEndTime = vEndTime + 2400;
                    }

                    timeSectors.add(new int[]{vBeginTime, vEndTime});
                }

                // Get overnight TimeSlots and filter by activationFlag
                if ('1' == vTimeSlot.days.charAt((currentDayInWeek - 2) == -1 ? 6 : currentDayInWeek - 2)
                        // currentDayInWeek from 1 to 7
                        && vTimeSlot.activationFlag.booleanValue() == activationFlag) {

                    vBeginTime = vTimeSlot.beginTimeHour * 100 + vTimeSlot.beginTimeMinute;
                    vEndTime = vTimeSlot.endTimeHour * 100 + vTimeSlot.endTimeMinute;

                    // Format time. Begin_time is from 0am.
                    if (vBeginTime > vEndTime) {
                        vBeginTime = 0;
                        vEndTime = vTimeSlot.endTimeHour * 100 + vTimeSlot.endTimeMinute;

                        timeSectors.add(new int[]{vBeginTime, vEndTime});
                    }
                }
            }
        }

        // Sort by begin time and end time
        Collections.sort(timeSectors, new Comparator<int[]>() {
            @Override
            public int compare(int[] lhs, int[] rhs) {
                if (lhs[0] < rhs[0] || (lhs[0] == rhs[0] && lhs[1] < rhs[1])) {
                    return -1;
                } else if (lhs[0] > rhs[0] || (lhs[0] == rhs[0] && lhs[1] > rhs[1])) {
                    return 1;
                }
                return 0;
            }
        });

        return timeSectors;
    }

    public ArrayList<TimeSlotItem> backupAllTimeSlots() {
        // Get all TimeSlot from DB
        Cursor cursor = mTimeSlotDataSource.getAllTimeSlot();
        if (cursor == null)
            return null;

        ArrayList<TimeSlotItem> timeSlotItems = new ArrayList<TimeSlotItem>();

        ColumnIndexCache columnIndexCache = new ColumnIndexCache();
        TimeSlotItem tsItem;

        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            // convert cursor to TimeSlotItem model
            tsItem = ModelConverter.cursorToTimeSlotItem(cursor, columnIndexCache);

            timeSlotItems.add(tsItem);
        }

        cursor.close();

        return timeSlotItems;
    }

    public void restoreAllTimeSlots(Collection<TimeSlotItem> timeSlotItems) {
        String currentTimeSlotId;

        // clear DB
        deleteAllTimeSlot();
        for (TimeSlotItem tsItem : timeSlotItems) {
            // add a timeslot, timeSlotId will be a new value.
            currentTimeSlotId = createOrUpdateTimeSlot(ModelConverter.firebaseTimeSlotItemToTimeSlot(tsItem));
        }
    }

}
