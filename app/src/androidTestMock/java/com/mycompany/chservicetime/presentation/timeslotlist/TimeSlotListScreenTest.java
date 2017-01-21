package com.mycompany.chservicetime.presentation.timeslotlist;

import android.app.Activity;
import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;

import com.mycompany.chservicetime.CustomItemMatcher.DATA_VIEW_TYPE;
import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.data.source.AppRepository;
import com.mycompany.chservicetime.data.source.FakeAppDataSource;
import com.mycompany.chservicetime.model.TimeSlot;
import com.mycompany.chservicetime.util.EspressoIdlingResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.mycompany.chservicetime.CustomItemMatcher.withItemText;

/**
 * This test use the fake data source.
 */
public class TimeSlotListScreenTest {

    final static TimeSlot TimeSlot1 = TimeSlot.createTimeSlot("111", "Work", "work time",
            9, 0, 17, 0, "0111110", true, false, TimeSlot.ServiceOption.NORMAL);
    final static TimeSlot TimeSlot2 = TimeSlot.createTimeSlot("222", "School", "school time",
            8, 30, 15, 0, "0111110", true, true, TimeSlot.ServiceOption.MUTE);
    final static TimeSlot TimeSlot3 = TimeSlot.createTimeSlot("333", "Test", "test",
            6, 20, 12, 22, "1100011", true, false, TimeSlot.ServiceOption.MUTE);

    AppRepository mAppRepository;

    IdlingResource mIdlingResource;

    /**
     * {@link ActivityTestRule} is a JUnit {@link Rule @Rule} to launch your activity under test.
     * <p>
     * <p>
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     * <p>
     * <p>
     * Sometimes an {@link Activity} requires a custom start {@link Intent} to receive data
     * from the source Activity. ActivityTestRule has a feature which let's you lazily start the
     * Activity under test, so you can control the Intent that is used to start the target Activity.
     */
    @Rule
    public ActivityTestRule<TimeSlotListActivity> mTimeSlotListActivityTestRule =
            new ActivityTestRule<TimeSlotListActivity>(TimeSlotListActivity.class
                    , true /* Initial touch mode  */
                    , false /* Lazily launch activity */);
    // TODO: check the lifecycle of ActivityTestRule when launchtype is true or false
    // https://jabknowsnothing.wordpress.com/2015/11/05/activitytestrule-espressos-test-lifecycle/
//            {
//                /**
//                 * To avoid a long list of tasks and the need to scroll through the list to find a
//                 * TimeSlot, we delete all data before each test.
//                 */
//                @Override
//                protected void beforeActivityLaunched() {
//                    super.beforeActivityLaunched();
//                    cleanTimeSlots();
//                }
//            };

    @Before
    public void setUp() throws Exception {
        /**
         * we register an IdlingResources with Espresso.
         * IdlingResource resource is a great way to tell Espresso when your app is in an idle state.
         * This helps Espresso to synchronize your test actions, which makes tests significantly more reliable.
         */
        //mIdlingResource = mTimeSlotListActivityTestRule.getActivity().getCountingIdlingResource();
        mIdlingResource = EspressoIdlingResource.getIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    public void tearDown() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }

    /**
     * Setup your test fixture with a fake timeSlot id. The {@link TimeSlotListActivity} is started.
     * <p>
     * Note that this test runs hermetically and is fully isolated using a fake implementation of
     * the service API. This is a great way to make your tests more reliable and faster at the same
     * time, since they are isolated from any outside dependencies.
     */
    private void startTestActivity() {
        // Lazily start the Activity from the ActivityTestRule this time to inject the start Intent
        Intent startIntent = new Intent();
        mTimeSlotListActivityTestRule.launchActivity(startIntent);
    }

    @Test
    public void showAllTimeSlots() {
        // Add 2 timeSlots
        cleanTimeSlots();
        createTimeSlots(TimeSlot1, TimeSlot2);

        startTestActivity();

        //Verify that all our timeSlots are shown
        onView(withItemText(TimeSlot1.name(), DATA_VIEW_TYPE.RECYCLERVIEW)).check(matches(isDisplayed()));
        onView(withItemText(TimeSlot2.name(), DATA_VIEW_TYPE.RECYCLERVIEW)).check(matches(isDisplayed()));
    }

    @Test
    public void showeEmptyTimeSlots() {
        startTestActivity();

        //Verify that all our timeSlots are shown
        onView(withId(R.id.add_time_slot)).check(matches(isDisplayed()));
        onView(withId(R.id.noTimeSlots)).check(matches(isDisplayed()));
    }

    @Test
    public void clickAddTimeSlotButton_opensAddTimeSlotUi() {
        startTestActivity();

        // Click on the add timeSlot button
        onView(withId(R.id.add_time_slot)).perform(click());

        // Check if the add timeSlot screen is displayed
        onView(withId(R.id.timeSlotNameEditText)).check(matches(isDisplayed()));
    }

    private void cleanTimeSlots() {
        FakeAppDataSource.getInstance().deleteAllTimeSlot();
    }

    private void createTimeSlots(TimeSlot... timeSlots) {
        if (timeSlots != null) {
            FakeAppDataSource.getInstance().addTimeSlots(timeSlots);
        }
    }
}
