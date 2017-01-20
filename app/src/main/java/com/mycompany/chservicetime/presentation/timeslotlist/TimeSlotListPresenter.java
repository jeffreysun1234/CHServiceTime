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

import android.app.Activity;
import android.support.annotation.NonNull;

import com.mycompany.chservicetime.data.source.AppDataSource;
import com.mycompany.chservicetime.data.source.AppRepository;
import com.mycompany.chservicetime.model.TimeSlot;
import com.mycompany.chservicetime.presentation.BaseTiPresenter;
import com.mycompany.chservicetime.util.CHLog;
import com.mycompany.chservicetime.util.EspressoIdlingResource;
import com.mycompany.chservicetime.util.schedulers.BaseSchedulerProvider;

import net.grandcentrix.thirtyinch.rx.RxTiPresenterSubscriptionHandler;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link TimeSlotListFragment}), retrieves the data and updates the
 * UI as required.
 */
public class TimeSlotListPresenter extends BaseTiPresenter<TimeSlotListView> {

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

    public void result(int requestCode, int resultCode) {
        //TODO: getView() is null.
        CHLog.d("******requestCode=" + requestCode + ",,,,resultCode=" + resultCode);
        // If a timeSlot was successfully added, show snackbar
        //if (AddEditTimeSlotActivity.REQUEST_ADD_TASK == requestCode && Activity.RESULT_OK == resultCode) {
        if (Activity.RESULT_OK == resultCode) {
            CHLog.d("--------- " + getView());
            //getView().showSuccessfullySavedMessage();
        }
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
        checkNotNull(timeSlotId, "activeTimeSlot cannot be null!");
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

    public void setForceUpdate(boolean mForceUpdate) {
        this.mForceUpdate = mForceUpdate;
    }
}
