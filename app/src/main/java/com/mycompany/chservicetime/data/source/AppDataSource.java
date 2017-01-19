package com.mycompany.chservicetime.data.source;

import android.support.annotation.NonNull;

import com.mycompany.chservicetime.model.TimeSlot;

import java.util.List;

import rx.Observable;


/**
 * Main entry point for accessing TimeSlot data.
 */
public interface AppDataSource {

    Observable<List<TimeSlot>> getAllTimeSlot();

    Observable<TimeSlot> getTimeSlot(@NonNull String id);

    void saveTimeSlot(@NonNull TimeSlot timeSlot);

    int updateActivationFlag(@NonNull String id, boolean activationFlag);

    void deleteAllTimeSlot();

    void deleteTimeSlot(@NonNull String id);

    void refreshTimeSlots();

    void addTimeSlots(TimeSlot... timeSlots);
}
