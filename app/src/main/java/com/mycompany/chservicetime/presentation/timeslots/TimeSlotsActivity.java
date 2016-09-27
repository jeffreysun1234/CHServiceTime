package com.mycompany.chservicetime.presentation.timeslots;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

    View mRootView;

    /**
     * Id to identify a WRITE_SETTINGS permission request.
     */
    private static final int REQUEST_WRITE_SETTINGS = 0;

    /**
     * Id to identify a READ_PHONE_STATE permission request.
     */
    private static final int REQUEST_READ_PHONE_STATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_slots);

        mRootView = findViewById(android.R.id.content);

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
    public void onStart() {
        super.onStart();

        // Request permissions
        requestReadPhoneStatePermission(this);
        requestWriteSettingsPermission(this);
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

    void requestWriteSettingsPermission(Activity context) {
        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(context);
        } else {
            permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        if (permission) {
            LOGD(TAG, "WRITE_SETTINGS permission has already been granted.");
        } else {
            LOGD(TAG, "WRITE_SETTINGS permission has NOT been granted. Requesting permission.");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivityForResult(intent, REQUEST_WRITE_SETTINGS);
            } else {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_SETTINGS},
                        REQUEST_WRITE_SETTINGS);
            }
        }
    }

    /**
     * Requests the READ_PHONE_STATE permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    void requestReadPhoneStatePermission(Activity context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            LOGD(TAG, "READ_PHONE_STATE permission has already been granted.");
        } else {
            LOGD(TAG, "READ_PHONE_STATE permission has NOT been granted. Requesting permission.");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                // Provide an additional rationale to the user if the permission was not granted
                // and the user would benefit from additional context for the use of the permission.
                // For example if the user has previously denied the permission.
                LOGD(TAG, "Displaying READ_PHONE_STATE permission rationale to provide additional context.");
                Snackbar.make(mRootView, R.string.permission_READ_PHONE_STATE_rationale,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(TimeSlotsActivity.this,
                                        new String[]{Manifest.permission.READ_PHONE_STATE},
                                        REQUEST_READ_PHONE_STATE);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                        REQUEST_READ_PHONE_STATE);
            }
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_SETTINGS) {
            // Received permission result for WRITE_SETTINGS permission.
            LOGD(TAG, "Received response for WRITE_SETTINGS permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // WRITE_SETTINGS permission has been granted
                LOGD(TAG, "WRITE_SETTINGS permission has now been granted.");
            } else {
                LOGD(TAG, "WRITE_SETTINGS permission was NOT granted.");
            }
        } else if (requestCode == REQUEST_READ_PHONE_STATE) {
            LOGD(TAG, "Received response for READ_PHONE_STATE permissions request.");
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LOGD(TAG, "READ_PHONE_STATE permission has now been granted.");
                Snackbar.make(mRootView, R.string.permision_available_READ_PHONE_STATE, Snackbar.LENGTH_SHORT).show();
            } else {
                LOGD(TAG, "READ_PHONE_STATE permission was NOT granted.");
                Snackbar.make(mRootView, R.string.permissions_not_granted, Snackbar.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_WRITE_SETTINGS && Settings.System.canWrite(this)) {
            LOGD(TAG, "WRITE_SETTINGS permission has now been granted.");
            //do your code
        }
    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }

}
