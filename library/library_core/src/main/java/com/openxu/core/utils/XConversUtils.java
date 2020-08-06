package com.openxu.core.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Author: openXu
 * Time: 2019/3/14 11:37
 * class: FConversUtils
 * Description: 各种转换工具类
 */
public class XConversUtils {

    /***/

    /**根据手机的分辨率从 dp 的单位 转成为 px(像素)*/
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    /**根据手机的分辨率从 px(像素) 的单位 转成为 dp  */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    /**将px值转换为sp值，保证文字大小不变*/
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
    /**将sp值转换为px值，保证文字大小不变 */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * file2Uri: file 转 uri
     * uri2File: uri 转 file
     */
    /**File to uri. */
    public static Uri file2Uri(@NonNull final File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authority = XUtils.getApp().getPackageName() + ".utilcode.provider";
            return FileProvider.getUriForFile(XUtils.getApp(), authority, file);
        } else {
            return Uri.fromFile(file);
        }
    }

    /**Uri to file. */
    public static File uri2File(@NonNull final Uri uri) {
        XLog.d("UriUtils", uri.toString());
        String authority = uri.getAuthority();
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            String path = uri.getPath();
            if (path != null) return new File(path);
            XLog.d("UriUtils", uri.toString() + " parse failed. -> 0");
            return null;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(XUtils.getApp(), uri)) {
            if ("com.android.externalstorage.documents".equals(authority)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return new File(Environment.getExternalStorageDirectory() + "/" + split[1]);
                }
                XLog.d("UriUtils", uri.toString() + " parse failed. -> 1");
                return null;
            } else if ("com.android.providers.downloads.documents".equals(authority)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id)
                );
                return getFileFromUri(contentUri, 2);
            } else if ("com.android.providers.media.documents".equals(authority)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                } else {
                    XLog.d("UriUtils", uri.toString() + " parse failed. -> 3");
                    return null;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getFileFromUri(contentUri, selection, selectionArgs, 4);
            } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
                return getFileFromUri(uri, 5);
            } else {
                XLog.d("UriUtils", uri.toString() + " parse failed. -> 6");
                return null;
            }
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            return getFileFromUri(uri, 7);
        } else {
            XLog.d("UriUtils", uri.toString() + " parse failed. -> 8");
            return null;
        }
    }

    private static File getFileFromUri(final Uri uri, final int code) {
        return getFileFromUri(uri, null, null, code);
    }
    private static File getFileFromUri(final Uri uri,
                                       final String selection,
                                       final String[] selectionArgs,
                                       final int code) {
        final Cursor cursor = XUtils.getApp().getContentResolver().query(
                uri, new String[]{"_data"}, selection, selectionArgs, null);
        if (cursor == null) {
            XLog.d("UriUtils", uri.toString() + " parse failed(cursor is null). -> " + code);
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                final int columnIndex = cursor.getColumnIndex("_data");
                if (columnIndex > -1) {
                    return new File(cursor.getString(columnIndex));
                } else {
                    XLog.d("UriUtils", uri.toString() + " parse failed(columnIndex: " + columnIndex + " is wrong). -> " + code);
                    return null;
                }
            } else {
                XLog.d("UriUtils", uri.toString() + " parse failed(moveToFirst return false). -> " + code);
                return null;
            }
        } catch (Exception e) {
            XLog.d("UriUtils", uri.toString() + " parse failed. -> " + code);
            return null;
        } finally {
            cursor.close();
        }
    }



    public static String getFloatStr(float a, float b) {
        DecimalFormat decimalFormat = new DecimalFormat("0.0");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
        float pro = (a * 100.0f) / (b * 1.0f);
        if (pro == 0)
            return "0 %";
        String str = decimalFormat.format(pro);
        if (str.endsWith("0")) {
            str = str.substring(0, str.lastIndexOf("0"));
            if (str.endsWith("0")) {
                str = str.substring(0, str.lastIndexOf("0"));
            }
            if (str.endsWith(".")) {
                str = str.substring(0, str.lastIndexOf("."));
            }
        }
        return str + " %";
    }
}
