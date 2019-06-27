package com.meng.picTools;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;

import com.meng.picTools.libAndHelper.*;


public class MainActivity extends Activity {

    private CharSequence url = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExceptionCatcher.getInstance().init(this);
        SharedPreferenceHelper.Init(this, "main");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean readonly = getIntent().getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false);
            url = getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intent = new Intent(this, MainActivity2.class);
            if (url != null) {
                intent.putExtra("pixivUrl", url.toString());
            }
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    }

    @Override
    protected void onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean readonly = getIntent().getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false);
            url = getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intent = new Intent(this, MainActivity2.class);
            if (url != null) {
                intent.putExtra("pixivUrl", url.toString());
            }
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
        super.onResume();
    }

}
