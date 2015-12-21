package com.floreantpos.v14.mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.floreantpos.v14.mobile.activity.GV;

public class MySharedPreference {
    public static void putInteger(Context ctx, String key, int value) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences(GV.TAG, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInteger(Context ctx, String key) {
        SharedPreferences sharedPrefs = ctx.getSharedPreferences(GV.TAG, 0);
        return sharedPrefs.getInt(key, -1);
    }

    public static String getServerAddress(Context ctx) {
        SharedPreferences sharedPrefs = ctx.getSharedPreferences(GV.TAG, 0);
        return sharedPrefs.getString(GV.PreferredShareID.SERVER_ADDR, null);
    }


    public static void setServerAddress(Context ctx, String value) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences(GV.TAG, Context.MODE_PRIVATE).edit();
        editor.putString(GV.PreferredShareID.SERVER_ADDR, value);
        editor.apply();
    }
}
