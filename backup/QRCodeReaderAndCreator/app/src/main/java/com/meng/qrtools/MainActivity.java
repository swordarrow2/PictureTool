package com.meng.qrtools;

import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;

import com.meng.qrtools.lib.exception.ExceptionCatcher;
import com.meng.qrtools.reader.*;
import com.meng.qrtools.creator.*;
import com.meng.*;

public class MainActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
		ExceptionCatcher.getInstance().init(this);
		Intent i=new Intent(MainActivity.this,MainActivity2.class);
		startActivity(i);
		finish();
    }
}
