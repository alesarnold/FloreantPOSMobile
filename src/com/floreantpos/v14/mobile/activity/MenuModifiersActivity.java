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
import com.floreantpos.v14.mobile.adapter.MenuModifierAdapter;
import com.floreantpos.v14.mobile.tasks.AddMenuModifierToTicketItemTask;
import com.floreantpos.v14.mobile.tasks.MenuModifiersTask;
import com.floreantpos.response.MenuModifierResponse;
import com.floreantpos.v14.mobile.utils.ActivityInvoker;
import com.floreantpos.v14.mobile.utils.MySharedPreference;

import java.util.List;

public class MenuModifiersActivity extends StyledBaseActivity {
    private GridView gridView;
    private MenuModifierAdapter customGridAdapter;
    private int ticketId;
    private int ticketItemId;
    private int menuItemId;
    private String menuItemName;

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
        ticketItemId = bundle.getInt("ticketItemId");
        menuItemId = bundle.getInt("menuItemId");
        menuItemName = bundle.getString("menuItemName");

        if (actionBar!=null) actionBar.setTitle(getString(R.string.menu_modifiers) + " "+menuItemName);

        final ProgressDialog progressDialog = ProgressDialog.show(MenuModifiersActivity.this, "Loading", getString(R.string.loading_menu_item_modifiers), true);

        progressDialog.setCanceledOnTouchOutside(true);

        MenuModifiersTask menuItemsTask  = new MenuModifiersTask((menuItemId)) {
            @Override
            public void onSuccess(List<MenuModifierResponse> response) {

                progressDialog.dismiss();

                if (response == null) {
                    Toast.makeText(MenuModifiersActivity.this, getString(R.string.connection_problem), Toast.LENGTH_LONG).show();
                } else {
                    populateData(response);
                }
            }

            @Override
            public void onFailed() {
                Toast.makeText(MenuModifiersActivity.this, getString(R.string.connection_problem), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        };


        menuItemsTask.execute();

    }

    private void populateData(List<MenuModifierResponse> list) {

        gridView = (GridView) findViewById(R.id.gridView1);
        customGridAdapter = new MenuModifierAdapter(this, R.layout.menu_item_row, list) {
            @Override
            public void pickMenuItem(MenuModifierResponse item) {

                AddMenuModifierToTicketItemTask addMenuItemToTicketTask = new AddMenuModifierToTicketItemTask(ticketItemId, item.id, MySharedPreference.getInteger(getApplicationContext(),"userId")) {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getApplicationContext(),"on success",Toast.LENGTH_LONG).show();
                            ActivityInvoker.notifyChangesToTicketItems(getApplicationContext());
                            ActivityInvoker.goToTicketItems(getApplicationContext(), ticketId);
                        }

                        @Override
                        public void onFailed() {
                            Toast.makeText(getApplicationContext(),"on failure",Toast.LENGTH_LONG).show();
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
