package com.floreantpos.v14.mobile.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.floreantpos.v14.mobile.R;

import java.util.List;

public class SetPreferenceActivity extends PreferenceActivity {
    final static String ACTION_HELP = "com.floreantpos.v14.mobile.PREF_HELP";
    final static String ACTION_SYSTEM = "com.floreantpos.v14.mobile.PREF_SYSTEM";

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String action = getIntent().getAction();

        if (action != null && action.equals(ACTION_SYSTEM)) {
            addPreferencesFromResource(R.xml.preferences_system);
        } else if (action != null && action.equals(ACTION_HELP)) {
            addPreferencesFromResource(R.xml.preferences_help);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // Load the legacy preferences headers
            addPreferencesFromResource(R.xml.preference_header_legacy);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }


    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }
}

