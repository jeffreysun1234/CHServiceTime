package com.mycompany.chservicetime.presentation.timeslots;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.mycompany.chservicetime.data.source.MockCursorProvider;
import com.mycompany.chservicetime.data.source.TimeSlotDataSource;
import com.mycompany.chservicetime.data.source.TimeSlotRepository;
import com.mycompany.chservicetime.model.TimeSlot;
import com.mycompany.chservicetime.presentation.timeslots.TimeSlotsContract;
import com.mycompany.chservicetime.presentation.timeslots.TimeSlotsPresenter;
import com.mycompany.chservicetime.presentation.timeslots.TimeSlotsQueryLoaderCallbacks;
import com.mycompany.chservicetime.util.LogUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doNothing;

/**
 * Unit tests for the implementation of {@link TimeSlotsPresenter}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(LogUtils.class)
public class TimeSlotsPresenterTest {
    /**
     * {@link ArgumentCaptor} is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private ArgumentCaptor<Cursor> mShowTimeSlotsArgumentCaptor;

    @Mock
    private TimeSlotsContract.View mTimeSlotsView;

    @Mock
    private TimeSlotRepository mTimeSlotRepository;

    @Mock
    private LoaderManager mLoaderManager;

    @Mock
    private Bundle mBundle;

    @Mock
    private Context mContext;

    @Mock
    private Log mAndroidLog;

    @Mock
    private TimeSlotDataSource.LoadTimeSlotsCallback mLoadTimeSlotsCallback;

    private MockCursorProvider.TimeSlotMockCursor mAllTimeSlotsCursor;
    private MockCursorProvider.TimeSlotMockCursor mEmptyTimeSlotsCursor;

    private TimeSlotsPresenter mTimeSlotsPresenter;
    private TimeSlotsQueryLoaderCallbacks mTimeSlotsQueryLoaderCallbacks;

    @Captor
    private ArgumentCaptor<TimeSlotDataSource.LoadTimeSlotsCallback> loadTimeSlotsCallbackArgumentCaptor;

    @Before
    public void setupTimeSlotsPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mTimeSlotsPresenter = new TimeSlotsPresenter(mTimeSlotsView, mTimeSlotRepository, mLoaderManager);

        // Mock cursor.
        mAllTimeSlotsCursor = MockCursorProvider.createAllTimeSlotsCursor();
        mEmptyTimeSlotsCursor = MockCursorProvider.createEmptyTimeSlotsCursor();

        mTimeSlotsQueryLoaderCallbacks = new TimeSlotsQueryLoaderCallbacks(mContext, mTimeSlotsView, true);

        // The presenter wont't update the view unless it's active.
        when(mTimeSlotsView.isActive()).thenReturn(true);

        // mock all the static methods in LogUtils
        PowerMockito.mockStatic(LogUtils.class);
        // use Mockito to set up your expectation
        // stubbing void methods
        try {
            PowerMockito.doNothing().when(LogUtils.class, "LOGD", any(String.class), any(String.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadAllTimeSlotsRefreshesDataFromRepository() {
        mTimeSlotsPresenter.loadTimeSlots(false);

        verify(mLoaderManager).initLoader(anyInt(), any(Bundle.class), any(LoaderManager.LoaderCallbacks.class));

    }

    @Test
    public void loadAllTimeSlotsFromRepositoryAndLoadIntoView() {
        mTimeSlotsQueryLoaderCallbacks.onLoadFinished(mock(Loader.class), mAllTimeSlotsCursor);

        // Then progress indicator is hidden and all timeSlots are shown in UI
        verify(mTimeSlotsView).setLoadingIndicator(false);
        verify(mTimeSlotsView).showTimeSlots(mShowTimeSlotsArgumentCaptor.capture());
        assertThat(mShowTimeSlotsArgumentCaptor.getValue().getCount(), is(3));
    }

    @Test
    public void loadAllTimeSlotsReturnsNothingShowsEmptyMessage() {
        mTimeSlotsQueryLoaderCallbacks.onLoadFinished(mock(Loader.class), mEmptyTimeSlotsCursor);

        // Then progress indicator is hidden and timeSlots are shown in UI
        verify(mTimeSlotsView).setLoadingIndicator(false);
        verify(mTimeSlotsView).showNoTimeSlots();
    }

    @Test
    public void unavailableTimeSlots_ShowsEmptyMessage() {
        mTimeSlotsQueryLoaderCallbacks.onLoadFinished(mock(Loader.class), null);

        // Then an empty message is shown
        verify(mTimeSlotsView).showNoTimeSlots();
    }

    @Test
    public void clickAddIcon_ShowsAddTimeSlotUi() {
        // When adding a new timeSlot
        mTimeSlotsPresenter.addNewTimeSlot();

        // Then add timeSlot UI is shown
        verify(mTimeSlotsView).showAddTimeSlotUI();
    }

    @Test
    public void clickOnTimeSlot_ShowsDetailUi() {
        // Given a stubbed active timeSlot
        TimeSlot requestedTimeSlot = new TimeSlot("1-1", "Work", "Work Time", 9, 0, 17, 0, "1000001", false);

        // When open timeSlot details is requested
        mTimeSlotsPresenter.openTimeSlotDetail(requestedTimeSlot.timeSlotId);

        // Then timeSlot detail UI is shown
        verify(mTimeSlotsView).showEditTimeSlotUi(requestedTimeSlot.timeSlotId);
    }

    @Test
    public void activateTimeSlot_ShowsTimeSlotMarkedActive() {
        // Given a stubbed completed timeSlot
        TimeSlot timeSlot = new TimeSlot("1-1", "Work", "Work Time", 9, 0, 17, 0, "1000001", false);
        timeSlot.activationFlag = false;

        // When timeSlot is marked as activated
        mTimeSlotsPresenter.activateTimeSlot(timeSlot);

        // Then repository is called and timeSlot marked active UI is shown
        verify(mTimeSlotRepository).updateActivationFlag(timeSlot.timeSlotId, timeSlot.activationFlag);
        verify(mTimeSlotsView).showTimeSlotMarkedActive();
    }

}
