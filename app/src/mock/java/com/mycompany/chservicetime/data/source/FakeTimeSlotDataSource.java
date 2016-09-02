/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mycompany.chservicetime.data.source;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.mycompany.chservicetime.model.TimeSlot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of a data source with static access to the data for easy testing.
 */
public class FakeTimeSlotDataSource implements TimeSlotDataSource {

    private static FakeTimeSlotDataSource INSTANCE;

    private static final Map<String, TimeSlot> TIMESLOT_DATA = new LinkedHashMap<>();

    // Prevent direct instantiation.
    private FakeTimeSlotDataSource() {
    }

    public static FakeTimeSlotDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakeTimeSlotDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void getAllTimeSlot(@NonNull LoadTimeSlotsCallback callback) {
       callback.onTimeSlotsLoaded(MockCursorProvider.createAllTimeSlotsCursorFromList(
               new ArrayList<TimeSlot>(TIMESLOT_DATA.values())));
    }

    @Override
    public void getTimeSlot(@NonNull String timeSlotId, @NonNull GetTimeSlotCallback callback) {
        TimeSlot timeSlot = TIMESLOT_DATA.get(timeSlotId);
        callback.onTimeSlotLoaded(timeSlot);
    }

    @Override
    public Cursor getAllTimeSlot() {
        return MockCursorProvider.createAllTimeSlotsCursorFromList(
                new ArrayList<TimeSlot>(TIMESLOT_DATA.values()));
    }

    @Override
    public TimeSlot getTimeSlot(@NonNull String timeSlotId) {
        return TIMESLOT_DATA.get(timeSlotId);
    }

    @Override
    public String createOrUpdateTimeSlot(TimeSlot timeSlot) {
        TIMESLOT_DATA.put(timeSlot.timeSlotId, timeSlot);
        return  timeSlot.timeSlotId;
    }

    @Override
    public void updateActivationFlag(@NonNull String timeSlotId, boolean activationFlag) {
        TimeSlot oldTimeSlot = TIMESLOT_DATA.get(timeSlotId);
        oldTimeSlot.activationFlag = activationFlag;
        TIMESLOT_DATA.put(timeSlotId, oldTimeSlot);
    }

    @Override
    public void deleteTimeSlot(String timeSlotId) {
        TIMESLOT_DATA.remove(timeSlotId);
    }

    @Override
    public void deleteAllTimeSlot() {
        TIMESLOT_DATA.clear();
    }

    @VisibleForTesting
    public void addTimeSlots(TimeSlot... timeSlots) {
        for (TimeSlot timeSlot : timeSlots) {
            TIMESLOT_DATA.put(timeSlot.timeSlotId, timeSlot);
        }
    }
}
