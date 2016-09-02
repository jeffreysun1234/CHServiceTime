package com.mycompany.chservicetime.presentation;

import android.app.KeyguardManager;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import com.mycompany.chservicetime.Injection;
import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.data.source.TimeSlotDataSource;
import com.mycompany.chservicetime.presentation.timeslots.TimeSlotsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.mycompany.chservicetime.presentation.CustomItemMatcher.matchToolbarTitle;

/**
 * Created by szhx on 8/31/2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class WorkflowTest {

    private static final long UI_TEST_TIMEOUT = 5 * 1000; //5 seconds

    private TimeSlotsActivity mActivity;

    private int rvLayoutId;

    /**
     * {@link IntentsTestRule} is an {@link ActivityTestRule} which inits and releases Espresso
     * Intents before and after each test run.
     * <p/>
     * <p/>
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     */
    @Rule
    public IntentsTestRule<TimeSlotsActivity> mActivityRule =
            new IntentsTestRule<TimeSlotsActivity>(TimeSlotsActivity.class, true /* Initial touch mode  */, true) {
                /**
                 * To avoid a long list of time slots and the need to scroll through the list to find a
                 * time slot, we call {@link TimeSlotDataSource#deleteAllTimeSlot()} before each test.
                 */
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();

                    // Doing this in @Before generates a race condition.
                    Injection.provideTimeSlotsRepository(InstrumentationRegistry.getTargetContext())
                            .deleteAllTimeSlot();
                }
            };

    /**
     * Prepare your test fixture for this test. In this case we register an IdlingResources with
     * Espresso. IdlingResource resource is a great way to tell Espresso when your app is in an
     * idle state. This helps Espresso to synchronize your test actions, which makes tests significantly
     * more reliable.
     */
    @UiThreadTest
    @Before
    public void setUp() throws Exception {
        mActivity = mActivityRule.getActivity();

        rvLayoutId = R.id.timeSlotListRecyclerView;

        try {
            mActivityRule.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    KeyguardManager mKG = (KeyguardManager) mActivity.getSystemService(Context.KEYGUARD_SERVICE);
                    KeyguardManager.KeyguardLock mLock = mKG.newKeyguardLock(Context.KEYGUARD_SERVICE);
                    mLock.disableKeyguard();

                    //turn the screen on
                    mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        Espresso.registerIdlingResources(mActivityRule.getActivity().getCountingIdlingResource());

        closeSoftKeyboard();
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    public void tearDown() throws Exception {
        Espresso.unregisterIdlingResources(mActivityRule.getActivity().getCountingIdlingResource());
    }

    private void emptyTimeSlotList() {
        onView(withId(R.id.empty_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.empty_tv)).check(matches(withText(R.string.no_time_slot)));
    }

    private void clickAddTimeSlotIcon_opensAddTimeSlotUi() {
        // click add icon
        onView(withId(R.id.add_time_slot)).perform(click());

        // Check if the add TimeSlot screen is displayed
        matchToolbarTitle(mActivity.getResources().getString(R.string.add_timeSlot));
    }

    private void createTimeSlot(String name, int beginHour, int beginMinute, int endHour, int endMinuter,
                                String days) {
        onView(withId(R.id.timeSlotNameEditText)).perform(typeText(name), closeSoftKeyboard());
        //onView(withId(R.id.timeSlotNameEditText)).perform(replaceText("Espresso Test\n"));

        //EspressoIdlingResource.increment();
        //closeSoftKeyboard();
        //EspressoIdlingResource.decrement();

        onView(withId(R.id.beginTimePicker)).perform(PickerActions.setTime(beginHour, beginMinute));
        onView(withId(R.id.endTimePicker)).perform(PickerActions.setTime(endHour, endMinuter));
        if (days.charAt(0) == '1') onView(withId(R.id.day0InWeekToggleButton)).perform(click());
        if (days.charAt(1) == '1') onView(withId(R.id.day1InWeekToggleButton)).perform(click());
        if (days.charAt(2) == '1') onView(withId(R.id.day2InWeekToggleButton)).perform(click());
        if (days.charAt(3) == '1') onView(withId(R.id.day3InWeekToggleButton)).perform(click());
        if (days.charAt(4) == '1') onView(withId(R.id.day4InWeekToggleButton)).perform(click());
        if (days.charAt(5) == '1') onView(withId(R.id.day5InWeekToggleButton)).perform(click());
        if (days.charAt(6) == '1') onView(withId(R.id.day6InWeekToggleButton)).perform(click());

        onView(withId(R.id.time_slot_save)).perform(click());

        // Verify task is displayed on screen
        onView(CustomItemMatcher.withItemText(name)).check(matches(isDisplayed()));
        // verify the data display correctly
        onView(withId(R.id.nameTextView)).check(matches(withText(name)));
    }

    private void changeItemName(String oldName, String newName){
        // change the name of the item
        onView(withId(R.id.timeSlotNameEditText)).perform(clearText(), replaceText(newName));
        onView(withId(R.id.time_slot_save)).perform(click());

        // verify the name of the item to be changed
        onView(withId(R.id.timeSlotListRecyclerView)).perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.nameTextView)).check(matches(withText(newName)));

        // Verify previous TimeSlot is not displayed
        onView(withText(oldName)).check(doesNotExist());
    }

    @Test
    public void normalWorkflow() {

        // There is no data at begin.
        emptyTimeSlotList();

        clickAddTimeSlotIcon_opensAddTimeSlotUi();

        // add TimeSlot dat
        String timeSlotName = "Work";
        createTimeSlot(timeSlotName, 9, 0, 17, 0, "0111110");

        // locate to the position 0.
        onView(withId(R.id.timeSlotListRecyclerView)).perform(RecyclerViewActions.scrollToPosition(0));

        // TODO: verify Edit icon is not display
        //onView(withId(R.id.edit_item_button)).check(matches(not(isCompletelyDisplayed())));

        // swipe
        onView(withId(R.id.nameTextView)).perform(swipeLeft());

        // click Edit icon
        onView(withId(R.id.edit_item_button)).perform(click());

        //Wait for the root view of the TimeSlot fragment.
        //onView(isRoot()).perform(waitForMatch(withId(R.id.time_slot_root_view), UI_TEST_TIMEOUT));

        String newTimeSlotName = "Change Name";
        changeItemName(timeSlotName, newTimeSlotName);

        // swipe again and click Delete icon
        onView(withId(R.id.nameTextView)).perform(swipeLeft());
        onView(withId(R.id.delete_item_button)).perform(click());

        // verify the item to be deleted.
        onView(withId(R.id.empty_tv)).check(matches(withText(R.string.no_time_slot)));
    }
}

