package com.mycompany.chservicetime.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mycompany.chservicetime.data.source.AppDataSource;
import com.mycompany.chservicetime.model.TimeSlot;
import com.mycompany.chservicetime.util.schedulers.BaseSchedulerProvider;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation of a data source as a db.
 */
public class AppLocalDataSource implements AppDataSource {

    @Nullable
    private static AppLocalDataSource INSTANCE;

    @NonNull
    private final BriteDatabase mBriteDB;

    private final AppDatabaseOpenHelper dbHelper;

    @NonNull
    private Func1<Cursor, TimeSlot> mTimeSlotMapperFunction;

    // Prevent direct instantiation.
    private AppLocalDataSource(@NonNull Context context,
                               @NonNull BaseSchedulerProvider schedulerProvider) {
        checkNotNull(context, "context cannot be null");
        checkNotNull(schedulerProvider, "scheduleProvider cannot be null");
        dbHelper = AppDatabaseOpenHelper.getInstance(context);
        dbHelper.getWritableDatabase();
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        mBriteDB = sqlBrite.wrapDatabaseHelper(dbHelper, schedulerProvider.io());

        mTimeSlotMapperFunction = new Func1<Cursor, TimeSlot>() {
            @Override
            public TimeSlot call(Cursor cursor) {
                TimeSlot timeSlot = TimeSlot.MAPPER.map(cursor);
                return timeSlot;
            }
        };
    }

    public static AppLocalDataSource getInstance(
            @NonNull Context context,
            @NonNull BaseSchedulerProvider schedulerProvider) {
        if (INSTANCE == null) {
            INSTANCE = new AppLocalDataSource(context, schedulerProvider);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public Observable<List<TimeSlot>> getAllTimeSlot() {
        return mBriteDB.createQuery(TimeSlot.TABLE_NAME, TimeSlot.SELECT_ALL)
                .mapToList(mTimeSlotMapperFunction);
    }

    @Override
    public Observable<TimeSlot> getTimeSlot(@NonNull String id) {
        return mBriteDB.createQuery(TimeSlot.TABLE_NAME, TimeSlot.SELECT_BY_ID, id)
                .mapToOneOrDefault(mTimeSlotMapperFunction, null);
    }

    @Override
    public void saveTimeSlot(@NonNull TimeSlot timeSlot) {
        checkNotNull(timeSlot);
        ContentValues values = TimeSlot.getMarshal(timeSlot).asContentValues();

        mBriteDB.insert(TimeSlot.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Override
    public int updateActivationFlag(@NonNull String id, boolean activationFlag) {
        TimeSlot.Update_activation_flag updateActivationFlag =
                new TimeSlot.Update_activation_flag(dbHelper.getWritableDatabase());
        updateActivationFlag.bind(activationFlag, id);

        return mBriteDB.executeUpdateDelete(TimeSlot.TABLE_NAME, updateActivationFlag.program);
    }

    @Override
    public void deleteAllTimeSlot() {
        mBriteDB.delete(TimeSlot.TABLE_NAME, null);
    }

    @Override
    public void deleteTimeSlot(@NonNull String id) {
        TimeSlot.Delete_by_id deleteById = new TimeSlot.Delete_by_id(dbHelper.getWritableDatabase());
        deleteById.bind(id);

        mBriteDB.executeUpdateDelete(TimeSlot.TABLE_NAME, deleteById.program);
    }

    @Override
    public void refreshTimeSlots() {
        // Not required because the {@link AppRepository} handles the logic of refreshing the
        // TimeSlots from all the available data sources.
    }
}

