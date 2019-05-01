package com.meng.picTools.qrtools;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.meng.picTools.MainActivity;
import com.meng.picTools.R;
import com.meng.picTools.pixivPictureDownloader.Data;

import java.io.File;

public class SettingsPreference extends PreferenceFragment{

    Preference clean;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName("main");
        addPreferencesFromResource(R.xml.preference);
        CheckBoxPreference cb=(CheckBoxPreference)findPreference("useLightTheme");
        cb.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
            @Override
            public boolean onPreferenceChange(Preference preference,Object newValue){
                //     Intent i = new Intent(getActivity().getApplicationContext(), PixivDownloadMain.class);
                //     i.putExtra("setTheme", true);
                //     getActivity().startActivity(i);
                getActivity().startActivity(new Intent(getActivity().getApplicationContext(),MainActivity.class).putExtra("setTheme",true));
                getActivity().finish();
                getActivity().overridePendingTransition(0,0);
                return true;
            }
        });
        clean=findPreference(Data.preferenceKeys.cleanTmpFilesNow);
        clean.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference){
                File frameFileFolder = new File(Environment.getExternalStorageDirectory().getPath()+File.separator+"Pictures/picTool/tmp");
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
