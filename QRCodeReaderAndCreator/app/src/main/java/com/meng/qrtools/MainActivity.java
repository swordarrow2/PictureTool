package com.meng.qrtools;

import android.app.*;
import android.content.*;
import android.os.*;
import com.meng.*;
import com.meng.qrtools.lib.exception.*;

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
