package com.meng.qrtools;

import android.app.*;
import android.content.*;
import android.os.*;
import com.meng.*;
import com.meng.qrtools.lib.ExceptionCatcher;
import com.meng.qrtools.lib.SharedPreferenceHelper;

public class MainActivity extends Activity {
    public static SharedPreferenceHelper sharedPreference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExceptionCatcher.getInstance().init(this);
        sharedPreference = new SharedPreferenceHelper(this, "main");
        startActivity(new Intent(MainActivity.this, MainActivity2.class).putExtra("setTheme", getIntent().getBooleanExtra("setTheme", false)));
        finish();
        overridePendingTransition(0, 0);
    }
    public static void selectImage(Activity a,int SELECT_FILE_REQUEST_CODE){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        a.startActivityForResult(intent,SELECT_FILE_REQUEST_CODE);
    }
}
