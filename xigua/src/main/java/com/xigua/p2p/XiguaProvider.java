package com.xigua.p2p;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * XiguaProvider
 *  通过声明 {@link ContentProvider} 自动完成初始化
 * Created by fanchen on 2018/10/24.
 */
public class XiguaProvider  extends ContentProvider {

    @Override
    public boolean onCreate() {
        P2PManager.getInstance().init(getContext());
        P2PManager.getInstance().setAllow3G(true);
        P2PManager.getInstance().isConnect();
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