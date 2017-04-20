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

import android.support.annotation.NonNull;

import com.mycompany.chservicetime.model.TimeSlot;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * Implementation of a data source with static access to the data for easy testing.
 */
public class FakeAppDataSource implements AppDataSource {

    private static FakeAppDataSource INSTANCE;

    private static final Map<String, TimeSlot> TIMESLOT_DATA = new LinkedHashMap<>();

    // Prevent direct instantiation.
    private FakeAppDataSource() {
    }

    public static FakeAppDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakeAppDataSource();
        }
        return INSTANCE;
    }

    @Override
    public Observable<List<TimeSlot>> getAllTimeSlot() {
        Collection<TimeSlot> values = TIMESLOT_DATA.values();
        return Observable.from(values).toList();
    }

    @Override
    public Observable<TimeSlot> getTimeSlot(@NonNull String timeSlotId) {
        TimeSlot timeSlot = TIMESLOT_DATA.get(timeSlotId);
        return Observable.just(timeSlot);
    }

    @Override
    public void saveTimeSlot(TimeSlot timeSlot) {
        TIMESLOT_DATA.put(timeSlot._id(), timeSlot);
    }

    @Override
    public int updateActivationFlag(@NonNull String timeSlotId, boolean activationFlag) {
        TimeSlot oldTimeSlot = TIMESLOT_DATA.get(timeSlotId);
        TimeSlot newTimeSlot = oldTimeSlot.toBuilder().activation_flag(activationFlag).build();
        TIMESLOT_DATA.put(timeSlotId, newTimeSlot);
        return 1;
    }

    @Override
    public void deleteTimeSlot(String timeSlotId) {
        TIMESLOT_DATA.remove(timeSlotId);
    }

    @Override
    public void deleteAllTimeSlot() {
        TIMESLOT_DATA.clear();
    }

    @Override
    public void refreshTimeSlots() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void addTimeSlots(TimeSlot... timeSlots) {
        for (TimeSlot timeSlot : timeSlots) {
            TIMESLOT_DATA.put(timeSlot._id(), timeSlot);
        }
    }
}
