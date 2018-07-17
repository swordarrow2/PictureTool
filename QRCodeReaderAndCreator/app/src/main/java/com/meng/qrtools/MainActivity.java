package com.meng.qrtools;

import android.app.*;
import android.content.*;
import android.os.*;
import com.meng.mbrowser.tools.*;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		ExceptionCatcher.getInstance().init(this);
        setContentView(R.layout.main);
		Intent i=new Intent(this,com.meng.qrtools.creator.awesomeCreator.class);
		startActivity(i);
		finish();
    }
}
