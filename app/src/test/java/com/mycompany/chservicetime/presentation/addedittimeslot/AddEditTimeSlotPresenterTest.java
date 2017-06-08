package com.mycompany.chservicetime.presentation.addedittimeslot;

import com.mycompany.chservicetime.data.source.AppRepository;
import com.mycompany.chservicetime.model.TimeSlot;
import com.mycompany.chservicetime.util.schedulers.BaseSchedulerProvider;
import com.mycompany.chservicetime.util.schedulers.ImmediateSchedulerProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;

import rx.Observable;

import static com.mycompany.chservicetime.presentation.addedittimeslot.AddEditTimeSlotPresenter.MESSAGE_TYPE_FAIL_GET_TIME_SLOT_ERROR;
import static com.mycompany.chservicetime.presentation.addedittimeslot.AddEditTimeSlotPresenter.MESSAGE_TYPE_INPUT_NAME_ERROR;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by szhx on 6/8/2017.
 */
public class AddEditTimeSlotPresenterTest {

    @Mock
    private AppRepository mAppRepository;

    @Mock
    private AddEditTimeSlotView mAddEditTimeSlotView;

    private BaseSchedulerProvider mSchedulerProvider;

    private AddEditTimeSlotPresenter mAddEditTimeSlotPresenter;

    final static TimeSlot TimeSlot1 = TimeSlot.createTimeSlot("111", "Work", "work time",
            9, 0, 17, 0, "0111110", true, false, TimeSlot.ServiceOption.NORMAL);

    @Before
    public void setupMocksAndView() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        mSchedulerProvider = new ImmediateSchedulerProvider();

        // Get a reference to the class under test
        mAddEditTimeSlotPresenter = new AddEditTimeSlotPresenter(TimeSlot1._id(), mAppRepository, mSchedulerProvider);
        mAddEditTimeSlotPresenter.create();
    }

    @Test
    public void saveNewTimeSlotToRepository_showsSuccessMessageUi() {
        // Get a reference to the class under test with a empty Id parameter for adding a new TimeSlot.
        mAddEditTimeSlotPresenter = new AddEditTimeSlotPresenter(null, mAppRepository, mSchedulerProvider);
        mAddEditTimeSlotPresenter.create();
        mAddEditTimeSlotPresenter.attachView(mAddEditTimeSlotView);

        // When the presenter is asked to save a timeSlot
        mAddEditTimeSlotPresenter.saveTimeSlot(TimeSlot1.name(), TimeSlot1.description(),
                TimeSlot1.begin_time_hour(), TimeSlot1.begin_time_minute(),
                TimeSlot1.end_time_hour(), TimeSlot1.end_time_minute(),
                TimeSlot1.days(), TimeSlot1.repeat_flag(), TimeSlot1.service_option(),
                TimeSlot1.activation_flag());

        // Then a timeSlot is saved in the repository and the view updated
        ArgumentCaptor<TimeSlot> showTimeSlotArgumentCaptor = ArgumentCaptor.forClass(TimeSlot.class);
        verify(mAppRepository).saveTimeSlot(showTimeSlotArgumentCaptor.capture()); // saved to the model
        assertThat(showTimeSlotArgumentCaptor.getValue()._id(), notNullValue());
        verify(mAddEditTimeSlotView).showTimeSlotList(); // shown in the UI
    }

    @Test
    public void saveTimeSlot_emptyTimeSlotShowsErrorUi() {
        // Get a reference to the class under test with a empty Id parameter for adding a new TimeSlot.
        mAddEditTimeSlotPresenter = new AddEditTimeSlotPresenter(null, mAppRepository, mSchedulerProvider);
        mAddEditTimeSlotPresenter.create();
        mAddEditTimeSlotPresenter.attachView(mAddEditTimeSlotView);

        // When the presenter is asked to save an timeSlot with a empty name
        mAddEditTimeSlotPresenter.saveTimeSlot("", TimeSlot1.description(),
                TimeSlot1.begin_time_hour(), TimeSlot1.begin_time_minute(),
                TimeSlot1.end_time_hour(), TimeSlot1.end_time_minute(),
                TimeSlot1.days(), TimeSlot1.repeat_flag(), TimeSlot1.service_option(),
                TimeSlot1.activation_flag());

        // Then an empty not error is shown in the UI
        verify(mAddEditTimeSlotView).showMessage(eq(MESSAGE_TYPE_INPUT_NAME_ERROR));
    }

    @Test
    public void saveExistingTimeSlotToRepository_showsSuccessMessageUi() {
        when(mAppRepository.getTimeSlot(TimeSlot1._id())).thenReturn(Observable.just(TimeSlot1));
        mAddEditTimeSlotPresenter.attachView(mAddEditTimeSlotView);

        // When the presenter is asked to save an existing timeSlot
        mAddEditTimeSlotPresenter.saveTimeSlot("New Name", TimeSlot1.description(),
                TimeSlot1.begin_time_hour(), TimeSlot1.begin_time_minute(),
                TimeSlot1.end_time_hour(), TimeSlot1.end_time_minute(),
                TimeSlot1.days(), TimeSlot1.repeat_flag(), TimeSlot1.service_option(),
                TimeSlot1.activation_flag());

        // Then a timeSlot is saved in the repository and the view updated
        ArgumentCaptor<TimeSlot> showTimeSlotArgumentCaptor = ArgumentCaptor.forClass(TimeSlot.class);
        verify(mAppRepository).saveTimeSlot(showTimeSlotArgumentCaptor.capture()); // saved to the model
        assertThat(showTimeSlotArgumentCaptor.getValue()._id(), equalTo(TimeSlot1._id()));
        verify(mAddEditTimeSlotView).showTimeSlotList(); // shown in the UI
    }

    @Test
    public void populateTimeSlot_callsRepoAndUpdatesViewOnSuccess() {
        when(mAppRepository.getTimeSlot(TimeSlot1._id())).thenReturn(Observable.just(TimeSlot1));

        mAddEditTimeSlotPresenter.attachView(mAddEditTimeSlotView);

        // Then the timeSlot repository is queried and the view updated
        verify(mAppRepository).getTimeSlot(eq(TimeSlot1._id()));

        verify(mAddEditTimeSlotView).setTimeSlotFields(any(TimeSlot.class));
    }

    @Test
    public void populateTimeSlot_callsRepoAndUpdatesViewOnError() {
        when(mAppRepository.getTimeSlot(TimeSlot1._id())).thenReturn(
                Observable.<TimeSlot>error(new NoSuchElementException()));

        mAddEditTimeSlotPresenter.attachView(mAddEditTimeSlotView);

        // Then the timeSlot repository is queried and a error message is showed.
        verify(mAppRepository).getTimeSlot(eq(TimeSlot1._id()));

        verify(mAddEditTimeSlotView).showMessage(eq(MESSAGE_TYPE_FAIL_GET_TIME_SLOT_ERROR));
        verify(mAddEditTimeSlotView, never()).setTimeSlotFields(any(TimeSlot.class));
    }
}