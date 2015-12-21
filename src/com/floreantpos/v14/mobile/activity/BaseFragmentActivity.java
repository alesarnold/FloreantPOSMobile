package com.floreantpos.v14.mobile.activity;

import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ViewConfiguration;
import android.view.WindowManager;

public abstract class BaseFragmentActivity extends FragmentActivity {

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 11) {

            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        if (Build.VERSION.SDK_INT >= 14) {

            boolean hasMenu = ViewConfiguration.get(getApplicationContext()).hasPermanentMenuKey();
            if (!hasMenu) {
                try {
                    getWindow().addFlags(WindowManager.LayoutParams.class.getField("FLAG_NEEDS_MENU_KEY").getInt(null));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

}
