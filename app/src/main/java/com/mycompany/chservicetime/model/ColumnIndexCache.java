package com.mycompany.chservicetime.model;

import android.database.Cursor;
import android.support.v4.util.ArrayMap;

/**
 * Created by szhx on 3/13/2016.
 */
public class ColumnIndexCache {
    private ArrayMap<String, Integer> mMap = new ArrayMap<>();

    public int getColumnIndex(Cursor cursor, String columnName) {
        if (!mMap.containsKey(columnName))
            mMap.put(columnName, cursor.getColumnIndex(columnName));
        return mMap.get(columnName);
    }

    public void clear() {
        mMap.clear();
    }
}
