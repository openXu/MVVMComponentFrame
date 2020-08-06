package com.openxu.core.crash;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Author: Ereza/CustomActivityOnCrash
 * Time: 2019/3/15 11:04
 * class: CaocInitProvider
 * Description:
 * https://github.com/Ereza/CustomActivityOnCrash/blob/master/library/src/main/java/cat/ereza/customactivityoncrash/provider/CaocInitProvider.java
 */
public class CaocInitProvider extends ContentProvider {

    public boolean onCreate() {
        CustomActivityOnCrash.install(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

}
