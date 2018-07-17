package com.meng.qrtools.reader;
import android.app.*;
import android.content.*;
import android.os.*;

public class reader extends Activity
{

	@Override
	public void onCreate(Bundle savedInstanceState,PersistableBundle persistentState){
		// TODO: Implement this method
		super.onCreate(savedInstanceState,persistentState);
		Intent i=new Intent(this,SimpleCaptureActivity.class);
		startActivity(i);
		finish();
	}
	
}
