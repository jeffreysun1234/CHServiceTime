package com.mycompany.chservicetime.di.module;

import android.content.Context;

import com.mycompany.chservicetime.util.schedulers.BaseSchedulerProvider;
import com.mycompany.chservicetime.util.schedulers.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. We use this to pass in the Context dependency to the
 */
@Module
public final class ApplicationModule {

    private final Context mContext;

    public ApplicationModule(Context context) {
        mContext = context;
    }

    @Provides
    public Context provideContext() {
        return mContext;
    }

    @Provides
    public BaseSchedulerProvider provideSchedulerProvider() {
        return SchedulerProvider.getInstance();
    }
}