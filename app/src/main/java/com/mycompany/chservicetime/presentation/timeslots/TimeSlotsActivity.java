package com.mycompany.chservicetime.presentation.timeslots;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mycompany.chservicetime.CHApplication;
import com.mycompany.chservicetime.Injection;
import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.base.BaseActivity;
import com.mycompany.chservicetime.data.source.local.CHServiceTimeContract;
import com.mycompany.chservicetime.service.SchedulingIntentService;
import com.mycompany.chservicetime.util.ActivityUtils;
import com.mycompany.chservicetime.util.EspressoIdlingResource;

import static com.mycompany.chservicetime.util.LogUtils.LOGD;
import static com.mycompany.chservicetime.util.LogUtils.makeLogTag;

public class TimeSlotsActivity extends BaseActivity {

    private static final String TAG = makeLogTag("TimeSlotsActivity");

    private TimeSlotsPresenter mTimeSlotsPresenter;

    private TimeSlotContentObserver mTimeSlotContentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_slots);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TimeSlotsFragment timeSlotsFragment =
                (TimeSlotsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (timeSlotsFragment == null) {
            // Create the fragment
            timeSlotsFragment = TimeSlotsFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), timeSlotsFragment, R.id.fragment);
        }

        // Create the presenter
        mTimeSlotsPresenter = new TimeSlotsPresenter(
                timeSlotsFragment,
                Injection.provideTimeSlotsRepository(getApplicationContext()),
                getSupportLoaderManager()
        );

        // Register TimeSlot data observer to trigger Scheduling service.
        mTimeSlotContentObserver = new TimeSlotContentObserver(null);
        getContentResolver().registerContentObserver(CHServiceTimeContract.TimeSlots.buildTimeSlotsUri(),
                true, mTimeSlotContentObserver);

        // Add AdView
//        AdView mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder()
//                //.addTestDevice(getString(R.string.ad_test_device_id))
//                .build();
//        mAdView.loadAd(adRequest);

    }

    @Override
    protected void onDestroy() {
        // Unregister TimeSlot data observer
        getContentResolver().unregisterContentObserver(mTimeSlotContentObserver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);

        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class TimeSlotContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public TimeSlotContentObserver(Handler handler) {
            super(handler);
        }

        // Implement the onChange(boolean) method to delegate the change notification to
        // the onChange(boolean, Uri) method to ensure correct operation on older versions
        // of the framework that did not have the onChange(boolean, Uri) method.
        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        // Implement the onChange(boolean, Uri) method to take advantage of the new Uri argument.
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            LOGD(TAG, "TimeSlotContentObserver ~~~~~~ selfChange = " + selfChange);

            // Send the open and close sound alarms based on the current data.
            SchedulingIntentService.startActionSetAlarm(CHApplication.getContext());
        }

    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }

}
