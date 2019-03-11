package com.meng.pixivGifDownloader;

import android.os.*;
import android.preference.*;
import com.meng.qrtools.*;
import java.io.*;

public class preferenceActivity extends PreferenceActivity{
    Preference clean;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(Data.preferenceKeys.mainPreferenceName);
        addPreferencesFromResource(R.xml.preference);
        clean=findPreference(Data.preferenceKeys.cleanTmpFilesNow);
        clean.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			  @Override
			  public boolean onPreferenceClick(Preference preference){
				  File frameFileFolder = new File(Environment.getExternalStorageDirectory().getPath()+File.separator+"tmp");
				  deleteFiles(frameFileFolder);
				  return true;
				}
			});
	  }
    private void deleteFiles(File folder){
        File[] fs=folder.listFiles();
        for(File f:fs){
            if(f.isDirectory()){
                deleteFiles(f);
                f.delete();
			  }else{
                f.delete();
			  }

		  }
	  }
  }
