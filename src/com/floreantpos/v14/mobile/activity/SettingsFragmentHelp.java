package com.floreantpos.v14.mobile.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.floreantpos.v14.mobile.R;

@SuppressLint("NewApi")
public class SettingsFragmentHelp extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_help);
    }
}
