package com.mycompany.chservicetime.di.qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by szhx on 12/7/2016.
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Repository {

}