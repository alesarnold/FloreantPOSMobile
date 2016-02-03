package com.floreantpos.v14.mobile.utils;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

public class AndroidTools {

    public static String getForegroundActivity(Context ctx) {

        String result = "";

        ActivityManager am = (ActivityManager) ctx.getSystemService(Activity.ACTIVITY_SERVICE);

        if (am != null && am.getRunningTasks(1).size() > 0) {
            result = am.getRunningTasks(1).get(0).topActivity.getClassName();
        }

        return result;
    }

}
