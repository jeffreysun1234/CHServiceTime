package com.mycompany.chservicetime.presentation.addedittimeslot;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mycompany.chservicetime.data.source.AppDataSource;
import com.mycompany.chservicetime.data.source.AppRepository;
import com.mycompany.chservicetime.model.TimeSlot;
import com.mycompany.chservicetime.presentation.BaseTiPresenter;
import com.mycompany.chservicetime.util.schedulers.BaseSchedulerProvider;

import net.grandcentrix.thirtyinch.rx.RxTiPresenterSubscriptionHandler;

import javax.inject.Inject;

import rx.Subscription;

/**
 * Created by szhx on 5/1/2016.
 */
public class AddEditTimeSlotPresenter extends BaseTiPresenter<AddEditTimeSlotView> {

    public static final int NULL_TIME_SLOT_ERROR = -1;
    public static final int MESSAGE_TYPE_VERIFY_SUCCESS = 0;
    public static final int MESSAGE_TYPE_INPUT_NAME_ERROR = 1;
    public static final int MESSAGE_TYPE_INPUT_TIME_ERROR = 2;
    public static final int MESSAGE_TYPE_INPUT_DAY_ERROR = 3;
    public static final int MESSAGE_TYPE_FAIL_GET_TIME_SLOT_ERROR = 4;
    public static final int MESSAGE_TYPE_FAIL_SAVE_TIME_SLOT_ERROR = 5;

    @NonNull
    AppDataSource mAppRepository;

    @NonNull
    BaseSchedulerProvider mSchedulerProvider;

    @Nullable
    String mId;

    RxTiPresenterSubscriptionHandler rxHelper = new RxTiPresenterSubscriptionHandler(this);

    /**
     * Creates a presenter for the add/edit view.
     *
     * @param id ID of the timeSlot to edit or null for a new timeSlot
     */
    @Inject
    public AddEditTimeSlotPresenter(@Nullable String id, @NonNull AppRepository appRepository,
                                    @NonNull BaseSchedulerProvider schedulerProvider) {
        mId = id;
        mAppRepository = appRepository;
        //mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null!");
        mSchedulerProvider = schedulerProvider;
    }

    /**
     * Method injection is used here to safely reference {@code this} after the object is created.
     * For more information, see Java Concurrency in Practice.
     */
    @Inject
    void setupListeners() {
        //((AddEditTaskFragment)this.getView())..setPresenter(this);
    }

    @Override
    protected void onAttachView(@NonNull final AddEditTimeSlotView view) {
        super.onAttachView(view);

        if (isNewTimeSlot()) {
            showTimeSlot(null);
        } else {
            // automatically unsubscribe in onDetachView(view)
            rxHelper.manageViewSubscription(populateTimeSlot());
        }
    }

    public Subscription populateTimeSlot() {
        if (isNewTimeSlot()) {
            throw new RuntimeException("populateTimeSlot() was called but timeSlot is new.");
        }

        Subscription subscription = mAppRepository
                .getTimeSlot(mId)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(
                        // onNext
                        timeSlot -> showTimeSlot(timeSlot),
                        // onError
                        e -> showMessage(MESSAGE_TYPE_FAIL_GET_TIME_SLOT_ERROR)
                );

        return subscription;
    }

    public void saveTimeSlot(String name, String description,
                             int beginTimeHour, int beginTimeMinute, int endTimeHour, int endTimeMinute,
                             String days, boolean repeatFlag, TimeSlot.ServiceOption serviceOption,
                             boolean activationFlag) {
        TimeSlot timeSlot = TimeSlot.createTimeSlot(name, description,
                beginTimeHour, beginTimeMinute, endTimeHour, endTimeMinute,
                days, repeatFlag, serviceOption);

        int verifyTimeSlotResult = verifyTimeSlot(timeSlot);

        // no error information means verify success.
        if (verifyTimeSlotResult == MESSAGE_TYPE_VERIFY_SUCCESS) {
            if (isNewTimeSlot()) {
                // insert a new TimeSlot
                mAppRepository.saveTimeSlot(timeSlot);
            } else {
                // update a TimeSlot, _id keeps same.
                mAppRepository.saveTimeSlot(
                        timeSlot.toBuilder()._id(mId).activation_flag(activationFlag).build());
            }

            getView().showTimeSlotList();
        } else {
            showMessage(verifyTimeSlotResult);
        }
    }

    private int verifyTimeSlot(TimeSlot timeSlot) {
        if (timeSlot == null) {
            throw new RuntimeException("verifyTimeSlot() was called but timeSlot is NULL.");
        }

        if (timeSlot.name() == null || timeSlot.name().trim().equals("")) {
            return MESSAGE_TYPE_INPUT_NAME_ERROR;
        }
        if (timeSlot.begin_time_hour() * 100 + timeSlot.begin_time_minute() ==
                timeSlot.end_time_hour() * 100 + timeSlot.end_time_minute()) {
            return MESSAGE_TYPE_INPUT_TIME_ERROR;
        }

        if (timeSlot.days() == null || "0000000".equals(timeSlot.days())) {
            return MESSAGE_TYPE_INPUT_DAY_ERROR;
        }
        return MESSAGE_TYPE_VERIFY_SUCCESS;
    }

    /**
     * @param timeSlot If timeSlot is null, then show a new TimeSlot Form.
     */
    private void showTimeSlot(TimeSlot timeSlot) {
        // The view may not be able to handle UI updates anymore
        if (viewIsActive()) {
            getView().setTimeSlotFields(timeSlot);
        }
    }

    private void showMessage(int messageType) {
        // The view may not be able to handle UI updates anymore
        if (viewIsActive()) {
            getView().showMessage(messageType);
        }
    }

    private boolean isNewTimeSlot() {
        return mId == null;
    }
}
