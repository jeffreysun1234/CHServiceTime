package com.mycompany.chservicetime.data.source;

import android.content.Context;

import com.mycompany.chservicetime.model.TimeSlot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by szhx on 1/17/2017.
 */
public class AppRepositoryTest {

    final static TimeSlot TimeSlot1 = TimeSlot.createTimeSlot("111", "Work", "work time",
            9, 0, 17, 0, "0111110", true, false, TimeSlot.ServiceOption.NORMAL);
    final static TimeSlot TimeSlot2 = TimeSlot.createTimeSlot("222", "School", "school time",
            8, 30, 15, 0, "0111110", true, false, TimeSlot.ServiceOption.MUTE);
    final static TimeSlot TimeSlot3 = TimeSlot.createTimeSlot("333", "Test", "test",
            6, 20, 12, 22, "1100011", true, false, TimeSlot.ServiceOption.MUTE);

    private static List<TimeSlot> TIMESLOTS = Arrays.asList(TimeSlot1, TimeSlot2, TimeSlot3);

    private AppRepository mAppRepository;

    private TestSubscriber<List<TimeSlot>> mTimeSlotListTestSubscriber;

    @Mock
    private AppDataSource mTimeSlotLocalDataSource;

    @Mock
    private Context mContext;


    @Before
    public void setupTimeSlotRepository() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mAppRepository = AppRepository.getInstance(mTimeSlotLocalDataSource);

