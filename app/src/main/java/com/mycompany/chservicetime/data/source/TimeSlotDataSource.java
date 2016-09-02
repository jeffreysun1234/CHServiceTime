package com.mycompany.chservicetime.data.source;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.mycompany.chservicetime.model.TimeSlot;


/**
 * Main entry point for accessing TimeSlot data.
 * <p/>
 * For simplicity, only getAllTimeSlot() and getTimeSlot() have callbacks. Consider adding callbacks to other
 * methods to inform the user of network/database errors or successful operations.
 * For example, when a new TimeSlot is created, it's synchronously stored in cache but usually every
 * operation on database or network should be executed in a different thread.
 */
public interface TimeSlotDataSource {
    interface LoadTimeSlotsCallback {

        void onTimeSlotsLoaded(Cursor timeSlots);

        void onDataNotAvailable();
    }

    interface GetTimeSlotCallback {

        void onTimeSlotLoaded(TimeSlot timeSlot);

        void onDataNotAvailable();
    }

    void getAllTimeSlot(@NonNull LoadTimeSlotsCallback callback);

    void getTimeSlot(@NonNull String timeSlotId, @NonNull GetTimeSlotCallback callback);

    Cursor getAllTimeSlot();

    TimeSlot getTimeSlot(@NonNull String timeSlotId);

    String createOrUpdateTimeSlot(@NonNull TimeSlot timeSlot);

    void updateActivationFlag(@NonNull String timeSlotId, boolean activationFlag);

    void deleteAllTimeSlot();

    void deleteTimeSlot(@NonNull String timeSlotId);
}
