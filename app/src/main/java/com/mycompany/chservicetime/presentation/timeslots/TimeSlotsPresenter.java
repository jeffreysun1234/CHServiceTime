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

package com.mycompany.chservicetime.presentation.timeslots;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;

import com.mycompany.chservicetime.CHApplication;
import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.business.auth.FirebaseAuthAdapter;
import com.mycompany.chservicetime.data.firebase.FirebaseRestDAO;
import com.mycompany.chservicetime.data.firebase.model.TimeSlotItem;
import com.mycompany.chservicetime.data.source.AppRepository;
import com.mycompany.chservicetime.model.TimeSlot;
import com.mycompany.chservicetime.presentation.addedittimeslot.AddEditTimeSlotActivity;
import com.mycompany.chservicetime.service.SchedulingIntentService;

import java.io.IOException;
import java.util.Collection;

import static com.mycompany.chservicetime.util.LogUtils.LOGD;
import static com.mycompany.chservicetime.util.LogUtils.makeLogTag;

/**
 * Listens to user actions from the UI ({@link TimeSlotsFragment}), retrieves the data and updates the
 * UI as required.
 */
public class TimeSlotsPresenter implements TimeSlotsContract.Presenter {
    private static final String TAG = makeLogTag("TimeSlotsPresenter");

    private final static int TIME_SLOTS_QUERY_LOADER = 1;

    @NonNull
    private final AppRepository mTimeSlotRepository;

    private final LoaderManager mLoaderManager;

    private final TimeSlotsContract.View mTimeSlotsView;

    private boolean mFirstLoad = true;

    public TimeSlotsPresenter(@NonNull TimeSlotsContract.View timeSlotsView,
                              @NonNull AppRepository timeSlotRepository,
                              @NonNull LoaderManager loaderManager) {
        mTimeSlotsView = timeSlotsView;
        mTimeSlotRepository = timeSlotRepository;
        mLoaderManager = loaderManager;

        mTimeSlotsView.setPresenter(this);
    }

    @Override
    public void start() {
        loadTimeSlots(true);
    }

    @Override
    public void result(int requestCode, int resultCode) {
        // If a timeSlot was successfully added, show snackbar
        if (AddEditTimeSlotActivity.REQUEST_ADD_TIME_SLOT == requestCode && Activity.RESULT_OK == resultCode) {
            mTimeSlotsView.showSuccessfullySavedMessage();

            // Send the open and close sound alarms based on the current data.
            SchedulingIntentService.startActionSetAlarm(CHApplication.getContext());
        }
    }

    /**
     * @param showLoadingIndicator Pass in true to display a loading icon in the UI
     */
    @Override
    public void loadTimeSlots(final boolean showLoadingIndicator) {
        mLoaderManager.initLoader(TIME_SLOTS_QUERY_LOADER, null,
                new TimeSlotsQueryLoaderCallbacks(CHApplication.getContext(), mTimeSlotsView,
                        showLoadingIndicator));
    }

    @Override
    public void addNewTimeSlot() {
        mTimeSlotsView.showAddTimeSlotUI();
    }

    @Override
    public void openTimeSlotDetail(@NonNull String requestedTimeSlotId) {
        mTimeSlotsView.showEditTimeSlotUi(requestedTimeSlotId);
    }

    @Override
    public void activateTimeSlot(@NonNull TimeSlot activeTimeSlot) {
        mTimeSlotRepository.updateActivationFlag(activeTimeSlot._id(), activeTimeSlot.activation_flag());
        mTimeSlotsView.showTimeSlotMarkedActive(activeTimeSlot.name(), activeTimeSlot.activation_flag());
    }

    @Override
    public void deleteTimeSlot(@NonNull String timeSlotId) {
        mTimeSlotRepository.deleteTimeSlot(timeSlotId);
        mTimeSlotsView.showTimeSlotDeleted();
    }

    @Override
    public void backupTimeSlotList() {
        if (FirebaseAuthAdapter.isSignIn()) {
            new AsyncTask() {

                @Override
                protected void onPreExecute() {
                    mTimeSlotsView.setLoadingIndicator(true, R.string.progress_dialog_access_firebase);
                }

                @Override
                protected Object doInBackground(Object[] params) {
//                    try {
//                        String userId = FirebaseAuthAdapter.getUserId();
//                        String authToken = FirebaseAuthAdapter.getAuthToken();
//
//                        LOGD(TAG, "userId: " + userId + " ; authToken: " + authToken);
//
//                        FirebaseRestDAO.create().backupTimeSlotItemList(
//                                userId,
//                                authToken,
//                                mTimeSlotRepository.backupAllTimeSlots());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    mTimeSlotsView.setLoadingIndicator(false, -1);
                    mTimeSlotsView.showSnackbarMessage(R.string.backup_done);
                }
            }.execute();
        } else {
            mTimeSlotsView.showLoginHint();
        }
    }

    @Override
    public void restoreTimeSlotList() {
        if (FirebaseAuthAdapter.isSignIn()) {
            new AsyncTask() {

                @Override
                protected void onPreExecute() {
                    mTimeSlotsView.setLoadingIndicator(true, R.string.progress_dialog_access_firebase);
                }

                @Override
                protected Object doInBackground(Object[] params) {
                    try {
                        String userId = FirebaseAuthAdapter.getUserId();
                        String authToken = FirebaseAuthAdapter.getAuthToken();

                        LOGD(TAG, "userId: " + userId + " ; authToken: " + authToken);

                        Collection<TimeSlotItem> timeSlotItemList = FirebaseRestDAO.create()
                                .restoreTimeSlotItemList(userId, authToken);

                        //mTimeSlotRepository.restoreAllTimeSlots(timeSlotItemList);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    mTimeSlotsView.setLoadingIndicator(false, -1);
                    mTimeSlotsView.showSnackbarMessage(R.string.restore_done);
                }
            }.execute();
        } else {
            mTimeSlotsView.showLoginHint();
        }
    }
}
