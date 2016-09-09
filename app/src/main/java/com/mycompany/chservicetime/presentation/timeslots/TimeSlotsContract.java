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

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.mycompany.chservicetime.base.BasePresenter;
import com.mycompany.chservicetime.base.BaseView;
import com.mycompany.chservicetime.model.TimeSlot;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface TimeSlotsContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean activeFlag, int stringResId);

        void showTimeSlots(Cursor cursor);

        void showAddTimeSlotUI();

        void showEditTimeSlotUi(String timeSlotId);

        void showTimeSlotMarkedActive();

        void showTimeSlotDeleted();

        void showLoadingTimeSlotsError();

        void showNoTimeSlots();

        void showSuccessfullySavedMessage();

        boolean isActive();

        void showSnackbarMessage(int stringResId);

        void showLoginHint();

    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadTimeSlots(boolean showLoadingIndicator);

        void addNewTimeSlot();

        void openTimeSlotDetail(@NonNull String timeSlotId);

        void activateTimeSlot(@NonNull TimeSlot activeTimeSlot);

        void deleteTimeSlot(@NonNull String timeSlotId);

        void backupTimeSlotList();

        void restoreTimeSlotList();
    }
}
