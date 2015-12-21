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
import com.floreantpos.v14.mobile.adapter.MenuGroupAdapter;
import com.floreantpos.v14.mobile.tasks.MenuGroupsTask;
import com.floreantpos.response.MenuGroupResponse;
import com.floreantpos.v14.mobile.utils.ActivityInvoker;

import java.util.List;

public class MenuGroupsActivity extends StyledBaseActivity {
    private GridView gridView;
    private MenuGroupAdapter customGridAdapter;
    private static int CAT_ID;
    private static int TICKET_ID;

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

        if (bundle == null) return;

        int CAT_ID = bundle.getInt("catId");
        TICKET_ID = bundle.getInt("ticketId");

        final ProgressDialog progressDialog = ProgressDialog.show(MenuGroupsActivity.this, "Loading", getString(R.string.loading_menu_groups), true);
        progressDialog.setCanceledOnTouchOutside(true);

        MenuGroupsTask task = new MenuGroupsTask(String.valueOf(CAT_ID)) {
            @Override
            public void onSuccess(List<MenuGroupResponse> responses) {
                progressDialog.dismiss();

                if (responses == null) {
                    Toast.makeText(MenuGroupsActivity.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
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

    private void populateData(List<MenuGroupResponse> list) {

        gridView = (GridView) findViewById(R.id.gridView1);
        customGridAdapter = new MenuGroupAdapter(this, R.layout.menu_group_row, list) {
            @Override
            public void proceedToChild(int groupId) {
                ActivityInvoker.showMenuItems(MenuGroupsActivity.this,groupId, TICKET_ID);
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
