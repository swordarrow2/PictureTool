package com.meng.qrtools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.meng.MainActivity2;
import com.meng.qrtools.lib.exception.ExceptionCatcher;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExceptionCatcher.getInstance().init(this);
        Intent i = new Intent(MainActivity.this, MainActivity2.class);
        startActivity(i);
        finish();
    }
}
