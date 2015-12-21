package com.floreantpos.v14.mobile.activity;


import android.app.Application;
import android.content.Context;

import java.net.CookieHandler;
import java.net.CookieManager;

public class FloreantApplication extends Application {
    private static FloreantApplication mInstance;
    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        mAppContext = getApplicationContext();

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

    }

    public static FloreantApplication getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return mAppContext;
    }

}