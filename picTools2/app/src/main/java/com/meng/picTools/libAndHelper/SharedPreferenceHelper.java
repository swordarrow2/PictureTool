package com.meng.picTools.libAndHelper;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferenceHelper {

    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;

    public static void Init(Context context, String preferenceName) {
        sp = context.getSharedPreferences(preferenceName, 0);
    }

    public static boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean p1) {
        return sp.getBoolean(key, p1);
    }

    public static void putBoolean(String key, Boolean value) {
        editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static String getValue(String key) {
        return sp.getString(key, null);
    }

    public static String getValue(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    public static void putValue(String key, String value) {
        editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }
}

