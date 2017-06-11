// Reference to https://blog.egorand.me/testing-runtime-permissions-lessons-learned/

package com.mycompany.chservicetime.presentation;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.filters.SdkSuppress;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;

import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.UiAutomatorUtils;
import com.mycompany.chservicetime.presentation.timeslotlist.TimeSlotListActivity;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.mycompany.chservicetime.UiAutomatorUtils.TEXT_DENY;
import static com.mycompany.chservicetime.UiAutomatorUtils.assertViewWithTextIsVisible;
import static com.mycompany.chservicetime.UiAutomatorUtils.denyCurrentPermission;
import static com.mycompany.chservicetime.UiAutomatorUtils.grantPermission;
import static com.mycompany.chservicetime.UiAutomatorUtils.openPermissions;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 23)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Ignore
public class PermissionTest {

    @Rule
    public ActivityTestRule<TimeSlotListActivity> rule = new ActivityTestRule<>(TimeSlotListActivity.class);

    private UiDevice mDevice;

    @Before
    public void setUp() {
        this.mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void a_shouldDisplayFirstPermissionRequestDialogAtStartup() throws Exception {
        assertViewWithTextIsVisible(mDevice, UiAutomatorUtils.TEXT_ALLOW);
        assertViewWithTextIsVisible(mDevice, TEXT_DENY);

        // cleanup for the next test
        denyCurrentPermission(mDevice);

        onView(withText(R.string.permission_phone_state_denied)).check(matches(isDisplayed()));
    }

    @Test
    public void b_shouldDisplaySecondPermissionRequestDialogIfPreviousPermissionWasDenied() throws Exception {
        assertViewWithTextIsVisible(mDevice, UiAutomatorUtils.TEXT_ALLOW);
        assertViewWithTextIsVisible(mDevice, TEXT_DENY);

        denyCurrentPermission(mDevice);
    }

    @Test
    public void c_shouldDisplayWriteSettingPermissionWindowIfPreviousPermissionWasDenied() throws Exception {
        assertViewWithTextIsVisible(mDevice, UiAutomatorUtils.TEXT_WRITE_SETTING);

        UiObject settingSwitchButton = mDevice.findObject(new UiSelector().resourceIdMatches("switchWidget"));
        settingSwitchButton.click();

        assertThat(settingSwitchButton.isChecked(), is(true));
    }

    @Test
    public void d_shouldDisplayMainActivityAfterPressBack() throws Exception {
        mDevice.pressBack();

        onView(withId(R.id.add_time_slot)).check(matches(isDisplayed()));
    }

    @Test
    @Ignore
    public void d_shouldLoadMainScreenIfPermissionWasGranted() throws Exception {
        onView(withId(R.id.add_time_slot)).check(matches(isDisplayed()));
        //        onView(withText(R.string.grant_permission)).check(matches(isDisplayed()));

        // will grant the permission for the next test
//        onView(withText(R.string.grant_permission)).perform(click());
        openPermissions(mDevice);
        grantPermission(mDevice, "Contacts");
    }
}
