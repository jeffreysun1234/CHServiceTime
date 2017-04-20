package com.mycompany.chservicetime.di.module;

import android.support.annotation.Nullable;

import com.mycompany.chservicetime.presentation.addedittimeslot.AddEditTimeSlotPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link AddEditTimeSlotPresenter}.
 */
@Module
public class AddEditTimeSlotPresenterModule {

    private String mId;

    public AddEditTimeSlotPresenterModule(@Nullable String id) {
        mId = id;
    }

    @Provides
    @Nullable
    String provideId() {
        return mId;
    }
}
