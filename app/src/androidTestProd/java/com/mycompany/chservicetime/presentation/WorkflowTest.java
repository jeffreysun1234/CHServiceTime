package com.mycompany.chservicetime.presentation;

import android.app.KeyguardManager;
import android.content.Context;
import android.media.AudioManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.filters.SdkSuppress;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.view.WindowManager;

import com.mycompany.chservicetime.CHApplication;
import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.TestDataHelper;
import com.mycompany.chservicetime.TimeSlotListUIHelper;
import com.mycompany.chservicetime.data.source.AppDataSource;
import com.mycompany.chservicetime.data.source.local.AppLocalDataSource;
import com.mycompany.chservicetime.model.TimeSlot;
import com.mycompany.chservicetime.presentation.timeslotlist.TimeSlotListActivity;
import com.mycompany.chservicetime.util.schedulers.BaseSchedulerProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.mycompany.chservicetime.CustomItemMatcher.matchToolbarTitle;
import static com.mycompany.chservicetime.TestHelper.getCurrentRingMode;
import static org.junit.Assert.assertEquals;

/**
 * Created by szhx on 8/31/2016.
 */
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
@LargeTest
public class WorkflowTest {

    private static final long UI_TEST_TIMEOUT = 5 * 1000; //5 seconds

    private BaseSchedulerProvider mSchedulerProvider;

    private AppLocalDataSource mLocalDataSource;

    private TimeSlotListActivity mActivity;

    private int rvLayoutId;

    private UiDevice mDevice;

    /**
     * {@link IntentsTestRule} is an {@link ActivityTestRule} which inits and releases Espresso
     * Intents before and after each test run.
     * <p>
     * <p>
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     */
    @Rule
    public IntentsTestRule<TimeSlotListActivity> mActivityRule =
            new IntentsTestRule<TimeSlotListActivity>(TimeSlotListActivity.class, true /* Initial touch mode  */, true) {
                /**
                 * To avoid a long list of time slots and the need to scroll through the list to find a
                 * time slot, we call {@link AppDataSource#deleteAllTimeSlot()} before each test.
                 */
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();

                    // Doing this in @Before generates a race condition.
                    ((CHApplication) InstrumentationRegistry.getTargetContext()
                            .getApplicationContext()).getAppRepositoryComponent()
                            .getAppRepository().deleteAllTimeSlot();
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

        // Keep the screen on.
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

        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    public void tearDown() throws Exception {
        Espresso.unregisterIdlingResources(mActivityRule.getActivity().getCountingIdlingResource());
    }

    @Test
    public void normalWorkflow() {
        TimeSlot testTimeSlot = TestDataHelper.getTimeSlotWithCurrentTime(TimeSlot.ServiceOption.VIBRATION);

        Context targetContext = InstrumentationRegistry.getTargetContext();

        TimeSlotListUIHelper timeSlotListUIHelper = new TimeSlotListUIHelper(targetContext);

        // There is no data at begin.
        timeSlotListUIHelper.emptyTimeSlotList();

        // open AddTimeSlot UI.
        timeSlotListUIHelper.clickAddTimeSlotIcon_opensAddTimeSlotUi();

        // add a TimeSlot
        timeSlotListUIHelper.createTimeSlot(testTimeSlot);

        // locate to the position 0.
        onView(withId(R.id.timeslot_list)).perform(RecyclerViewActions.scrollToPosition(0));

        // click activation checkbox
        onView(withId(R.id.activeSwitch)).perform(click());

        // verify the vibrate status
        int currentRingMode = getCurrentRingMode(mActivity.getApplicationContext());
        assertEquals(AudioManager.RINGER_MODE_VIBRATE, currentRingMode);

//        // TODO: verify Edit icon is hidden
//        //onView(withId(R.id.edit_item_button)).check(matches((isCompletelyDisplayed())));

        // click Edit menu
        timeSlotListUIHelper.clickSwipeMenuByViewId(R.id.nameTextView,
                targetContext.getString(R.string.img_edit));

        // Check if the add TimeSlot screen is displayed
        matchToolbarTitle(mActivity.getResources().getString(R.string.edit_timeSlot));

        // change the name of TimeSlot and save.
        String newTimeSlotName = "Change Name";
        timeSlotListUIHelper.changeItemName(testTimeSlot.name(), newTimeSlotName);

        // swipe again and click Delete icon
        timeSlotListUIHelper.clickSwipeMenuByViewId(R.id.nameTextView,
                targetContext.getString(R.string.img_delete));

        // verify the item to be deleted.
        timeSlotListUIHelper.emptyTimeSlotList();

        // verify the vibrate status
        currentRingMode = getCurrentRingMode(mActivity.getApplicationContext());
        assertEquals(AudioManager.RINGER_MODE_NORMAL, currentRingMode);
    }
}

