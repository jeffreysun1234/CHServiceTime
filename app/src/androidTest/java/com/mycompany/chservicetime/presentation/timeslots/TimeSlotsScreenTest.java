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

package com.mycompany.chservicetime.presentation.timeslots;

import android.app.Activity;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.mycompany.chservicetime.Injection;
import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.data.source.AppDataSource;
import com.mycompany.chservicetime.data.source.AppRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Tests for the timeSlots screen, the main screen which contains a list of all timeSlots.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TimeSlotsScreenTest {

    private AppRepository mTimeSlotRepository;

    /**
     * {@link ActivityTestRule} is a JUnit {@link Rule @Rule} to launch your activity under test.
     * <p/>
     * <p/>
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     * <p/>
     * <p/>
     * Sometimes an {@link Activity} requires a custom start {@link Intent} to receive data
     * from the source Activity. ActivityTestRule has a feature which let's you lazily start the
     * Activity under test, so you can control the Intent that is used to start the target Activity.
     */
    @Rule
    public ActivityTestRule<TimeSlotsActivity> mTimeSlotsActivityTestRule =
            new ActivityTestRule<TimeSlotsActivity>(
                    TimeSlotsActivity.class, true /* Initial touch mode  */, true) {
                /**
                 * To avoid a long list of time slots and the need to scroll through the list to find a
                 * time slot, we call {@link AppDataSource#deleteAllTimeSlot()} before each test.
                 */
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();

                    // Doing this in @Before generates a race condition.
                    mTimeSlotRepository = Injection.provideTimeSlotsRepository(
                            InstrumentationRegistry.getTargetContext());
                    mTimeSlotRepository.deleteAllTimeSlot();
                }
            };

    @Before
    public void setup() {
    }

//    private void loadActiveTimeSlot() {
//        startActivityWithWithStubbedTimeSlot(ACTIVE_TASK);
//    }
//
//    /**
//     * Setup your test fixture with a fake timeSlot id. The {@link TimeSlotsActivity} is started with
//     * a particular timeSlot id, which is then loaded from the service API.
//     * <p/>
//     * <p/>
//     * Note that this test runs hermetically and is fully isolated using a fake implementation of
//     * the service API. This is a great way to make your tests more reliable and faster at the same
//     * time, since they are isolated from any outside dependencies.
//     */
//    private void startActivityWithWithStubbedTimeSlot(TimeSlot timeSlot) {
//        // Add a timeSlot stub to the fake service api layer.
//        AppRepository.destroyInstance();
//        FakeTimeSlotDataSource.getInstance().addTimeSlots(timeSlot);
//
//        // Lazily start the Activity from the ActivityTestRule this time to inject the start Intent
//        Intent startIntent = new Intent();
//        startIntent.putExtra(TimeSlotDetailActivity.EXTRA_TASK_ID, timeSlot.getId());
//        mTimeSlotsActivityTestRule.launchActivity(startIntent);
//    }

    private void createStubbedTimeSlots(){
//        TimeSlot timeSlot = new TimeSlot("1-1", "Work", "Work Time", 9, 0, 17, 0, "1000001", false);
//        mTimeSlotRepository.createOrUpdateTimeSlot(timeSlot);
    }

    @Test
    public void timeSlots_DisplayedInUi() throws Exception {
        // Check that the timeSlot name is displayed
        onView(withId(R.id.nextOperationTitle)).check(matches(isDisplayed()));

        Thread.sleep(5000);

//        onView(withId(R.id.timeSlot_detail_title)).check(matches(withText(TASK_TITLE)));
//        onView(withId(R.id.timeSlot_detail_description)).check(matches(withText(TASK_DESCRIPTION)));
//        onView(withId(R.id.timeSlot_detail_complete)).check(matches(not(isChecked())));
    }

//    @Test
//    public void orientationChange_menuAndTimeSlotPersist() {
//        loadActiveTimeSlot();
//
//        // Check delete menu item is displayed and is unique
//        onView(withId(R.id.menu_delete)).check(matches(isDisplayed()));
//
//        TestUtils.rotateOrientation(mTimeSlotsActivityTestRule);
//
//        // Check that the timeSlot is shown
//        onView(withId(R.id.timeSlot_detail_title)).check(matches(withText(TASK_TITLE)));
//        onView(withId(R.id.timeSlot_detail_description)).check(matches(withText(TASK_DESCRIPTION)));
//
//        // Check delete menu item is displayed and is unique
//        onView(withId(R.id.menu_delete)).check(matches(isDisplayed()));
//    }
}

