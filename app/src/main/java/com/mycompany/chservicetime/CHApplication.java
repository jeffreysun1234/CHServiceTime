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

import com.facebook.stetho.Stetho;
import com.mycompany.chservicetime.di.component.AppRepositoryComponent;
import com.mycompany.chservicetime.di.component.ApplicationComponent;
import com.mycompany.chservicetime.di.component.DaggerAppRepositoryComponent;
import com.mycompany.chservicetime.di.component.DaggerApplicationComponent;
import com.mycompany.chservicetime.di.module.AppRepositoryModule;
import com.mycompany.chservicetime.di.module.ApplicationModule;
import com.mycompany.chservicetime.util.CHLog;
import com.squareup.leakcanary.LeakCanary;

import static com.mycompany.chservicetime.util.CHLog.makeLogTag;

public class CHApplication extends Application {
    private static final String TAG = makeLogTag("CHApplication");

    public static CHApplication INSTANCE;

    // Global mContext used in this app
    private static Context mContext = null;
    private static ApplicationComponent mApplicationComponent;
    private static AppRepositoryComponent mAppRepositoryComponent;

    /**
     * If you want to mock mContext, then override this method in your mock subclass of CHApplication.
     */
    protected Context createContext() {
        return this.getApplicationContext();
    }

    public static Context getContext() {
        if (mContext == null)
            CHLog.d(TAG, "Application mContext is NULL.");
        return mContext;
    }

//    @Override
//    protected void attachBaseContext(Context context) {
//        super.attachBaseContext(context);
//        MultiDex.install(this);
//    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        INSTANCE = this;

        setDaggerGraph();

        if (BuildConfig.DEBUG) {
            // log output with System.out.println
            CHLog.setLogger(CHLog.TESTOUT);

            // Stetho Initial
            Stetho.initializeWithDefaults(this);
        } else {
            // disable log
            CHLog.setLogger(null);
        }

        mContext = createContext();

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
