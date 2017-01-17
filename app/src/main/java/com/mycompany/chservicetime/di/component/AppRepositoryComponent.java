package com.mycompany.chservicetime.di.component;

import com.mycompany.chservicetime.CHApplication;
import com.mycompany.chservicetime.data.source.AppRepository;
import com.mycompany.chservicetime.di.module.AppRepositoryModule;
import com.mycompany.chservicetime.di.module.ApplicationModule;
import com.mycompany.chservicetime.util.schedulers.BaseSchedulerProvider;

import javax.inject.Singleton;

import dagger.Component;

/**
 * This is a Dagger component. Refer to {@link CHApplication} for the list of Dagger components
 * used in this application.
 * <p>
 * Even though Dagger allows annotating a {@link Component @Component} as a singleton, the code
 * itself must ensure only one instance of the class is created. This is done in {@link
 * CHApplication}.
 */
@Singleton
@Component(dependencies = ApplicationModule.class, modules = AppRepositoryModule.class)
public interface AppRepositoryComponent {

    // downstream components need these exposed with the return type
    // method name does not really matter
    AppRepository getAppRepository();

    BaseSchedulerProvider getSchedulerProvider();
}
