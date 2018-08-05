package com.meng.qrtools;

import android.content.*;
import android.os.*;
import android.preference.*;

public class settings extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO: Implement this method
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName("main");
        addPreferencesFromResource(R.xml.preference);
        CheckBoxPreference cb = (CheckBoxPreference) findPreference("useLightTheme");
        cb.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
           //     Intent i = new Intent(getActivity().getApplicationContext(), MainActivity.class);
           //     i.putExtra("setTheme", true);
           //     getActivity().startActivity(i);
                getActivity().startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class).putExtra("setTheme", true));
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
                return true;
            }
        });
    }

}
