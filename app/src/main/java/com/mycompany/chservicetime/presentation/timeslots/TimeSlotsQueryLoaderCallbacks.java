package com.mycompany.chservicetime.presentation.timeslots;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.mycompany.chservicetime.data.source.local.CHServiceTimeContract;

/**
 * Cursor loader interface's implements
 */
public class TimeSlotsQueryLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

    boolean mShowLoadingIndicator;
    Context mContext;
    TimeSlotsContract.View mTimeSlotsView;

    /**
     * @param showLoadingIndicator Pass in true to display a loading icon in the UI
     */
    public TimeSlotsQueryLoaderCallbacks(Context mContext, TimeSlotsContract.View mTimeSlotsView, boolean showLoadingIndicator) {
        this.mContext = mContext;
        this.mTimeSlotsView = mTimeSlotsView;
        this.mShowLoadingIndicator = showLoadingIndicator;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mShowLoadingIndicator) {
            mTimeSlotsView.setLoadingIndicator(true);
        }

        return new CursorLoader(mContext, CHServiceTimeContract.TimeSlots.buildTimeSlotsUri(),
                CHServiceTimeContract.TimeSlots.DEFAULT_PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // The view may not be able to handle UI updates anymore
        if (!mTimeSlotsView.isActive()) {
            return;
        }

        if (mShowLoadingIndicator) {
            mTimeSlotsView.setLoadingIndicator(false);
        }

        //check if show empty view
        if (data == null || data.getCount() == 0) {
            mTimeSlotsView.showNoTimeSlots();
        } else {
            mTimeSlotsView.showTimeSlots(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // no-op
    }
}
