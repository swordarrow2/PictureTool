package com.meng.pixivdownloader;

import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import java.io.File;

/**
 * Created by Administrator on 2018/3/13.
 */

public class preferenceActivity extends PreferenceActivity {
    Preference clean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(Data.preferenceKeys.mainPreferenceName);
        addPreferencesFromResource(R.xml.preference);
        clean=findPreference(Data.preferenceKeys.cleanTmpFilesNow);
        clean.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                File frameFileFolder = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "tmp");
                deleteFiles(frameFileFolder);
                return true;
            }
        });
    }
    private void deleteFiles(File folder){
        File[] fs=folder.listFiles();
        for (File f:fs) {
            if (f.isDirectory()){
                deleteFiles(f);
                f.delete();
            }else{
                f.delete();
            }

        }
    }
}
