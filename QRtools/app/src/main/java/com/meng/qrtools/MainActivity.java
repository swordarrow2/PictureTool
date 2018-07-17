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
		Intent i=new Intent(this,com.github.sumimakito.awesomeqrsample.MainActivity.class);
		startActivity(i);
		finish();
    }
}
