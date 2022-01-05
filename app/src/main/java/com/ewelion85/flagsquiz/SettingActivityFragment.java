package com.ewelion85.flagsquiz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;


public class SettingActivityFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }

//    @Override
//    public View onCreateView(
//            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState
//    ) {
//
//        View v = inflater.inflate(R.layout.fragment_settings, container, false);
//
//        addPreferences
//
//        return v;
//
//    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

}