package com.mycompany.chservicetime.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.mycompany.chservicetime.di.component.AppRepositoryComponent;
import com.mycompany.chservicetime.di.module.AppRepositoryModule;
import com.mycompany.chservicetime.di.qualifier.Local;
import com.mycompany.chservicetime.model.TimeSlot;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation to load TimeSlots from the data sources into a cache.
 * <p>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 * <p>
 * By marking the constructor with {@code @Inject} and the class with {@code @Singleton}, Dagger
 * injects the dependencies required to create an instance of the TasksRespository (if it fails, it
 * emits a compiler error). It uses {@link AppRepositoryModule} to do so, and the constructed
 * instance is available in {@link AppRepositoryComponent}.
 * <p>
 * Dagger generated code doesn't require public access to the constructor or class, and
 * therefore, to ensure the developer doesn't instantiate the class manually and bypasses Dagger,
 * it's good practice minimise the visibility of the class/constructor as much as possible.
 */
@Singleton
public class AppRepository implements AppDataSource {

    @Nullable
    private static AppRepository INSTANCE = null;

    @NonNull
    private final AppDataSource mAppLocalDataSource;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    @VisibleForTesting
    @Nullable
    Map<String, TimeSlot> mCachedTimeSlots;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    @VisibleForTesting
    boolean mCacheIsDirty = false;

    /**
     * Returns the single instance of this class, creating it if necessary.
     * <p>
     * By marking the constructor with {@code @Inject}, Dagger will try to inject the dependencies
     * required to create an instance of the AppRepository. Because {@link AppDataSource} is an
     * interface, we must provide to Dagger a way to build those arguments, this is done in
     * {@link AppRepositoryModule}.
     * <p>
     * When two arguments or more have the same type, we must provide to Dagger a way to
     * differentiate them. This is done using a qualifier.
     * <p>
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    AppRepository(@Local @NonNull AppDataSource appLocalDataSource) {
        mAppLocalDataSource = appLocalDataSource;
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     */
    public static AppRepository getInstance(@NonNull AppDataSource appLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new AppRepository(appLocalDataSource);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(AppDataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Gets TimeSlots from cache or local data source (SQLite), whichever is
     * available first.
     */
    @Override
    public Observable<List<TimeSlot>> getAllTimeSlot() {
        // Respond immediately with cache if available and not dirty
        if (mCachedTimeSlots != null && !mCacheIsDirty) {
            return Observable.from(mCachedTimeSlots.values()).toList();
        } else {
            buildCachedTimeSlotsIfNotExist();
        }

        // Query the local storage if available.
        Observable<List<TimeSlot>> localTimeSlots = mAppLocalDataSource.getAllTimeSlot()
                .flatMap(timeSlots ->
                        Observable.from(timeSlots)
                                .doOnNext(timeSlot -> mCachedTimeSlots.put(timeSlot._id(), timeSlot))
                                .toList())
                .map(timeSlots -> {
                    if (timeSlots == null || timeSlots.isEmpty()) {
                        throw new NoSuchElementException("No timeSlot list found with timeSlotId ");
                    }
                    return timeSlots;
                })
                .doOnCompleted(() -> mCacheIsDirty = false)
                .first();

        return localTimeSlots;
    }

    /**
     * Gets TimeSlot from local data source (sqlite).
     */
    @Override
    public Observable<TimeSlot> getTimeSlot(@NonNull final String id) {
        checkNotNull(id);

        final TimeSlot cachedTimeSlot = getTimeSlotWithId(id);

        // Respond immediately with cache if available
        if (cachedTimeSlot != null) {
            return Observable.just(cachedTimeSlot);
        }

        // Do in memory cache update to keep the app UI up to date
        buildCachedTimeSlotsIfNotExist();

        Observable<TimeSlot> localTimeSlot = mAppLocalDataSource
                .getTimeSlot(id)
                .map(timeSlot -> {
                    if (timeSlot == null) {
                        throw new NoSuchElementException("No timeSlot found with timeSlotId " + id);
                    }
                    return timeSlot;
                })
                .doOnNext(timeSlot -> mCachedTimeSlots.put(timeSlot._id(), timeSlot));

        return localTimeSlot;
    }

    /**
     * The function will automatically decide if Insert or Update the data.
     *
     * @param timeSlot
     */
    @Override
    public void saveTimeSlot(@NonNull TimeSlot timeSlot) {
        checkNotNull(timeSlot);
        mAppLocalDataSource.saveTimeSlot(timeSlot);

        // Do in memory cache update to keep the app UI up to date
        buildCachedTimeSlotsIfNotExist();
        mCachedTimeSlots.put(timeSlot._id(), timeSlot);
    }

    public int updateActivationFlag(@NonNull String id, boolean activationFlag) {
        checkNotNull(id);
        int rows = mAppLocalDataSource.updateActivationFlag(id, activationFlag);
        buildCachedTimeSlotsIfNotExist();

        if (rows > 0) {
            TimeSlot timeSlot = mCachedTimeSlots.get(id);
            TimeSlot newTimeSlot = timeSlot.toBuilder().activation_flag(activationFlag).build();
            mCachedTimeSlots.put(id, newTimeSlot);
        }

        return rows;
    }

    @Override
    public void deleteAllTimeSlot() {
        mAppLocalDataSource.deleteAllTimeSlot();

        buildCachedTimeSlotsIfNotExist();
        mCachedTimeSlots.clear();
    }

    @Override
    public void deleteTimeSlot(@NonNull String id) {
        mAppLocalDataSource.deleteTimeSlot(id);

        mCachedTimeSlots.remove(id);
    }

    @Override
    public void refreshTimeSlots() {
        mCacheIsDirty = true;
    }

    private void buildCachedTimeSlotsIfNotExist() {
        // Do in memory cache update to keep the app UI up to date
        if (mCachedTimeSlots == null) {
            mCachedTimeSlots = new LinkedHashMap<>();
        }
    }

    @Nullable
    private TimeSlot getTimeSlotWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedTimeSlots == null || mCachedTimeSlots.isEmpty()) {
            return null;
        } else {
            return mCachedTimeSlots.get(id);
        }
    }
}
