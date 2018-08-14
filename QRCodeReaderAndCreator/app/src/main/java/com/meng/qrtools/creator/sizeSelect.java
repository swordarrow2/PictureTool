package com.meng.qrtools.creator;
import android.app.*;
import com.meng.qrtools.*;
import android.os.*;

public class sizeSelect extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState){
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
    public void setTheme(int resid) {
        if (MainActivity.sharedPreference.getBoolean("useLightTheme", true)) {
            super.setTheme(R.style.AppThemeLight);
        } else {
            super.setTheme(R.style.AppThemeDark);
        }
    }
	
}
