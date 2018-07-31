package com.meng.qrtools;
import android.preference.*;
import android.os.*;

public class settings extends PreferenceFragment
{

	@Override
	public void onCreate(Bundle savedInstanceState){
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName("main");
		addPreferencesFromResource(R.xml.preference);
	}
	
}
