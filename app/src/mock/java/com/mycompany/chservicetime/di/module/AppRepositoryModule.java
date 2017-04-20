package com.mycompany.chservicetime.di.module;

import com.mycompany.chservicetime.data.source.AppDataSource;
import com.mycompany.chservicetime.data.source.FakeAppDataSource;
import com.mycompany.chservicetime.di.qualifier.Local;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppRepositoryModule {

    @Singleton
    @Provides
    @Local
    AppDataSource provideAppLocalDataSource() {
        return FakeAppDataSource.getInstance();
    }
}
