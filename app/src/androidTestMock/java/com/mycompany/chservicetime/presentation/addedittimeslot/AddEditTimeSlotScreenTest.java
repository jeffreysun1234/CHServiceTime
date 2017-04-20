package com.mycompany.chservicetime.presentation.addedittimeslot;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.data.source.FakeAppDataSource;
import com.mycompany.chservicetime.model.TimeSlot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by szhx on 5/2/2016.
 * <p/>
 * Tests for the add TimeSlot screen.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddEditTimeSlotScreenTest {

    /**
     * {@link TimeSlot} stub that is added to the fake service API layer.
     */
    private TimeSlot timeSlot1 = TimeSlot.createTimeSlot("111", "Work", "work time",
            9, 0, 17, 0, "0111110", true, false, TimeSlot.ServiceOption.MUTE);

    /**
     * {@link IntentsTestRule} is an {@link ActivityTestRule} which inits and releases Espresso
     * Intents before and after each test run.
     * <p/>
     * <p/>
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     */
    @Rule
    public IntentsTestRule<AddEditTimeSlotActivity> mAddTaskIntentsTestRule =
            new IntentsTestRule<>(AddEditTimeSlotActivity.class,
                    true /* Initial touch mode  */,
                    false /* Lazily launch activity */);

    @Before
    public void setUp() throws Exception {
        /**
         * we register an IdlingResources with Espresso.
         * IdlingResource resource is a great way to tell Espresso when your app is in an idle state.
         * This helps Espresso to synchronize your test actions, which makes tests significantly more reliable.
         */
//        Espresso.registerIdlingResources(
//                mAddTaskIntentsTestRule.getActivity().getCountingIdlingResource());
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    //@After
    public void tearDown() {
//        Espresso.unregisterIdlingResources(
//                mAddTaskIntentsTestRule.getActivity().getCountingIdlingResource());
    }

    /**
     * Setup your test fixture with a fake timeSlot id. The {@link AddEditTimeSlotActivity} is started with
     * a particular timeSlot id, which is then loaded from the service API.
     * <p>
     * Note that this test runs hermetically and is fully isolated using a fake implementation of
     * the service API. This is a great way to make your tests more reliable and faster at the same
     * time, since they are isolated from any outside dependencies.
     */
    private void startActivityWithStubbedTimeSlot(TimeSlot timeSlot) {
        // Lazily start the Activity from the ActivityTestRule this time to inject the start Intent
        Intent startIntent = new Intent();
        startIntent.putExtra(AddEditTimeSlotFragment.ARGUMENT_EDIT_TIME_SLOT_ID, timeSlot._id());
        mAddTaskIntentsTestRule.launchActivity(startIntent);
    }

    private void startActivityWithEmptyTimeSlot() {
        // Lazily start the Activity from the ActivityTestRule this time to inject the start Intent
        Intent startIntent = new Intent();
        mAddTaskIntentsTestRule.launchActivity(startIntent);
    }

    @Test
    public void errorShownOnEmptyTimeSlot() {
        startActivityWithEmptyTimeSlot();

        // Add empty name
        onView(withId(R.id.timeSlotNameEditText)).perform(typeText(""));

        // Try to save the task
        onView(withId(R.id.time_slot_save)).perform(click());

        // Verify empty tasks snackbar is shown
        String emptyTaskMessageText = getTargetContext().getString(R.string.input_name_error);
        onView(withText(emptyTaskMessageText)).check(matches(isDisplayed()));

        // Verify that the activity is still displayed (a correct task would close it).
        onView(withId(R.id.timeSlotNameEditText)).check(matches(isDisplayed()));
    }


    @Test
    public void timeSlotDetails_DisplayedInUi() throws Exception {
        // Given some TimeSlots
        FakeAppDataSource source = FakeAppDataSource.getInstance();
        source.deleteAllTimeSlot();
        source.saveTimeSlot(timeSlot1.toBuilder().build());

        startActivityWithStubbedTimeSlot(timeSlot1);

        // Check that the TimeSlot name are displayed
        onView(withId(R.id.timeSlotNameEditText)).check(matches(withText(timeSlot1.name())));
        onView(withId(R.id.radio_mute)).check(matches((isChecked())));
        onView(withId(R.id.radio_vibrate)).check(matches(not(isChecked())));
    }
}