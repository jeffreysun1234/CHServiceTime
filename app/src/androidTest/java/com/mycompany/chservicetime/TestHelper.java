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

package com.mycompany.chservicetime;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitor;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.v7.widget.Toolbar;

import java.util.Collection;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.runner.lifecycle.Stage.RESUMED;

/**
 * Useful test methods common to all activities
 */
public class TestHelper {

    private static void rotateToLandscape(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private static void rotateToPortrait(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public static void rotateOrientation(Activity activity) {
        int currentOrientation = activity.getResources().getConfiguration().orientation;

        switch (currentOrientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                rotateToPortrait(activity);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                rotateToLandscape(activity);
                break;
            default:
                rotateToLandscape(activity);
        }
    }

    /**
     * Returns the content description for the navigation button view in the toolbar.
     */
    public static String getToolbarNavigationContentDescription(
            @NonNull Activity activity, @IdRes int toolbar1) {
        Toolbar toolbar = (Toolbar) activity.findViewById(toolbar1);
        if (toolbar != null) {
            return (String) toolbar.getNavigationContentDescription();
        } else {
            throw new RuntimeException("No toolbar found.");
        }
    }

    /**
     * Gets an Activity in the RESUMED stage.
     * <p>
     * This method should never be called from the Main thread. In certain situations there might
     * be more than one Activities in RESUMED stage, but only one is returned.
     * See {@link ActivityLifecycleMonitor}.
     */
    public static Activity getCurrentActivity() throws IllegalStateException {
        // The array is just to wrap the Activity and be able to access it from the Runnable.
        final Activity[] resumedActivity = new Activity[1];

        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance()
                        .getActivitiesInStage(RESUMED);
                if (resumedActivities.iterator().hasNext()) {
                    resumedActivity[0] = (Activity) resumedActivities.iterator().next();
                } else {
                    throw new IllegalStateException("No Activity in stage RESUMED");
                }
            }
        });
        return resumedActivity[0];
    }

    public static String getText(Context context, int stringId) {
        return context.getResources().getString(stringId);
    }

    public static int getCurrentRingMode(Context context) {
        AudioManager audioManager =
                (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getRingerMode();
    }

    public static boolean viewIsDisplayed(int viewId) {
        final boolean[] isDisplayed = {true};
        onView(withId(viewId))
                .withFailureHandler((error, viewMatcher) -> isDisplayed[0] = false)
                .check(matches(isDisplayed()));
        return isDisplayed[0];
    }

    public static boolean textIsDisplayed(Context context, int stringResId) {
        final boolean[] isDisplayed = {true};
        onView(withText(context.getString(stringResId)))
                .withFailureHandler((error, viewMatcher) -> isDisplayed[0] = false)
                .check(matches(isDisplayed()));
        return isDisplayed[0];
    }
}
