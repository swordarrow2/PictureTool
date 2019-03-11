package com.meng.pixivGifDownloader;

import android.content.*;

public class sharedPreferenceHelper {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public sharedPreferenceHelper(Context c, String name) {
        sp = c.getSharedPreferences(name, 0);
    }

    public void putValue(String key, String value) {
        editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getValue(String key) {
        return sp.getString(key, null);
    }

    public boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public void putBoolean(String key, Boolean value) {
        editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
}

