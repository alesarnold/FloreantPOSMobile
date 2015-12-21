package com.floreantpos.v14.mobile.utils;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by Leonar Tambunan on 3/18/14.
 * <p/>
 * Coverse
 */
public class AndroidTools {

    public static boolean isActivityRunning(Class className, Context ctx) {

        boolean active = false;

        ActivityManager am = (ActivityManager) ctx.getSystemService(Activity.ACTIVITY_SERVICE);

        if (am != null && am.getRunningTasks(1).size() > 0) {
            String activeClassName = am.getRunningTasks(1).get(0).topActivity.getClassName();


            active = className.getName().equals(activeClassName) || activeClassName.contains(className.getSimpleName());
        }

        return active;
    }

    public static String getForegroundActivity(Context ctx) {

        String result = "";

        ActivityManager am = (ActivityManager) ctx.getSystemService(Activity.ACTIVITY_SERVICE);

        if (am != null && am.getRunningTasks(1).size() > 0) {
            result = am.getRunningTasks(1).get(0).topActivity.getClassName();
        }

        return result;
    }

}
