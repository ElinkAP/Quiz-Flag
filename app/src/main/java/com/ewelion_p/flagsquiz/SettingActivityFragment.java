package com.ewelion_p.flagsquiz;


import android.os.Bundle;


import androidx.preference.PreferenceFragmentCompat;



public class SettingActivityFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);


    }

}