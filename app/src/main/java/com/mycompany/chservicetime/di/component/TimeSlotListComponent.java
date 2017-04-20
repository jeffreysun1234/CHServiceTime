package com.mycompany.chservicetime.di.component;

import com.mycompany.chservicetime.di.scope.FragmentScope;
import com.mycompany.chservicetime.presentation.timeslotlist.TimeSlotListPresenter;

import dagger.Component;

/**
 * Created by szhx on 12/10/2016.
 */

@FragmentScope
@Component(dependencies = AppRepositoryComponent.class)
public interface TimeSlotListComponent {
    void inject(TimeSlotListPresenter timeSlotListPresenter);

    TimeSlotListPresenter getTimeSlotListPresenter();
}
