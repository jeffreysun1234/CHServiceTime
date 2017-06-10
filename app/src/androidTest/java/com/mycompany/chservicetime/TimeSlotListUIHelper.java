package com.mycompany.chservicetime;

import android.content.Context;
import android.support.test.espresso.contrib.PickerActions;

import com.mycompany.chservicetime.CustomItemMatcher.DATA_VIEW_TYPE;
import com.mycompany.chservicetime.model.TimeSlot;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.mycompany.chservicetime.CustomItemMatcher.matchToolbarTitle;
import static com.mycompany.chservicetime.CustomItemMatcher.withItemText;
import static org.hamcrest.Matchers.allOf;

/**
 * Some atom operations of TimeSlotList UI.
 * <p>
 * constructor need a target context.
 */
public class TimeSlotListUIHelper {
    Context mTargetContext;

    public TimeSlotListUIHelper(Context targetContext) {
        mTargetContext = targetContext;
    }

    public void emptyTimeSlotList() {
        onView(withId(R.id.noTimeSlots)).check(matches(isDisplayed()));
        onView(withId(R.id.noTimeSlotsMain)).check(matches(withText(R.string.no_timeslots_all)));
    }

    public void clickAddTimeSlotIcon_opensAddTimeSlotUi() {
        // click add icon
        onView(withId(R.id.add_time_slot)).perform(click());

        // Check if the add TimeSlot screen is displayed
        matchToolbarTitle(mTargetContext.getString(R.string.add_timeSlot));
    }

    public void createTimeSlot(TimeSlot timeSlot) {
        onView(withId(R.id.timeSlotNameEditText))
                .perform(typeText(timeSlot.name()), closeSoftKeyboard());
        onView(withId(R.id.beginTimePicker))
                .perform(PickerActions.setTime(timeSlot.begin_time_hour(), timeSlot.begin_time_minute()));
        onView(withId(R.id.endTimePicker))
                .perform(PickerActions.setTime(timeSlot.end_time_hour(), timeSlot.end_time_minute()));
        if (timeSlot.days().charAt(0) == '1')
            onView(withId(R.id.day0InWeekToggleButton)).perform(click());
        if (timeSlot.days().charAt(1) == '1')
            onView(withId(R.id.day1InWeekToggleButton)).perform(click());
        if (timeSlot.days().charAt(2) == '1')
            onView(withId(R.id.day2InWeekToggleButton)).perform(click());
        if (timeSlot.days().charAt(3) == '1')
            onView(withId(R.id.day3InWeekToggleButton)).perform(click());
        if (timeSlot.days().charAt(4) == '1')
            onView(withId(R.id.day4InWeekToggleButton)).perform(click());
        if (timeSlot.days().charAt(5) == '1')
            onView(withId(R.id.day5InWeekToggleButton)).perform(click());
        if (timeSlot.days().charAt(6) == '1')
            onView(withId(R.id.day6InWeekToggleButton)).perform(click());

        onView(withId(R.id.time_slot_save)).perform(click());

        // Verify timeslot is displayed on screen
        onView(withItemText(timeSlot.name(), DATA_VIEW_TYPE.RECYCLERVIEW))
                .check(matches(isDisplayed()));
    }

    public void changeItemName(String oldName, String newName) {
        // change the name of the item
        onView(withId(R.id.timeSlotNameEditText)).perform(clearText(), replaceText(newName));
        onView(withId(R.id.time_slot_save)).perform(click());

        // verify the name of the item to be changed
        //onView(withId(R.id.timeSlotListRecyclerView)).perform(RecyclerViewActions.scrollToPosition(0));
        onView(withItemText(newName, DATA_VIEW_TYPE.RECYCLERVIEW)).check(matches(isDisplayed()));
        onView(withId(R.id.nameTextView)).check(matches(withText(newName)));

        // Verify previous TimeSlot is not displayed
        onView(withText(oldName)).check(doesNotExist());
    }

    public void clickCheckBoxForActivate(String title) {
        onView(allOf(withId(R.id.activeSwitch), hasSibling(withText(title)))).perform(click());
    }

    /**
     * @param viewId        selected the item
     * @param operationText the text of the item of the swipe menu.
     */
    public void clickSwipeMenuByViewId(int viewId, String operationText) {
        // swipe
        onView(withId(viewId)).perform(swipeLeft());

        // click Delete icon
        onView(withItemText(operationText, DATA_VIEW_TYPE.RECYCLERVIEW)).check(matches(isDisplayed()))
                .perform(click());
    }

    /**
     * @param stringResId the text of the overflow menu
     */
    public void clickOverFlowMenuByText(int stringResId) {
        // open the overflow menu.
        openActionBarOverflowOrOptionsMenu(mTargetContext);

        // we may be need to delay manually sometime.
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        onView(withText(mTargetContext.getString(stringResId))).perform(click());
    }
}
