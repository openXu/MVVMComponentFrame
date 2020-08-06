package com.openxu.core.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

public class XSharedData {

    private Context context;
    private static final String SP_NAME = "openXuSp";
    private static XSharedData instance;

    public static XSharedData getInstance(Context context) {
        if(instance==null)
            instance = new XSharedData(context);
        return instance;
    }

    private XSharedData(Context context) {
        this.context = context;
    }

    /**
     * key
     */
    private static final String KEY_AUTO_LOGIN = "auto_login";//是否自动登录
    private static final String KEY_CURRENT_USER = "current_userinfo";//当前登录用户信息


    public <T> T getData(String key, Class clazz, T def) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        Object obj;
        if (clazz == String.class)
            obj = sharedPreferences.getString(key, (String)def);
        else if (clazz == Integer.class)
            obj = sharedPreferences.getInt(key, (Integer)def);
        else if (clazz == Float.class)
            obj = sharedPreferences.getFloat(key, (Float)def);
        else if (clazz == Long.class)
            obj = sharedPreferences.getLong(key, (Long)def);
        else if (clazz == Boolean.class)
            obj = sharedPreferences.getBoolean(key, (Boolean)def);
        else
            obj = sharedPreferences.getString(key, def.toString());
        return (T) obj;
    }

    public void saveData(String key, Object data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (data instanceof String)
            editor.putString(key, (String) data);
        else if (data instanceof Integer)
            editor.putInt(key, (int) data);
        else if (data instanceof Float)
            editor.putFloat(key, (float) data);
        else if (data instanceof Long)
            editor.putLong(key, (long) data);
        else if (data instanceof Boolean)
            editor.putBoolean(key, (boolean) data);
        else
            editor.putString(key, (String) data);
        editor.commit();
    }

    public void clear() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
    }


}

