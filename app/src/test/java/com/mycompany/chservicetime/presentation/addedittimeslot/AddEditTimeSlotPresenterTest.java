package com.mycompany.chservicetime.presentation.addedittimeslot;

import com.mycompany.chservicetime.data.source.TimeSlotDataSource;
import com.mycompany.chservicetime.data.source.TimeSlotRepository;
import com.mycompany.chservicetime.model.TimeSlot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by szhx on 8/20/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddEditTimeSlotPresenterTest {

    @Mock
    private TimeSlotRepository mTimeSlotRepository;

    @Mock
    private AddEditTimeSlotContract.View mAddEditTimeSlotView;

    /**
     * {@link ArgumentCaptor} is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private ArgumentCaptor<TimeSlotDataSource.GetTimeSlotCallback> mGetTimeSlotCallbackCaptor;

    private AddEditTimeSlotPresenter mAddEditTimeSlotPresenter;

    @Before
    public void setUp() throws Exception {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // The presenter wont't update the view unless it's active.
        when(mAddEditTimeSlotView.isActive()).thenReturn(true);
    }

    @Test
    public void saveNewTimeSlotToRepository_showsSuccessUi() {
        // Get a reference to the class under test
        mAddEditTimeSlotPresenter = givenAddEditTimeSlotPresenter(null);

        // When the presenter is asked to save a timeSlot
        mAddEditTimeSlotPresenter.createOrUpdateTimeSlot(null, "Work", "This is for work time.",
                9, 0, 17, 0, "0111110", true);

        // Then a timeSlot is saved in the repository and the view updated
        verify(mTimeSlotRepository).createOrUpdateTimeSlot(any(TimeSlot.class)); // saved to the model
        verify(mAddEditTimeSlotView).finishView(); // finish the current view.
    }

    @Test
    public void saveTimeSlot_emptyTimeSlotShowsErrorMessageUi() {
        // Get a reference to the class under test
        mAddEditTimeSlotPresenter = givenAddEditTimeSlotPresenter(null);

        // When the presenter is asked to save an error TimeSlot which name is empty.
        mAddEditTimeSlotPresenter.createOrUpdateTimeSlot(null, "", "", 9, 0, 17, 0, "0111110", true);

        // Then an empty not error is shown in the UI
        verify(mAddEditTimeSlotView).showError(any(Integer.class));
    }

    @Test
    public void saveExistingTimeSlotToRepository_showsSuccessMessageUi() {
        String timeSlotId = "1";

        // Get a reference to the class under test
        mAddEditTimeSlotPresenter = givenAddEditTimeSlotPresenter(timeSlotId);

        // When the presenter is asked to save an existing timeSlot
        mAddEditTimeSlotPresenter.createOrUpdateTimeSlot(timeSlotId, "Test", "", 9, 0, 17, 0, "0111110", true);

        // Then a timeSlot is saved in the repository and the view updated
        verify(mTimeSlotRepository).createOrUpdateTimeSlot(any(TimeSlot.class)); // saved to the model
        verify(mAddEditTimeSlotView).finishView(); // finish the current view.
    }

    @Test
    public void populateTimeSlot_callsRepoAndUpdatesView() {
        TimeSlot testTimeSlot = new TimeSlot("1-1", "Test", "", 9, 0, 17, 0, "0111110", true);

        // Get a reference to the class under test
        mAddEditTimeSlotPresenter = givenAddEditTimeSlotPresenter(testTimeSlot.timeSlotId);

        // When the presenter is asked to populate an existing timeSlot
        mAddEditTimeSlotPresenter.populateTimeSlot();

        // Then the timeSlot repository is queried and the view updated
        verify(mTimeSlotRepository).getTimeSlot(eq(testTimeSlot.timeSlotId), mGetTimeSlotCallbackCaptor.capture());

        // Simulate callback
        mGetTimeSlotCallbackCaptor.getValue().onTimeSlotLoaded(testTimeSlot);

        verify(mAddEditTimeSlotView).setTimeSlotFields(testTimeSlot);
    }

    private AddEditTimeSlotPresenter givenAddEditTimeSlotPresenter(String timeSlotId) {
        return new AddEditTimeSlotPresenter(timeSlotId, mTimeSlotRepository, mAddEditTimeSlotView);
    }
}