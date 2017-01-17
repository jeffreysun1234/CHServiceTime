/*
 * Copyright 2015 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mycompany.chservicetime;

import android.app.Application;
import android.content.Context;

import com.mycompany.chservicetime.di.component.AppRepositoryComponent;
import com.mycompany.chservicetime.di.component.ApplicationComponent;
import com.mycompany.chservicetime.di.component.DaggerAppRepositoryComponent;
import com.mycompany.chservicetime.di.component.DaggerApplicationComponent;
import com.mycompany.chservicetime.di.module.AppRepositoryModule;
import com.mycompany.chservicetime.di.module.ApplicationModule;
import com.mycompany.chservicetime.util.CHLog;

import static com.mycompany.chservicetime.util.LogUtils.LOGD;
import static com.mycompany.chservicetime.util.LogUtils.makeLogTag;

public class CHApplication extends Application {
    private static final String TAG = makeLogTag("CHApplication");

    public static CHApplication INSTANCE;

    // Global context used in this app
    private static Context context = null;
    private static ApplicationComponent mApplicationComponent;
    private static AppRepositoryComponent mAppRepositoryComponent;

    /**
     * If you want to mock context, then override this method in your mock subclass of CHApplication.
     */
    protected Context createContext() {
        return this.getApplicationContext();
    }

    public static Context getContext() {
        if (context == null)
            LOGD(TAG, "Application context is NULL.");
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        INSTANCE = this;

        setDaggerGraph();

        if (BuildConfig.DEBUG) {
            // log output with System.out.println
            CHLog.setLogger(CHLog.TESTOUT);
        } else {
            // disable log
            CHLog.setLogger(null);
        }

        context = createContext();

        // initialize Firebase
        //Firebase.setAndroidContext(this);

    }

    private void setDaggerGraph() {
        ApplicationModule applicationModule = new ApplicationModule(getApplicationContext());

        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(applicationModule)
                .build();

        mAppRepositoryComponent = DaggerAppRepositoryComponent.builder()
                .applicationModule(applicationModule)
                .appRepositoryModule(new AppRepositoryModule())
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }

    public AppRepositoryComponent getAppRepositoryComponent() {
        return mAppRepositoryComponent;
    }
}
