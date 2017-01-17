package com.mycompany.chservicetime.di.component;


import com.mycompany.chservicetime.di.module.AddEditTimeSlotPresenterModule;
import com.mycompany.chservicetime.di.scope.FragmentScope;
import com.mycompany.chservicetime.presentation.addedittimeslot.AddEditTimeSlotFragment;
import com.mycompany.chservicetime.presentation.addedittimeslot.AddEditTimeSlotPresenter;

import dagger.Component;

/**
 * Created by szhx on 12/10/2016.
 */

@FragmentScope
@Component(dependencies = AppRepositoryComponent.class, modules = AddEditTimeSlotPresenterModule.class)
public interface AddEditTimeSlotComponent {

    void inject(AddEditTimeSlotFragment addEditTimeSlotFragment);

    AddEditTimeSlotPresenter getAddEditTimeSlotPresenter();
}
