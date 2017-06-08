package com.mycompany.chservicetime.presentation.timeslotlist;

import com.mycompany.chservicetime.data.source.AppRepository;
import com.mycompany.chservicetime.model.TimeSlot;
import com.mycompany.chservicetime.util.schedulers.BaseSchedulerProvider;
import com.mycompany.chservicetime.util.schedulers.ImmediateSchedulerProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.Observable;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by szhx on 1/18/2017.
 */
public class TimeSlotListPresenterTest {

    @Mock
    private AppRepository mAppRepository;

    @Mock
    private TimeSlotListView mTimeSlotListView;

    private BaseSchedulerProvider mSchedulerProvider;

    private TimeSlotListPresenter mTimeSlotListPresenter;

    final static TimeSlot TimeSlot1 = TimeSlot.createTimeSlot("111", "Work", "work time",
            9, 0, 17, 0, "0111110", true, false, TimeSlot.ServiceOption.NORMAL);
    final static TimeSlot TimeSlot2 = TimeSlot.createTimeSlot("222", "School", "school time",
            8, 30, 15, 0, "0111110", true, false, TimeSlot.ServiceOption.MUTE);
    final static TimeSlot TimeSlot3 = TimeSlot.createTimeSlot("333", "Test", "test",
            6, 20, 12, 22, "1100011", true, false, TimeSlot.ServiceOption.MUTE);

    List<TimeSlot> TIMESLOTS;

    @Before
    public void setupTimeSlotListPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Make the sure that all schedulers are immediate.
        mSchedulerProvider = new ImmediateSchedulerProvider();

        // Get a reference to the class under test
        mTimeSlotListPresenter = new TimeSlotListPresenter(mAppRepository, mSchedulerProvider);
        mTimeSlotListPresenter.create();

        TIMESLOTS = Arrays.asList(TimeSlot1, TimeSlot2, TimeSlot3);
    }

    @Test
    public void loadAllTimeSlotListFromRepositoryAndLoadIntoView() {
        // Given an initialized TimeSlotListPresenter with initialized timeSlots
        when(mAppRepository.getAllTimeSlot()).thenReturn(Observable.just(TIMESLOTS));

        // When loading of TimeSlotList is requested
        mTimeSlotListPresenter.setForceUpdate(true);

        mTimeSlotListPresenter.attachView(mTimeSlotListView);

        // call Reposiotry
        verify(mAppRepository).getAllTimeSlot();

        InOrder inOrder = Mockito.inOrder(mTimeSlotListView);
        // Then progress indicator is shown
        verify(mTimeSlotListView).setLoadingIndicator(true);
        // Then progress indicator is hidden and all timeSlots are shown in UI
        verify(mTimeSlotListView).setLoadingIndicator(false);

        ArgumentCaptor<List> showTimeSlotsArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mTimeSlotListView).showTimeSlots(showTimeSlotsArgumentCaptor.capture());
        assertTrue(showTimeSlotsArgumentCaptor.getValue().size() == 3);
    }

    @Test
    public void loadAllTimeSlotsReturnsNothingShowsEmptyMessage() {
        // Given that timeSlots are empty in the repository
        when(mAppRepository.getAllTimeSlot())
                .thenReturn(Observable.<List<TimeSlot>>just(Collections.emptyList()));

        // When timeSlots are loaded
        mTimeSlotListPresenter.setForceUpdate(true);
        mTimeSlotListPresenter.attachView(mTimeSlotListView);

        // Then progress indicator is hidden and timeSlots are shown in UI
        verify(mTimeSlotListView).setLoadingIndicator(false);
        verify(mTimeSlotListView).showNoTimeSlots();
    }

    @Test
    public void clickOnAddIcon_ShowsAddTimeSlotUi() {
        when(mAppRepository.getAllTimeSlot()).thenReturn(Observable.just(TIMESLOTS));
        mTimeSlotListPresenter.attachView(mTimeSlotListView);

        // When adding a new timeSlot
        mTimeSlotListPresenter.addEditTimeSlot(null);

        // Then add timeSlot UI is shown
        verify(mTimeSlotListView).showAddEditTimeSlot(null);
    }

    @Test
    public void clickOnTimeSlot_ShowsEditTimeSlotUi() {
        when(mAppRepository.getAllTimeSlot()).thenReturn(Observable.just(TIMESLOTS));
        mTimeSlotListPresenter.attachView(mTimeSlotListView);

        // Given a stubbed active timeSlot
        TimeSlot requestedTimeSlot = TimeSlot1.toBuilder().build();

        // When open timeSlot details is requested
        mTimeSlotListPresenter.addEditTimeSlot(requestedTimeSlot._id());

        // Then timeSlot detail UI is shown
        verify(mTimeSlotListView).showAddEditTimeSlot(any(String.class));
    }

    @Test
    public void activateTimeSlot_ShowsTimeSlotMarkedActive() {
        // Given a stubbed completed timeSlot
        TimeSlot timeSlot = TimeSlot1.toBuilder().build();

        // And no timeSlots available in the repository
        when(mAppRepository.getAllTimeSlot()).thenReturn(Observable.<List<TimeSlot>>empty());
        when(mAppRepository.updateActivationFlag(timeSlot._id(), true)).thenReturn(1);
        mTimeSlotListPresenter.setForceUpdate(true);
        mTimeSlotListPresenter.attachView(mTimeSlotListView);

        // When timeSlot is marked as activated
        mTimeSlotListPresenter.activateTimeSlot(timeSlot._id(), true);

        // Then repository is called and timeSlot marked active UI is shown
        verify(mAppRepository).updateActivationFlag(eq(timeSlot._id()), eq(true));
        verify(mTimeSlotListView).showTimeSlotActivationFlagMessage(eq(true));
    }

    @Test
    public void errorLoadingTimeSlotList_ShowsError() {
        // Given that no timeSlots are available in the repository
        when(mAppRepository.getAllTimeSlot()).thenReturn(Observable.<List<TimeSlot>>error(new Exception()));

        // When timeSlots are loaded
        mTimeSlotListPresenter.setForceUpdate(true);
        mTimeSlotListPresenter.attachView(mTimeSlotListView);

        // Then an error message is shown
        verify(mTimeSlotListView).showLoadingTimeSlotsError();
    }
}