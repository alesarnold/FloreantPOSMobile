package com.floreantpos.v14.mobile.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.widget.Toast;
import com.floreantpos.v14.mobile.R;

@SuppressLint("NewApi")
public class SettingsFragmentSystem extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_system);

        //Toast.makeText(getActivity(),this.getClass().getName(),Toast.LENGTH_LONG).show();
    }




}

