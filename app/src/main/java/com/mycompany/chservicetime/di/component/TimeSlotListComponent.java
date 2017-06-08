package com.mycompany.chservicetime.di.component;

import com.mycompany.chservicetime.CHApplication;
import com.mycompany.chservicetime.di.scope.FragmentScope;
import com.mycompany.chservicetime.presentation.timeslotlist.TimeSlotListPresenter;

import dagger.Component;

/**
 * This is a Dagger component. Refer to {@link CHApplication} for the list of Dagger components
 * used in this application.
 * <P>
 * Because this component depends on the {@link AppRepositoryComponent}, which is a singleton, a
 * scope must be specified. All fragment components use a custom scope for this purpose.
 */
@FragmentScope
@Component(dependencies = AppRepositoryComponent.class)
public interface TimeSlotListComponent {

    void inject(TimeSlotListPresenter timeSlotListPresenter);

    TimeSlotListPresenter getTimeSlotListPresenter();
}
