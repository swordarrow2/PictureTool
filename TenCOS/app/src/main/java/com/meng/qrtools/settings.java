package com.meng.qrtools;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import com.meng.tencos.*;

public class settings extends PreferenceFragment{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName("main");
        addPreferencesFromResource(R.xml.preference);
        CheckBoxPreference cb=(CheckBoxPreference)findPreference("useLightTheme");
        cb.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
            @Override
            public boolean onPreferenceChange(Preference preference,Object newValue){
                getActivity().startActivity(new Intent(getActivity().getApplicationContext(),MainView.class).putExtra("setTheme",true));
                getActivity().finish();
                getActivity().overridePendingTransition(0,0);
                return true;
            }
        });
    }

}
