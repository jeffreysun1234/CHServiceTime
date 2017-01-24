package com.mycompany.chservicetime.presentation.addedittimeslot;

import com.mycompany.chservicetime.data.source.AppRepository;
import com.mycompany.chservicetime.model.TimeSlot;
import com.mycompany.chservicetime.util.schedulers.BaseSchedulerProvider;
import com.mycompany.chservicetime.util.schedulers.ImmediateSchedulerProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the implementation of {@link AddEditTimeSlotPresenter}.
 */
public class AddEditTimeSlotPresenterTest {

    @Mock
    private AppRepository mAppRepository;

    @Mock
    private AddEditTimeSlotView mAddEditTimeSlotView;

    private BaseSchedulerProvider mSchedulerProvider;

    private AddEditTimeSlotPresenter mAddEditTimeSlotPresenter;

    private TimeSlot timeSlot;

    @Before
    public void setupMocksAndView() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        mSchedulerProvider = new ImmediateSchedulerProvider();

        timeSlot = TimeSlot.createTimeSlot("111", "Work", "work time",
                9, 0, 17, 0, "0111110", true, false, TimeSlot.ServiceOption.NORMAL);
    }

    @Test
    public void saveNewTimeSlotToRepository_showsSuccessMessageUi() {
        // Get a reference to the class under test
        mAddEditTimeSlotPresenter = new AddEditTimeSlotPresenter(null, mAppRepository, mSchedulerProvider);

        // Initialize the presenter
        mAddEditTimeSlotPresenter.create();
        mAddEditTimeSlotPresenter.attachView(mAddEditTimeSlotView);

        // When the presenter is asked to save a task
        mAddEditTimeSlotPresenter.saveTimeSlot(timeSlot.name(), timeSlot.description(),
                timeSlot.begin_time_hour(), timeSlot.begin_time_minute(),
                timeSlot.end_time_hour(), timeSlot.end_time_minute(), timeSlot.days(),
                timeSlot.repeat_flag(), timeSlot.service_option(), timeSlot.activation_flag());

        // Then a task is saved in the repository and the view updated
        verify(mAppRepository).saveTimeSlot(any(TimeSlot.class)); // saved to the model
        verify(mAddEditTimeSlotView).showTimeSlotList(); // shown in the UI
    }

    @Test
    public void saveTimeSlot_emptyNameTimeSlotShowsErrorUi() {
        // Get a reference to the class under test
        mAddEditTimeSlotPresenter = new AddEditTimeSlotPresenter(null, mAppRepository, mSchedulerProvider);

        // Initialize the presenter
        mAddEditTimeSlotPresenter.create();
        mAddEditTimeSlotPresenter.attachView(mAddEditTimeSlotView);

        // When the presenter is asked to save an empty TimeSlot
        mAddEditTimeSlotPresenter.saveTimeSlot("", timeSlot.description(),
                timeSlot.begin_time_hour(), timeSlot.begin_time_minute(),
                timeSlot.end_time_hour(), timeSlot.end_time_minute(), timeSlot.days(),
                timeSlot.repeat_flag(), timeSlot.service_option(), timeSlot.activation_flag());

        // Then an error is shown in the UI
        verify(mAddEditTimeSlotView).showMessage(
                eq(AddEditTimeSlotPresenter.MESSAGE_TYPE_INPUT_NAME_ERROR));
    }

    @Test
    public void saveExistingTimeSlotToRepository_showsSuccessMessageUi() {
        when(mAppRepository.getTimeSlot(timeSlot._id())).thenReturn(Observable.just(timeSlot));

        // Get a reference to the class under test
        mAddEditTimeSlotPresenter = new AddEditTimeSlotPresenter(timeSlot._id(), mAppRepository, mSchedulerProvider);

        // Initialize the presenter
        mAddEditTimeSlotPresenter.create();
        mAddEditTimeSlotPresenter.attachView(mAddEditTimeSlotView);

        // When the presenter is asked to save an existing task
        mAddEditTimeSlotPresenter.saveTimeSlot(timeSlot.name(), timeSlot.description(),
                timeSlot.begin_time_hour(), timeSlot.begin_time_minute(),
                timeSlot.end_time_hour(), timeSlot.end_time_minute(), timeSlot.days(),
                timeSlot.repeat_flag(), timeSlot.service_option(), timeSlot.activation_flag());

        // Then a task is saved in the repository and the view updated
        verify(mAppRepository).saveTimeSlot(any(TimeSlot.class)); // saved to the model
        verify(mAddEditTimeSlotView).showTimeSlotList(); // shown in the UI
    }

    @Test
    public void populateTimeSlot_callsRepoAndUpdatesViewOnSuccess() {
        when(mAppRepository.getTimeSlot(timeSlot._id())).thenReturn(Observable.just(timeSlot));

        // Get a reference to the class under test
        mAddEditTimeSlotPresenter = new AddEditTimeSlotPresenter(timeSlot._id(), mAppRepository, mSchedulerProvider);

        // Initialize the presenter
        mAddEditTimeSlotPresenter.create();
        mAddEditTimeSlotPresenter.attachView(mAddEditTimeSlotView);

        // Then the TimeSlot repository is queried and the view updated
        verify(mAppRepository).getTimeSlot(eq(timeSlot._id()));

        verify(mAddEditTimeSlotView).setTimeSlotFields(timeSlot);
        //assertThat(mAddEditTimeSlotPresenter.isDataMissing(), is(false));
    }

    @Test
    public void populateTimeSlot_callsRepoAndUpdatesViewOnError() {
        when(mAppRepository.getTimeSlot(timeSlot._id())).thenReturn(
                Observable.<TimeSlot>error(new NoSuchElementException()));

        // Get a reference to the class under test
        mAddEditTimeSlotPresenter = new AddEditTimeSlotPresenter(timeSlot._id(), mAppRepository, mSchedulerProvider);

        // Initialize the presenter
        mAddEditTimeSlotPresenter.create();
        mAddEditTimeSlotPresenter.attachView(mAddEditTimeSlotView);

        // Then the TimeSlot repository is queried and the view updated
        verify(mAppRepository).getTimeSlot(eq(timeSlot._id()));

        verify(mAddEditTimeSlotView).showMessage(
                eq(AddEditTimeSlotPresenter.MESSAGE_TYPE_FAIL_GET_TIME_SLOT_ERROR));
        verify(mAddEditTimeSlotView, never()).setTimeSlotFields(any(TimeSlot.class));
    }
}