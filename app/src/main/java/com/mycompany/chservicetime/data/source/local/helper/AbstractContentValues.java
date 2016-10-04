package com.mycompany.chservicetime.data.source.local.helper;

import android.content.ContentValues;

public abstract class AbstractContentValues {
    protected final ContentValues mContentValues = new ContentValues();

    /**
     * Returns the {@code ContentValues} wrapped by this object.
     */
    public ContentValues values() {
        return mContentValues;
    }
}