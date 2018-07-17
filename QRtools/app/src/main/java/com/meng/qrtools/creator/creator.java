package com.meng.qrtools.creator;
import android.app.*;
import android.content.*;
import android.os.*;

public class creator extends Activity
{

	@Override
	public void onCreate(Bundle savedInstanceState,PersistableBundle persistentState){
		// TODO: Implement this method
		super.onCreate(savedInstanceState,persistentState);
		Intent i=new Intent(this,com.sevenheaven.zxingdemo.MainActivity.class);
		startActivity(i);
		finish();
	}
	
}
