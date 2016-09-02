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

import static com.mycompany.chservicetime.util.LogUtils.LOGD;
import static com.mycompany.chservicetime.util.LogUtils.makeLogTag;

public class CHApplication extends Application {
    private static final String TAG = makeLogTag("CHApplication");

    // Global context used in this app
    private static Context context = null;

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

        context = createContext();

        // initialize Firebase
        //Firebase.setAndroidContext(this);

    }
}
