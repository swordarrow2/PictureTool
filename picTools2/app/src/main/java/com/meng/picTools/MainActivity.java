package com.meng.picTools;

import android.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;

import com.meng.picTools.encryAndDecry.*;
import com.meng.picTools.fragment.*;
import com.meng.picTools.gif.*;
import com.meng.picTools.helpers.*;
import com.meng.picTools.lib.*;
import com.meng.picTools.pixivPictureDownloader.*;
import com.meng.picTools.qrCode.creator.*;
import com.meng.picTools.qrCode.reader.*;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;

import com.meng.picTools.sauceNao.*;
import com.meng.picTools.ocr.*;


public class MainActivity extends AppCompatActivity {

    public static MainActivity instence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        instence = this;
        //    ExceptionCatcher.getInstance().init(this);
        SharedPreferenceHelper.Init(this, "main");
        DataBaseHelper.init(this);
        FileHelper.init(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
}

