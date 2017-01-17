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

package com.mycompany.chservicetime.data.source.local;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.mycompany.chservicetime.data.source.AppDataSource;
import com.mycompany.chservicetime.model.TimeSlot;
import com.mycompany.chservicetime.util.schedulers.BaseSchedulerProvider;
import com.mycompany.chservicetime.util.schedulers.ImmediateSchedulerProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import rx.observers.TestSubscriber;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Integration test for the {@link AppDataSource}, which uses the {@link AppDatabaseOpenHelper}.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AppLocalDataSourceTest {

    private BaseSchedulerProvider mSchedulerProvider;

    private AppLocalDataSource mLocalDataSource;

    @Before
    public void setup() {
        AppLocalDataSource.destroyInstance();
        mSchedulerProvider = new ImmediateSchedulerProvider();

        mLocalDataSource = AppLocalDataSource.getInstance(
                InstrumentationRegistry.getTargetContext(), mSchedulerProvider);
    }

    //@After
    public void cleanUp() {
        mLocalDataSource.deleteAllTimeSlot();
    }

    @Test
    public void testPreConditions() {
        assertNotNull(mLocalDataSource);
    }

    @Test
    public void saveTimeSlot_retrievesTimeSlot() {
        // Given a new timeSlot
        final TimeSlot newTimeSlot = TimeSlot.createTimeSlot("111", "Work", "work time",
                9, 0, 17, 0, "0111110", true, false, TimeSlot.ServiceOption.NORMAL);

        // When saved into the persistent repository
        mLocalDataSource.saveTimeSlot(newTimeSlot);

        // Then the timeSlot can be retrieved from the persistent repository
        TestSubscriber<TimeSlot> testSubscriber = new TestSubscriber<>();
        mLocalDataSource.getTimeSlot(newTimeSlot._id()).subscribe(testSubscriber);
        testSubscriber.assertValue(newTimeSlot);
    }

    @Test
    public void activateTimeSlot_retrievedTimeSlotIsActive() {
        // Given a new timeSlot in the persistent repository
        final TimeSlot newTimeSlot = TimeSlot.createTimeSlot("111", "Work", "work time",
                9, 0, 17, 0, "0111110", true, false, TimeSlot.ServiceOption.NORMAL);
        mLocalDataSource.saveTimeSlot(newTimeSlot);

        // When activated in the persistent repository
        mLocalDataSource.updateActivationFlag(newTimeSlot._id(), true);

        // Then the timeSlot can be retrieved from the persistent repository and is active
        TestSubscriber<TimeSlot> testSubscriber = new TestSubscriber<>();
        mLocalDataSource.getTimeSlot(newTimeSlot._id()).subscribe(testSubscriber);
        testSubscriber.assertValueCount(1);
        TimeSlot result = testSubscriber.getOnNextEvents().get(0);
        assertThat(result.activation_flag(), is(true));
    }

    @Test
    public void deleteTimeSlotById_timeSlotNotRetrievable() {
        // Given 3 new timeSlots in the persistent repository
        final TimeSlot newTimeSlot1 = TimeSlot.createTimeSlot("111", "Work", "work time",
                9, 0, 17, 0, "0111110", true, false, TimeSlot.ServiceOption.NORMAL);
        final TimeSlot newTimeSlot2 = TimeSlot.createTimeSlot("222", "School", "school time",
                8, 30, 15, 0, "0111110", true, false, TimeSlot.ServiceOption.MUTE);
        final TimeSlot newTimeSlot3 = TimeSlot.createTimeSlot("333", "Test", "test",
                6, 20, 12, 22, "1100011", true, false, TimeSlot.ServiceOption.MUTE);

        mLocalDataSource.saveTimeSlot(newTimeSlot1);
        mLocalDataSource.saveTimeSlot(newTimeSlot2);
        mLocalDataSource.saveTimeSlot(newTimeSlot3);

        // When a timeSlot are deleted in the repository
        mLocalDataSource.deleteTimeSlot("222");

        // Then the deleted timeSlot cannot be retrieved
        TestSubscriber<List<TimeSlot>> testSubscriber = new TestSubscriber<>();
        mLocalDataSource.getAllTimeSlot().subscribe(testSubscriber);
        List<TimeSlot> result = testSubscriber.getOnNextEvents().get(0);
        assertThat(result, not(hasItems(newTimeSlot2)));
    }

    @Test
    public void deleteAllTimeSlots_emptyListOfRetrievedTimeSlot() {
        // Given a new timeSlot in the persistent repository
        TimeSlot newTimeSlot = TimeSlot.createTimeSlot("111", "Work", "work time",
                9, 0, 17, 0, "0111110", true, false, TimeSlot.ServiceOption.NORMAL);
        mLocalDataSource.saveTimeSlot(newTimeSlot);

        // When all timeSlots are deleted
        mLocalDataSource.deleteAllTimeSlot();

        // Then the retrieved timeSlots is an empty list
        TestSubscriber<List<TimeSlot>> testSubscriber = new TestSubscriber<>();
        mLocalDataSource.getAllTimeSlot().subscribe(testSubscriber);
        List<TimeSlot> result = testSubscriber.getOnNextEvents().get(0);
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void getTimeSlots_retrieveSavedTimeSlots() {
        // Given 2 new timeSlots in the persistent repository
        final TimeSlot newTimeSlot1 = TimeSlot.createTimeSlot("111", "Work", "work time",
                9, 0, 17, 0, "0111110", true, false, TimeSlot.ServiceOption.NORMAL);
        final TimeSlot newTimeSlot2 = TimeSlot.createTimeSlot("222", "School", "school time",
                8, 30, 15, 0, "0111110", true, false, TimeSlot.ServiceOption.MUTE);

        mLocalDataSource.saveTimeSlot(newTimeSlot1);
        mLocalDataSource.saveTimeSlot(newTimeSlot2);

        // Then the timeSlots can be retrieved from the persistent repository
        TestSubscriber<List<TimeSlot>> testSubscriber = new TestSubscriber<>();
        mLocalDataSource.getAllTimeSlot().subscribe(testSubscriber);
        List<TimeSlot> result = testSubscriber.getOnNextEvents().get(0);
        assertThat(result, hasItems(newTimeSlot1, newTimeSlot2));
    }

    @Test
    public void getTimeSlot_whenTimeSlotNotSaved() {
        //Given that no timeSlot has been saved
        //When querying for a timeSlot, null is returned.
        TestSubscriber<TimeSlot> testSubscriber = new TestSubscriber<>();
        mLocalDataSource.getTimeSlot("1").subscribe(testSubscriber);
        testSubscriber.assertValue(null);
    }
}
