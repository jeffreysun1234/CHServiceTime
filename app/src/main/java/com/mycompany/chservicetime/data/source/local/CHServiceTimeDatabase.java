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

import android.accounts.Account;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import static com.mycompany.chservicetime.data.source.local.CHServiceTimeContract.*;
import static com.mycompany.chservicetime.util.LogUtils.LOGD;
import static com.mycompany.chservicetime.util.LogUtils.makeLogTag;

/**
 * Helper for managing {@link SQLiteDatabase} that stores data for
 * {@link CHServiceTimeProvider}.
 */
public class CHServiceTimeDatabase extends SQLiteOpenHelper {
    private static final String TAG = makeLogTag("CHServiceTimeDatabase");

    private static final String DATABASE_NAME = "chservicetime.db";

    // NOTE: carefully update onUpgrade() when bumping database versions to make
    // sure user data is saved.

    private static final int VER_INIT_RELEASE = 1; // app version 1.0
    private static final int CUR_DATABASE_VERSION = VER_INIT_RELEASE;

    private static CHServiceTimeDatabase sInstance;

    private final Context mContext;

    interface Tables {
        String TIME_SLOTS = "time_slots";
    }

    public static CHServiceTimeDatabase getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new CHServiceTimeDatabase(context.getApplicationContext());
        }
        return sInstance;
    }

    private CHServiceTimeDatabase(Context context) {
        super(context, DATABASE_NAME, null, CUR_DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.TIME_SLOTS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SyncColumns.UPDATED_TIMESTAMP + " INTEGER NOT NULL,"
                + TimeSlotsColumns.NAME + " TEXT NOT NULL, "
                + TimeSlotsColumns.DESCRIPTION + " TEXT, "
                + TimeSlotsColumns.TIME_SLOT_ID + " TEXT NOT NULL, "
                + TimeSlotsColumns.BEGIN_TIME_HOUR + " INTEGER NOT NULL, "
                + TimeSlotsColumns.BEGIN_TIME_MINUTE + " INTEGER NOT NULL, "
                + TimeSlotsColumns.END_TIME_HOUR + " INTEGER NOT NULL, "
                + TimeSlotsColumns.END_TIME_MINUTE + " INTEGER NOT NULL, "
                + TimeSlotsColumns.REPEAT_FLAG + " INTEGER NOT NULL DEFAULT 0, "
                + TimeSlotsColumns.ACTIVATION_FLAG + " INTEGER NOT NULL DEFAULT 0, "
                + TimeSlotsColumns.DAYS + " TEXT NOT NULL DEFAULT '0000000', "
                + "UNIQUE (" + TimeSlotsColumns.TIME_SLOT_ID + ") ON CONFLICT REPLACE)");

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LOGD(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);
    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);

        // make sure the sqlite database will be recreated later.
        sInstance = null;
    }
}
