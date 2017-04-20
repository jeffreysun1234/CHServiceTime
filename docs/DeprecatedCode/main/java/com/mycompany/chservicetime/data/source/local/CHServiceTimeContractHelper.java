/*
 * Copyright 2014 Google Inc. All rights reserved.
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

package com.mycompany.chservicetime.data.source.local;

import android.net.Uri;
import android.text.TextUtils;

import java.util.Locale;

/**
 * Provides helper methods for specifying query parameters on {@code Uri}s.
 */
public class CHServiceTimeContractHelper {

    public static final String QUERY_PARAMETER_DISTINCT = "distinct";

    private static final String QUERY_PARAMETER_CALLER_IS_SYNC_ADAPTER = "callerIsSyncAdapter";


    public static boolean isUriCalledFromSyncAdapter(Uri uri) {
        String flag = uri.getQueryParameter(QUERY_PARAMETER_CALLER_IS_SYNC_ADAPTER);
        if (flag == null) {
            return false;
        }
        flag = flag.toLowerCase(Locale.ROOT);
        return (!"false".equals(flag) && !"0".equals(flag));
    }

    public static Uri setUriAsCalledFromSyncAdapter(Uri uri) {
        return uri.buildUpon().appendQueryParameter(QUERY_PARAMETER_CALLER_IS_SYNC_ADAPTER, "true")
                .build();
    }

    public static boolean isQueryDistinct(Uri uri) {
        return !TextUtils.isEmpty(uri.getQueryParameter(QUERY_PARAMETER_DISTINCT));
    }

    public static String formatQueryDistinctParameter(String parameter) {
        return CHServiceTimeContractHelper.QUERY_PARAMETER_DISTINCT + " " + parameter;
    }
}