        mTimeSlotListTestSubscriber = new TestSubscriber<>();
    }

    @After
    public void destroyRepositoryInstance() {
        mAppRepository.destroyInstance();
    }

    @Test
    public void getTimeSlotList_repositoryCachesAfterFirstSubscription_whenTimeSlotListAvailableInLocalStorage() {
        // Given that the local data source has data available
        setTimeSlotListAvailable(mTimeSlotLocalDataSource, TIMESLOTS);

        // When two subscriptions are set
        TestSubscriber<List<TimeSlot>> testSubscriber1 = new TestSubscriber<>();
        mAppRepository.getAllTimeSlot().subscribe(testSubscriber1);

        TestSubscriber<List<TimeSlot>> testSubscriber2 = new TestSubscriber<>();
        mAppRepository.getAllTimeSlot().subscribe(testSubscriber2);

        // Then timeslots were only requested once from local sources
        verify(mTimeSlotLocalDataSource).getAllTimeSlot();
        assertFalse(mAppRepository.mCacheIsDirty);
        testSubscriber1.assertValue(TIMESLOTS);
        testSubscriber2.assertValue(TIMESLOTS);
    }

    @Test
    public void getTimeSlotList_requestsAllTimeSlotListFromLocalDataSource() {
        // Given that the local data source has data available
        setTimeSlotListAvailable(mTimeSlotLocalDataSource, TIMESLOTS);

        // When timeslots are requested from the timeslots repository
        mAppRepository.getAllTimeSlot().subscribe(mTimeSlotListTestSubscriber);

        // Then timeslots are loaded from the local data source
        verify(mTimeSlotLocalDataSource).getAllTimeSlot();
        mTimeSlotListTestSubscriber.assertValue(TIMESLOTS);
    }

    @Test
    public void saveTimeSlot_savesTimeSlotToServiceAPI() {
        // Given a stub timeslot with title and description
        TimeSlot newTimeSlot = TimeSlot1.toBuilder().build();

        // When a timeslot is saved to the timeslots repository
        mAppRepository.saveTimeSlot(newTimeSlot);

        // Then the service API and persistent repository are called and the cache is updated
        verify(mTimeSlotLocalDataSource).saveTimeSlot(newTimeSlot);
        assertThat(mAppRepository.mCachedTimeSlots.size(), is(1));
    }

    @Test
    public void updateActivationFlag_activatesTimeSlotToServiceAPIUpdatesCache() {
        // Given a stub timeslot with not activated in the repository
        TimeSlot newTimeSlot = TimeSlot1.toBuilder().activation_flag(false).build();
        mAppRepository.saveTimeSlot(newTimeSlot);
        assertThat(mAppRepository.mCachedTimeSlots.containsKey(newTimeSlot._id()), is(true));

        // make sure the repository is updated successfully.
        when(mTimeSlotLocalDataSource.updateActivationFlag(eq(newTimeSlot._id()), eq(true)))
                .thenReturn(1);

        // When a timeslot is activated to the timeslots repository
        mAppRepository.updateActivationFlag(newTimeSlot._id(), true);

        // Then persistent repository are called and the cache is updated
        verify(mTimeSlotLocalDataSource).updateActivationFlag(eq(newTimeSlot._id()), eq(true));
        assertThat(mAppRepository.mCachedTimeSlots.size(), is(1));
        assertThat(mAppRepository.mCachedTimeSlots.get(newTimeSlot._id()).activation_flag(), is(true));
    }

    @Test
    public void getTimeSlot_requestsSingleTimeSlotFromLocalDataSource() {
        // Given a stub timeslot in the local repository
        TimeSlot timeslot = TimeSlot1.toBuilder().build();
        setTimeSlotAvailable(mTimeSlotLocalDataSource, timeslot);

        // When a timeslot is requested from the timeslots repository
        TestSubscriber<TimeSlot> testSubscriber = new TestSubscriber<>();
        mAppRepository.getTimeSlot(timeslot._id()).subscribe(testSubscriber);

        // Then the timeslot is loaded from the database
        verify(mTimeSlotLocalDataSource).getTimeSlot(eq(timeslot._id()));
        testSubscriber.assertValue(timeslot);
    }

    @Test
    public void getTimeSlot_whenDataNotLocal_fails() {
        // Given a stub timeslot.
        TimeSlot timeslot = TimeSlot1.toBuilder().build();
        // And the timeslot not available in the local repository
        setTimeSlotNotAvailable(mTimeSlotLocalDataSource, timeslot._id());

        // When a timeslot is requested from the timeslots repository
        TestSubscriber<TimeSlot> testSubscriber = new TestSubscriber<>();
        mAppRepository.getTimeSlot(timeslot._id()).subscribe(testSubscriber);

        // Verify no data is returned
        testSubscriber.assertNoValues();
        // Verify that error is returned
        testSubscriber.assertError(NoSuchElementException.class);
    }

    @Test
    public void deleteAllTimeSlotList_deleteTimeSlotListToServiceAPIUpdatesCache() {
        // Given 3 stub timeslots in the repository
        mAppRepository.saveTimeSlot(TimeSlot1);
        mAppRepository.saveTimeSlot(TimeSlot2);
        mAppRepository.saveTimeSlot(TimeSlot3);

        assertThat(mAppRepository.mCachedTimeSlots.size(), is(3));

        // When all timeslots are deleted to the timeslots repository
        mAppRepository.deleteAllTimeSlot();

        // Verify the data sources were called
        verify(mTimeSlotLocalDataSource).deleteAllTimeSlot();

        assertThat(mAppRepository.mCachedTimeSlots.size(), is(0));
    }

    @Test
    public void deleteTimeSlot_deleteTimeSlotToServiceAPIRemovedFromCache() {
        // Given a timeslot in the repository
        TimeSlot newTimeSlot = TimeSlot1.toBuilder().build();
        mAppRepository.saveTimeSlot(newTimeSlot);
        assertThat(mAppRepository.mCachedTimeSlots.containsKey(newTimeSlot._id()), is(true));

        // When deleted
        mAppRepository.deleteTimeSlot(newTimeSlot._id());

        // Verify the data sources were called
        verify(mTimeSlotLocalDataSource).deleteTimeSlot(newTimeSlot._id());

        // Verify it's removed from repository
        assertThat(mAppRepository.mCachedTimeSlots.containsKey(newTimeSlot._id()), is(false));
    }

    @Test
    public void getTimeSlotListWithDirtyCache_timeslotsAreRetrievedFromLocal() {
        // Given that the remote data source has data available
        setTimeSlotListAvailable(mTimeSlotLocalDataSource, TIMESLOTS);

        // When calling getAllTimeSlot in the repository with dirty cache
        mAppRepository.refreshTimeSlots();
        mAppRepository.getAllTimeSlot().subscribe(mTimeSlotListTestSubscriber);

        // Verify the timeslots from the local data source are returned
        verify(mTimeSlotLocalDataSource).getAllTimeSlot();
        mTimeSlotListTestSubscriber.assertValue(TIMESLOTS);
    }

    @Test
    public void getTimeSlotListWithLocalDataSourcesUnavailable_firesOnDataUnavailable() {
        // Given that the local data source has no data available
        setTimeSlotListNotAvailable(mTimeSlotLocalDataSource);

        // When calling getAllTimeSlot in the repository
        mAppRepository.getAllTimeSlot().subscribe(mTimeSlotListTestSubscriber);

        // Verify no data is returned
        mTimeSlotListTestSubscriber.assertNoValues();
        // Verify that error is returned
        mTimeSlotListTestSubscriber.assertError(NoSuchElementException.class);
    }

    @Test
    public void getTimeSlotList_refreshesLocalDataSource() {
        // Given that the local data source has data available
        setTimeSlotListAvailable(mTimeSlotLocalDataSource, TIMESLOTS);

        // Mark cache as dirty to force a reload of data from remote data source.
        mAppRepository.refreshTimeSlots();

        // When calling getAllTimeSlot in the repository
        mAppRepository.getAllTimeSlot().subscribe(mTimeSlotListTestSubscriber);

        // Verify that the data fetched from the local data source was saved in cache.
        assertEquals(mAppRepository.mCachedTimeSlots.size(), TIMESLOTS.size());
        mTimeSlotListTestSubscriber.assertValue(TIMESLOTS);
    }

    private void setTimeSlotListNotAvailable(AppDataSource dataSource) {
        //when(dataSource.getAllTimeSlot()).thenReturn(Observable.just(Collections.<TimeSlot>emptyList()));
        when(dataSource.getAllTimeSlot()).thenReturn(Observable.just(null));
    }

    private void setTimeSlotListAvailable(AppDataSource dataSource, List<TimeSlot> timeslots) {
        // don't allow the data sources to complete.
        when(dataSource.getAllTimeSlot()).thenReturn(Observable.just(timeslots).concatWith(Observable.<List<TimeSlot>>never()));
    }

    private void setTimeSlotNotAvailable(AppDataSource dataSource, String timeslotId) {
        when(dataSource.getTimeSlot(eq(timeslotId))).thenReturn(Observable.<TimeSlot>just(null).concatWith(Observable.<TimeSlot>never()));
    }

    private void setTimeSlotAvailable(AppDataSource dataSource, TimeSlot timeslot) {
        when(dataSource.getTimeSlot(eq(timeslot._id()))).thenReturn(Observable.just(timeslot).concatWith(Observable.<TimeSlot>never()));
    }
}