package com.floreantpos.v14.mobile.activity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;
import com.floreantpos.v14.mobile.R;
import com.floreantpos.v14.mobile.adapter.MenuItemAdapter;
import com.floreantpos.v14.mobile.tasks.AddMenuItemToTicketTask;
import com.floreantpos.v14.mobile.tasks.MenuItemsTask;
import com.floreantpos.response.MenuItemResponse;
import com.floreantpos.response.TicketItemResponse;
import com.floreantpos.v14.mobile.utils.ActivityInvoker;
import com.floreantpos.v14.mobile.utils.MySharedPreference;

import java.util.List;

public class MenuItemsActivity extends StyledBaseActivity {
    private GridView gridView;
    private MenuItemAdapter customGridAdapter;
    private int ticketId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_item);

        final ActionBar actionBar;
        if (Build.VERSION.SDK_INT >= 11) {
            actionBar = getActionBar();
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        } else {
            actionBar = null;
        }

        Bundle bundle = getIntent().getExtras();

        if (bundle == null) return;

        ticketId = bundle.getInt("ticketId");
        int groupId = bundle.getInt("groupId");

        final ProgressDialog progressDialog = ProgressDialog.show(MenuItemsActivity.this, "Loading", getString(R.string.loading_menu_items), true);

        progressDialog.setCanceledOnTouchOutside(true);

        MenuItemsTask menuItemsTask  = new MenuItemsTask(String.valueOf(groupId)) {
            @Override
            public void onSuccess(List<MenuItemResponse> response) {

                progressDialog.dismiss();

                if (response == null) {
                    Toast.makeText(MenuItemsActivity.this, getString(R.string.connection_problem), Toast.LENGTH_LONG).show();
                } else {
                    populateData(response);
                }
            }

            @Override
            public void onFailed() {
                Toast.makeText(MenuItemsActivity.this, getString(R.string.connection_problem), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        };


        menuItemsTask.execute();

    }

    private void populateData(List<MenuItemResponse> list) {

        gridView = (GridView) findViewById(R.id.gridView1);
        customGridAdapter = new MenuItemAdapter(this, R.layout.menu_item_row, list) {
            @Override
            public void pickMenuItem(final MenuItemResponse item) {

                    AddMenuItemToTicketTask addMenuItemToTicketTask = new AddMenuItemToTicketTask(ticketId, (item.id), MySharedPreference.getInteger(getApplicationContext(),"userId")) {
                        @Override
                        public void onSuccess(TicketItemResponse t) {
                            ActivityInvoker.notifyChangesToTicketItems(getApplicationContext());

                            if (item.hasModifier) {
                                ActivityInvoker.showMenuModifiers(MenuItemsActivity.this,t.itemId,t.id,ticketId,t.name);
                            } else {
                                ActivityInvoker.goToTicketItems(getApplicationContext(),ticketId);
                            }

                        }

                        @Override
                        public void onFailed() {
                            Toast.makeText(MenuItemsActivity.this, R.string.connection_problem, Toast.LENGTH_SHORT).show();
                        }
                    };

                    addMenuItemToTicketTask.execute();
            }
        };


        gridView.setAdapter(customGridAdapter);

        customGridAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
