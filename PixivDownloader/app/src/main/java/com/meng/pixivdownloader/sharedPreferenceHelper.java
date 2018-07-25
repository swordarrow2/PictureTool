package com.meng.pixivdownloader;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2018/3/13.
 */

public class sharedPreferenceHelper{

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Context context;

    public sharedPreferenceHelper(Context c,String name){
        context=c;
        sp=context.getSharedPreferences(name,0);
        editor=sp.edit();
    }
    public void putValue(String key,String value){
        editor=sp.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public String getValue(String key){
        return sp.getString(key,null);
    }


    public boolean getBoolean(String key) {
        return sp.getBoolean(key,false);
    }
    public void putBoolean(String key,Boolean value){
        editor=sp.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }
}

