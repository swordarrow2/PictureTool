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
    //    Intent i = new Intent(MainActivity.this, MainActivity2.class);
    //    i.putExtra("setTheme", getIntent().getBooleanExtra("setTheme", false));
    //    startActivity(i);
        startActivity(new Intent(MainActivity.this, MainActivity2.class).putExtra("setTheme", getIntent().getBooleanExtra("setTheme", false)));
        finish();
        overridePendingTransition(0, 0);
    }
}
