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
import com.floreantpos.v14.mobile.adapter.MenuCategoryAdapter;
import com.floreantpos.v14.mobile.tasks.MenuCategoriesTask;
import com.floreantpos.response.MenuCategoryResponse;
import com.floreantpos.v14.mobile.utils.ActivityInvoker;

import java.util.List;

public class MenuCategoriesActivity extends StyledBaseActivity {
    private GridView gridView;
    private MenuCategoryAdapter customGridAdapter;
    static int TICKET_ID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_category);

        final ActionBar actionBar;
        if (Build.VERSION.SDK_INT >= 11) {
            actionBar = getActionBar();
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        } else {
            actionBar = null;
        }

        Bundle bundle = getIntent().getExtras();
        TICKET_ID = bundle.getInt("ticketId");
//
//        if (bundle == null) return;

        final ProgressDialog progressDialog = ProgressDialog.show(MenuCategoriesActivity.this, "Loading", getString(R.string.loading_menu_categories), true);
        progressDialog.setCanceledOnTouchOutside(true);

        MenuCategoriesTask task = new MenuCategoriesTask() {
            @Override
            public void onSuccess(List<MenuCategoryResponse> responses) {
                progressDialog.dismiss();

                if (responses == null) {
                    Toast.makeText(MenuCategoriesActivity.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
                } else {
                    populateData(responses);
                }
            }

            @Override
            public void onFailed() {

            }
        };

        task.execute();
    }

    private void populateData(List<MenuCategoryResponse> list) {

        gridView = (GridView) findViewById(R.id.gridView1);

        customGridAdapter = new MenuCategoryAdapter(this, R.layout.menu_category_row, list) {

            @Override
            public void proceedToMenuGroups(int catId) {
                ActivityInvoker.showMenuGroups(MenuCategoriesActivity.this,catId,TICKET_ID);
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
//        setContentView(R.layout.new_ads_category);
    }
}
