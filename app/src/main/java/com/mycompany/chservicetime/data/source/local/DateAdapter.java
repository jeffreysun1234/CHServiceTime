package com.mycompany.chservicetime.data.source.local;

import android.support.annotation.NonNull;

import com.squareup.sqldelight.ColumnAdapter;

import java.util.Calendar;

/**
 * Created by szhx on 11/27/2016.
 */

public class DateAdapter implements ColumnAdapter<Calendar, Long> {
    @NonNull
    @Override
    public Calendar decode(Long databaseValue) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(databaseValue);
        return calendar;
    }

    @Override
    public Long encode(@NonNull Calendar value) {
        return value.getTimeInMillis();
    }
}
