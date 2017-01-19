/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.mycompany.chservicetime.presentation.timeslotlist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.mycompany.chservicetime.CHApplication;
import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.di.component.DaggerTimeSlotListComponent;
import com.mycompany.chservicetime.di.component.TimeSlotListComponent;
import com.mycompany.chservicetime.util.ActivityUtils;
import com.mycompany.chservicetime.util.EspressoIdlingResource;

import net.grandcentrix.thirtyinch.TiActivity;

public class TimeSlotListActivity extends TiActivity<TimeSlotListPresenter, TimeSlotListView> {

    TimeSlotListPresenter timeSlotListPresenter;
    TimeSlotListFragment timeSlotsFragment;

    @NonNull
    @Override
    public TimeSlotListPresenter providePresenter() {
        /*
         * Inject the TimeSlotListPresenter instance to Dagger Graph, then the injected fields can be instanced.
         */
        TimeSlotListComponent timeSlotsComponent = DaggerTimeSlotListComponent.builder()
                .appRepositoryComponent(CHApplication.INSTANCE.getAppRepositoryComponent())
                .build();

        timeSlotListPresenter = timeSlotsComponent.getTimeSlotListPresenter();

        return timeSlotListPresenter;
    }

    @NonNull
    @Override
    public TimeSlotListView provideView() {
        // Binding View happens on the OnStart_after and Setting Presenter happens on the OnCreate_after,
        // so we pass the Presenter instance to Fragment at this point.
        timeSlotsFragment.setPresenter(timeSlotListPresenter);

        return timeSlotsFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_slot_list_act);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        timeSlotsFragment =
                (TimeSlotListFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (timeSlotsFragment == null) {
            // Create the fragment
            timeSlotsFragment = TimeSlotListFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), timeSlotsFragment, R.id.contentFrame);
        }
    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }
}
