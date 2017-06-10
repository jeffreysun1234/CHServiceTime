package com.mycompany.chservicetime.presentation;

import android.app.KeyguardManager;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.filters.SdkSuppress;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.view.WindowManager;

import com.mycompany.chservicetime.CHApplication;
import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.TestHelper;
import com.mycompany.chservicetime.TimeSlotListUIHelper;
import com.mycompany.chservicetime.data.source.AppDataSource;
import com.mycompany.chservicetime.data.source.AppRepository;
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
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.uiautomator.Until.findObject;
import static com.mycompany.chservicetime.CustomItemMatcher.DATA_VIEW_TYPE;
import static com.mycompany.chservicetime.CustomItemMatcher.withItemText;

/**
 * Created by szhx on 8/31/2016.
 */
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
@LargeTest
public class BackupRestoreTest {

    private static final long UI_TEST_TIMEOUT = 5 * 1000; // 5 seconds
    private static final long UI_OPERATE_TIMEOUT = 500; // 500 millonseconds

    // change the name of the package if needed.
    private static final String BASIC_PACKAGE = "com.mycompany.servicetime.debug";

    private BaseSchedulerProvider mSchedulerProvider;

    private AppLocalDataSource mLocalDataSource;

    private TimeSlotListActivity mActivity;

    private int rvLayoutId;

    private UiDevice mDevice;

    private Context mTargetContext;
    private TimeSlotListUIHelper mTimeSlotListUIHelper;

    final static TimeSlot TimeSlot1 = TimeSlot.createTimeSlot("111", "Work", "work time",
            9, 0, 17, 0, "0111110", true, false, TimeSlot.ServiceOption.NORMAL);

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
                    AppRepository appRepository = ((CHApplication) InstrumentationRegistry.getTargetContext()
                            .getApplicationContext()).getAppRepositoryComponent()
                            .getAppRepository();
                    appRepository.deleteAllTimeSlot();
                    appRepository.saveTimeSlot(TimeSlot1);
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

        //rvLayoutId = R.id.timeSlotListRecyclerView;

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
        mTargetContext = InstrumentationRegistry.getTargetContext();

        mTimeSlotListUIHelper = new TimeSlotListUIHelper(mTargetContext);

        // open the overflow menu.
        openActionBarOverflowOrOptionsMenu(mTargetContext);
        // check if the user has login
        if (TestHelper.textIsDisplayed(mTargetContext, R.string.action_logout)) {
            pressBack();
        } else {
            loginFirebase(mTargetContext, mDevice);
        }

        backup();

        // delete a timeslot
        mTimeSlotListUIHelper.clickSwipeMenuByViewId(R.id.nameTextView,
                mTargetContext.getString(R.string.img_delete));

        // verify the item to be deleted.
        mTimeSlotListUIHelper.emptyTimeSlotList();

        restore();

        // the deleted timeslot display again
        onView(withItemText(TimeSlot1.name(), DATA_VIEW_TYPE.RECYCLERVIEW))
                .check(matches(isDisplayed()));
    }

    private void backup() {
        mTimeSlotListUIHelper.clickOverFlowMenuByText(R.string.menu_backup);

        // check backup finish
        onView(withText(mTargetContext.getString(R.string.backup_done)))
                .check(matches(isDisplayed()));
    }

    private void restore() {
        // restore
        mTimeSlotListUIHelper.clickOverFlowMenuByText(R.string.menu_restore);
        // check restore finish
        onView(withText(mTargetContext.getString(R.string.restore_done)))
                .check(matches(isDisplayed()));

    }

    private void loginFirebase(Context targetContext, UiDevice uiDevice) {
        onView(withText(targetContext.getString(R.string.action_login)))
                .perform(click());

        uiDevice.findObject(By.res(BASIC_PACKAGE, "email")).setText("b@b.com");
        uiDevice.findObject(By.res(BASIC_PACKAGE, "button_next")).click();

        UiObject2 passwordEditText = uiDevice.wait(
                findObject(By.res(BASIC_PACKAGE, "password")), UI_TEST_TIMEOUT);
        passwordEditText.setText("b");

        uiDevice.findObject(By.res(BASIC_PACKAGE, "button_done")).click();

        // wait until return to the main app
        uiDevice.wait(Until.findObject(By.res(BASIC_PACKAGE, "add_time_slot")), UI_TEST_TIMEOUT);
    }
}

