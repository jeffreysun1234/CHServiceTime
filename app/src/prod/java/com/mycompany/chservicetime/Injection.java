/*
 * Copyright (C) 2015 The Android Open Source Project
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

import android.content.Context;
import android.support.annotation.NonNull;

import com.mycompany.chservicetime.data.source.AppDataSource;
import com.mycompany.chservicetime.data.source.AppRepository;

/**
 * Enables injection of production implementations for
 * {@link AppDataSource} at compile time.
 */
public class Injection {

    public static AppRepository provideTimeSlotsRepository(@NonNull Context context) {
        //return AppRepository.getInstance(CHServiceTimeDAO.getInstance(context));
        return null;
    }
}
