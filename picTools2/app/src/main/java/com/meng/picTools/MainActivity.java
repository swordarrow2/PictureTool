package com.meng.picTools;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;

import com.meng.picTools.helpers.*;
import com.meng.picTools.lib.*;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExceptionCatcher.getInstance().init(this);
        SharedPreferenceHelper.Init(this, "main");
        boolean readonly = getIntent().getBooleanExtra("android.intent.extra.PROCESS_TEXT_READONLY", false);
        CharSequence text = getIntent().getCharSequenceExtra("android.intent.extra.PROCESS_TEXT");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intent = new Intent(this, MainActivity2.class);
            if (text != null) {
                intent.putExtra("pixivUrl", text.toString());
            }
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    }

    @Override
    protected void onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            boolean readonly = getIntent().getBooleanExtra("android.intent.extra.PROCESS_TEXT_READONLY", false);
            CharSequence text = getIntent().getCharSequenceExtra("android.intent.extra.PROCESS_TEXT");
            Intent intent = new Intent(this, MainActivity2.class);
            if (text != null) {
                intent.putExtra("pixivUrl", text.toString());
            }
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
        super.onResume();
    }

}
