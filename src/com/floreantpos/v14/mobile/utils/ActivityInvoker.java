package com.floreantpos.v14.mobile.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import com.floreantpos.v14.mobile.R;
import com.floreantpos.v14.mobile.activity.*;
import com.floreantpos.v14.mobile.tasks.ClockOutTask;

public class ActivityInvoker {


    public static void goToTicketItems(Context ctx, int ticketId) {
        if (ctx == null) return;
        Intent intent = new Intent(ctx, TicketListActivity.TicketItemsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("ticketId", ticketId);
        ctx.startActivity(intent);
    }

    public static void goToOpenTickets(Context activityContext) {
        if (activityContext == null) return;
//        String activeClass = AndroidTools.getForegroundActivity(activityContext);
//        if (!activeClass.equals(TicketListActivity.class.getName())) {
        Intent i = new Intent(activityContext, TicketListActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activityContext.startActivity(i);
//        }
    }

    public static void gotoLoginPage(Context activityContext) {
        if (activityContext == null) return;
//        String activeClass = AndroidTools.getForegroundActivity(activityContext);
//        if (!activeClass.equals(LoginActivity.class.getName())) {
        Intent i = new Intent(activityContext, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activityContext.startActivity(i);
//        }
    }

    public static void logout(final Context activityContext) {

        MySharedPreference.putInteger(activityContext,"userId", -1);

        ActivityInvoker.gotoLoginPage(activityContext);
    }


    public static void showSettings(Context ctx) {
        if (ctx == null) return;
        String activeClass = AndroidTools.getForegroundActivity(ctx);
        if (!activeClass.equals(SetPreferenceActivity.class.getName())) {
            Intent i = new Intent();
            i.setClass(ctx, SetPreferenceActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            ctx.startActivity(i);
        }
    }

    public static void clockOut(final Context ctx) {

        ClockOutTask clockOutTask = new ClockOutTask() {
            @Override
            public void onSuccess() {
                gotoLoginPage(ctx);
            }

            @Override
            public void onFailed() {

            }
        };

        clockOutTask.execute();


    }


    public static void notifyChangesToTicketList(Context ctx) {
        if (ctx == null) return;
        Intent intnet = new Intent(IntentNames.TICKET_MODIFIED);
        ctx.sendBroadcast(intnet);

    }

    public static void notifyChangesToTicketItems(Context ctx) {
        if (ctx == null) return;
        Intent intent = new Intent(IntentNames.TICKET_ITEMS_MODIFIED);
        ctx.sendBroadcast(intent);
    }


    public static void showMenuCategories(Context ctx, int ticketId) {
        Intent i = new Intent();
        i.putExtra("ticketId", ticketId);
        i.setClass(ctx, MenuCategoriesActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        ctx.startActivity(i);
    }

    public static void showMenuGroups(Context ctx, int catId, int ticketId) {

        Intent intent = new Intent();
        intent.setClass(ctx, MenuGroupsActivity.class);
        intent.putExtra("catId", catId);
        intent.putExtra("ticketId", ticketId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        ctx.startActivity(intent);
    }

    public static void showMenuItems(Context ctx, int groupId, int ticketId) {
        Intent intent = new Intent();
        intent.setClass(ctx, MenuItemsActivity.class);
        intent.putExtra("groupId", groupId);
        intent.putExtra("ticketId", ticketId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        ctx.startActivity(intent);
    }

    public static void showMenuModifiers(Context ctx, int menuItemId, int ticketItemId,int ticketId, String menuItemName) {

        Intent intent = new Intent();
        intent.setClass(ctx, MenuModifiersActivity.class);
        intent.putExtra("ticketId", ticketId);
        intent.putExtra("ticketItemId", ticketItemId);
        intent.putExtra("menuItemId", menuItemId);
        intent.putExtra("menuItemName", menuItemName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        ctx.startActivity(intent);
    }
}
