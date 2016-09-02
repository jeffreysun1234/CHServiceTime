package com.mycompany.chservicetime.presentation.addedittimeslot;


import com.mycompany.chservicetime.base.BasePresenter;
import com.mycompany.chservicetime.base.BaseView;
import com.mycompany.chservicetime.model.TimeSlot;

/**
 * Created by szhx on 5/1/2016.
 * <p>
 * This specifies the contract between the view and the presenter.
 */
public class AddEditTimeSlotContract {
    interface View extends BaseView<Presenter> {

        void showError(int error);

        void finishView();

        void setTimeSlotFields(TimeSlot timeSlot);

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void createOrUpdateTimeSlot(String timeSlotId, String name, String description,
                                    int beginTimeHour, int beginTimeMinute,
                                    int endTimeHour, int endTimeMinute, String days, boolean repeatFlag);

        void populateTimeSlot();
    }
}
