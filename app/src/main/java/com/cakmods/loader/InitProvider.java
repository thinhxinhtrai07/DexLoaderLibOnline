package com.cakmods.loader;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * ContentProvider tự động chạy khi game khởi động
 * Không cần sửa Activity chính của game
 */
public class InitProvider extends ContentProvider {

    private static final String TAG = "CakModsLoader";

    @Override
    public boolean onCreate() {
        Log.e(TAG, "=== AutoLoader initialized! ===");
        
        // Bắt đầu tải và load thư viện
        if (getContext() != null) {
            AutoLoader.start(getContext());
        }
        
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}