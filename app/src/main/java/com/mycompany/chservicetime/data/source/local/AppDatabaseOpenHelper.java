package com.mycompany.chservicetime.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mycompany.chservicetime.model.TimeSlot;
import com.mycompany.chservicetime.util.CHLog;

import static com.mycompany.chservicetime.util.CHLog.makeLogTag;

/**
 * Created by szhx on 11/28/2016.
 */

public class AppDatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = makeLogTag("CHServiceTimeDatabase");

    private static final int VER_INIT_RELEASE = 1; // app version 1.0
    private static final int CUR_DATABASE_VERSION = VER_INIT_RELEASE;

    public static final String DATABASE_NAME = "chservicetime.db";

    private static AppDatabaseOpenHelper instance;

    public static AppDatabaseOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new AppDatabaseOpenHelper(context);
        }
        return instance;
    }

    public AppDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, CUR_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TimeSlot.CREATE_TABLE);

        /*
         Populate initial data.
          */
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        CHLog.d(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required as at version 1
    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);

        // make sure the sqlite database will be recreated later.
        instance = null;
    }
}
