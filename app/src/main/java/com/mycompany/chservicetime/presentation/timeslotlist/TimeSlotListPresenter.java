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

package com.mycompany.chservicetime.presentation.timeslotlist;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.business.auth.FirebaseAuthAdapter;
import com.mycompany.chservicetime.data.firebase.FirebaseRestDAO;
import com.mycompany.chservicetime.data.firebase.model.TimeSlotItem;
import com.mycompany.chservicetime.data.source.AppDataSource;
import com.mycompany.chservicetime.data.source.AppRepository;
import com.mycompany.chservicetime.model.ModelConverter;
import com.mycompany.chservicetime.model.TimeSlot;
import com.mycompany.chservicetime.presentation.BaseTiPresenter;
import com.mycompany.chservicetime.util.CHLog;
import com.mycompany.chservicetime.util.EspressoIdlingResource;
import com.mycompany.chservicetime.util.schedulers.BaseSchedulerProvider;

import net.grandcentrix.thirtyinch.rx.RxTiPresenterSubscriptionHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Listens to user actions from the UI ({@link TimeSlotListFragment}), retrieves the data and updates the
 * UI as required.
 */
public class TimeSlotListPresenter extends BaseTiPresenter<TimeSlotListView> {
    private static final String TAG = "TimeSlotListPresenter";

    @NonNull
    AppRepository mAppRepository;

    @NonNull
    BaseSchedulerProvider mSchedulerProvider;

    private boolean mFirstLoad = true;

    // Force to read data from the local database.
    private boolean mForceUpdate = false;

    private RxTiPresenterSubscriptionHandler rxHelper = new RxTiPresenterSubscriptionHandler(this);

    @Inject
    public TimeSlotListPresenter(AppRepository appRepository, BaseSchedulerProvider schedulerProvider) {
        mAppRepository = appRepository;
        mSchedulerProvider = schedulerProvider;
    }

    @Override
    protected void onAttachView(@NonNull final TimeSlotListView view) {
        super.onAttachView(view);

        rxHelper.manageViewSubscription(loadTimeSlots(mForceUpdate));
    }

    public Subscription loadTimeSlots(boolean forceUpdate) {
        // Simplification for sample: a network reload will be forced on first load.
        Subscription subscription = loadTimeSlots(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
        return subscription;
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link AppDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private Subscription loadTimeSlots(final boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            getView().setLoadingIndicator(true);
        }
        if (forceUpdate) {
            mAppRepository.refreshTimeSlots();
        }

        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice

        Subscription subscription = mAppRepository
                .getAllTimeSlot()
                .flatMap(new Func1<List<TimeSlot>, Observable<TimeSlot>>() {
                    @Override
                    public Observable<TimeSlot> call(List<TimeSlot> timeSlots) {
                        return Observable.from(timeSlots);
                    }
                })
                .toList()
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .doOnTerminate(() -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                        EspressoIdlingResource.decrement(); // Set app as idle.
                    }
                    getView().setLoadingIndicator(false);
                })
                .subscribe(
                        //onNext
                        this::processTimeSlots,
                        // OnError
                        throwable -> getView().showLoadingTimeSlotsError());

        return subscription;
    }

    private void processTimeSlots(@NonNull List<TimeSlot> timeSlots) {
        if (timeSlots.isEmpty()) {
            getView().showNoTimeSlots();
        } else {
            // Show the list of timeSlots
            getView().showTimeSlots(timeSlots);
        }
    }

    public void addEditTimeSlot(String id) {
        getView().showAddEditTimeSlot(id);
    }

    public void activateTimeSlot(@NonNull String timeSlotId, boolean activationFlag) {
//        checkNotNull(timeSlotId, "activeTimeSlot cannot be null!");
        int row = mAppRepository.updateActivationFlag(timeSlotId, activationFlag);
        if (row == 1) {
            getView().showTimeSlotActivationFlagMessage(activationFlag);
        }
        loadTimeSlots(false, false);
    }

    public void clearTimeSlots() {
        mAppRepository.deleteAllTimeSlot();
        getView().showTimeSlotsClearedMessage();
        loadTimeSlots(false, false);
    }

    public void deleteTimeSlot(String timeSlotId) {
        mAppRepository.deleteTimeSlot(timeSlotId);
        getView().showTimeSlotDeletedMessage();
        loadTimeSlots(false, false);
    }

    public boolean isForceUpdate() {
        return mForceUpdate;
    }

    /**
     * make sure of get the latest data from the local data source.
     *
     * @param mForceUpdate
     */
    public void setForceUpdate(boolean mForceUpdate) {
        this.mForceUpdate = mForceUpdate;
    }

    public void backupTimeSlotList() {
        if (FirebaseAuthAdapter.isSignIn()) {
            new AsyncTask() {

                @Override
                protected void onPreExecute() {
                    getView().setLoadingIndicator(true);
                }

                @Override
                protected Object doInBackground(Object[] params) {
                    try {
                        String userId = FirebaseAuthAdapter.getUserEmail();
                        String authToken = FirebaseAuthAdapter.getAuthToken();

                        CHLog.d(TAG, "userId: " + userId + " ; authToken: " + authToken);

                        FirebaseRestDAO.create().backupTimeSlotItemList(
                                userId,
                                authToken,
                                mAppRepository.getAllTimeSlot().toBlocking().first());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    getView().setLoadingIndicator(false);
                    getView().showFeedbackMessage(R.string.backup_done);
                }
            }.execute();
        } else {
            getView().showFeedbackMessage(R.string.sign_in_hint);
        }
    }

    public void restoreTimeSlotList() {
        if (FirebaseAuthAdapter.isSignIn()) {
            new AsyncTask() {

                @Override
                protected void onPreExecute() {
                    getView().setLoadingIndicator(true);
                }

                @Override
                protected Object doInBackground(Object[] params) {
                    try {
                        String userId = FirebaseAuthAdapter.getUserEmail();
                        String authToken = FirebaseAuthAdapter.getAuthToken();

                        CHLog.d(TAG, "userId: " + userId + " ; authToken: " + authToken);

                        Collection<TimeSlotItem> timeSlotItemList = FirebaseRestDAO.create()
                                .restoreTimeSlotItemList(userId, authToken);
                        for (TimeSlotItem tsItem : timeSlotItemList) {
                            mAppRepository.saveTimeSlot(ModelConverter.firebaseTimeSlotItemToTimeSlot(tsItem));
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    getView().setLoadingIndicator(false);
                    getView().showFeedbackMessage(R.string.restore_done);
                    // refresh view
                    loadTimeSlots(false, false);
                }
            }.execute();
        } else {
            getView().showFeedbackMessage(R.string.sign_in_hint);
        }
    }
}
