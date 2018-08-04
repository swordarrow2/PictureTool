package com.meng.qrtools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.meng.MainActivity2;
import com.meng.qrtools.lib.SharedPreferenceHelper.SharedPreferenceHelper;
import com.meng.qrtools.lib.exception.ExceptionCatcher;

public class MainActivity extends Activity {
    public static SharedPreferenceHelper sharedPreference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExceptionCatcher.getInstance().init(this);
        sharedPreference = new SharedPreferenceHelper(this, "main");
        Intent i = new Intent(MainActivity.this, MainActivity2.class);
        if (!getIntent().getBooleanExtra("setTheme", false)) {
            i.putExtra("setTheme", true);
        }
        startActivity(i);
        finish();
        overridePendingTransition(0, 0);
    }
}
