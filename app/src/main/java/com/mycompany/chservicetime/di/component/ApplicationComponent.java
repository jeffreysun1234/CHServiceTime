package com.mycompany.chservicetime.di.component;

import android.content.Context;

import com.mycompany.chservicetime.di.module.ApplicationModule;
import com.mycompany.chservicetime.util.schedulers.BaseSchedulerProvider;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by szhx on 12/5/2016.
 */

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    Context getContext();

    BaseSchedulerProvider getSchedulerProvider();
}
