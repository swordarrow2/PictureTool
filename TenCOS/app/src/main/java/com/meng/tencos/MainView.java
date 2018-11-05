package com.meng.tencos;

import android.app.*;
import android.content.*;
import android.os.*;
import com.meng.qrtools.lib.*;
import com.meng.tencos.utils.*;
import com.meng.tencos.ui.*;

public class MainView extends Activity{
	
	public static boolean lightTheme = false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        lightTheme=BizService.sharedPreference.getBoolean("useLightTheme",true);
		
		ExceptionCatcher.getInstance().init(this);
        startActivity(new Intent(MainView.this,com.meng.MainActivity2.class));
		finish();
    }
}
