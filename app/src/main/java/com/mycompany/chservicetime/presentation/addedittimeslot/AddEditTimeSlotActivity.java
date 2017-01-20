package com.mycompany.chservicetime.presentation.addedittimeslot;

import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.util.ActivityUtils;
import com.mycompany.chservicetime.util.EspressoIdlingResource;

public class AddEditTimeSlotActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_TIME_SLOT = 1;

    AddEditTimeSlotFragment mAddEditTimeSlotFragment;
    String mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_time_slot);

        // hide Soft Keyboard when activity starts
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mAddEditTimeSlotFragment =
                (AddEditTimeSlotFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        mId = getIntent().getStringExtra(AddEditTimeSlotFragment.ARGUMENT_EDIT_TIME_SLOT_ID);

        if (mAddEditTimeSlotFragment == null) {
            mAddEditTimeSlotFragment = AddEditTimeSlotFragment.newInstance(mId);

            if (getIntent().hasExtra(AddEditTimeSlotFragment.ARGUMENT_EDIT_TIME_SLOT_ID) && mId != null) {
                actionBar.setTitle(R.string.edit_timeSlot);
            } else {
                actionBar.setTitle(R.string.add_timeSlot);
            }

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    mAddEditTimeSlotFragment, R.id.contentFrame);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }
}
