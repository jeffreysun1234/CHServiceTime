package com.mycompany.chservicetime.di.module;

import android.content.Context;

import com.mycompany.chservicetime.data.source.AppDataSource;
import com.mycompany.chservicetime.data.source.AppRepository;
import com.mycompany.chservicetime.data.source.local.AppLocalDataSource;
import com.mycompany.chservicetime.di.qualifier.Local;
import com.mycompany.chservicetime.util.schedulers.BaseSchedulerProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * This is used by Dagger to inject the required arguments into the {@link AppRepository}.
 */
@Module
public class AppRepositoryModule {

    @Singleton
    @Provides
    @Local
    AppDataSource provideAppLocalDataSource(Context context, BaseSchedulerProvider schedulerProvider) {
        return AppLocalDataSource.getInstance(context, schedulerProvider);
    }
}
