package com.mycompany.chservicetime.presentation.addedittimeslot;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mycompany.chservicetime.data.source.TimeSlotDataSource;
import com.mycompany.chservicetime.data.source.TimeSlotRepository;
import com.mycompany.chservicetime.model.TimeSlot;

/**
 * Created by szhx on 5/1/2016.
 */
public class AddEditTimeSlotPresenter implements AddEditTimeSlotContract.Presenter {

    public static final int NULL_TIME_SLOT_ERROR = -1;
    public static final int VERIFY_SUCCESS = 0;
    public static final int INPUT_NAME_ERROR = 1;
    public static final int INPUT_TIME_ERROR = 2;
    public static final int INPUT_DAY_ERROR = 3;
    public static final int FAIL_GET_TIME_SLOT_ERROR = 4;
    public static final int FAIL_SAVE_TIME_SLOT_ERROR = 5;

    @NonNull
    private TimeSlotRepository mTimeSlotRepository;

    private final AddEditTimeSlotContract.View mAddTimeSlotView;

    @Nullable
    private String mTimeSlotId;

    /**
     * Creates a presenter for the add/edit view.
     *
     * @param timeSlotId      ID of the timeSlot to edit or null for a new timeSlot
     * @param addTimeSlotView the add/edit view
     */
    public AddEditTimeSlotPresenter(@Nullable String timeSlotId,
                                    @NonNull TimeSlotRepository timeSlotRepository,
                                    @NonNull AddEditTimeSlotContract.View addTimeSlotView) {
        mTimeSlotId = timeSlotId;
        mTimeSlotRepository = timeSlotRepository;
        mAddTimeSlotView = addTimeSlotView;

        mAddTimeSlotView.setPresenter(this);
    }

    @Override
    public void start() {
        if (mTimeSlotId != null) {
            populateTimeSlot();
        } else {
            showTimeSlot(null);
        }
    }

    @Override
    public void createOrUpdateTimeSlot(String timeSlotId, String name, String description,
                                       int beginTimeHour, int beginTimeMinute, int endTimeHour, int endTimeMinute,
                                       String days, boolean repeatFlag) {
        TimeSlot timeSlot = new TimeSlot(timeSlotId, name, description,
                beginTimeHour, beginTimeMinute, endTimeHour, endTimeMinute,
                days, repeatFlag);

        int verifyTimeSlotResult = verifyTimeSlot(timeSlot);
        // no error information means verify success.
        if (verifyTimeSlotResult == VERIFY_SUCCESS) {
            mTimeSlotRepository.createOrUpdateTimeSlot(timeSlot);

            mAddTimeSlotView.finishView();
        } else {
            showVerifyTimeSlotError(verifyTimeSlotResult);
        }
    }

    @Override
    public void populateTimeSlot() {
        if (mTimeSlotId == null) {
            throw new RuntimeException("populateTimeSlot() was called but timeSlot is new.");
        }

        mTimeSlotRepository.getTimeSlot(mTimeSlotId, new TimeSlotDataSource.GetTimeSlotCallback() {
            @Override
            public void onTimeSlotLoaded(TimeSlot timeSlot) {
                showTimeSlot(timeSlot);
            }

            @Override
            public void onDataNotAvailable() {
                showVerifyTimeSlotError(FAIL_GET_TIME_SLOT_ERROR);
            }
        });
    }

    private int verifyTimeSlot(TimeSlot timeSlot) {
        if (timeSlot == null) {
            throw new RuntimeException("verifyTimeSlot() was called but timeSlot is NULL.");
        }

        if (timeSlot.name == null || timeSlot.name.trim().equals("")) {
            return INPUT_NAME_ERROR;
        }
        if (timeSlot.beginTimeHour * 100 + timeSlot.beginTimeMinute ==
                timeSlot.endTimeHour * 100 + timeSlot.endTimeMinute) {
            return INPUT_TIME_ERROR;
        }

        if (timeSlot.days == null || "0000000".equals(timeSlot.days)) {
            return INPUT_DAY_ERROR;
        }
        return VERIFY_SUCCESS;
    }

    /**
     * @param timeSlot If timeSlot is null, then show a new TimeSlot Form.
     */
    private void showTimeSlot(TimeSlot timeSlot) {
        // The view may not be able to handle UI updates anymore
        if (mAddTimeSlotView.isActive()) {
            mAddTimeSlotView.setTimeSlotFields(timeSlot);
        }
    }

    private void showSaveError() {
        // The view may not be able to handle UI updates anymore
        if (mAddTimeSlotView.isActive()) {
            mAddTimeSlotView.showError(FAIL_SAVE_TIME_SLOT_ERROR);
        }
    }

    private void showVerifyTimeSlotError(int error) {
        // The view may not be able to handle UI updates anymore
        if (mAddTimeSlotView.isActive()) {
            mAddTimeSlotView.showError(error);
        }
    }
}
