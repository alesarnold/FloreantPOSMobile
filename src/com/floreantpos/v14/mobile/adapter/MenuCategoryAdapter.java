package com.floreantpos.v14.mobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.floreantpos.response.MenuCategoryResponse;
import com.floreantpos.v14.mobile.R;

import java.util.ArrayList;
import java.util.List;

public abstract class MenuCategoryAdapter extends ArrayAdapter<MenuCategoryResponse> {
    private Context context;
    private int layoutResourceId;
    private List<MenuCategoryResponse> data = new ArrayList<MenuCategoryResponse>();

    public MenuCategoryAdapter(Context context, int layoutResourceId, List<MenuCategoryResponse> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder;

        final MenuCategoryResponse item = data.get(position);


        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RecordHolder();
            holder.txtName = (TextView) row.findViewById(R.id.item_text);

            row.setTag(holder);


        } else {
            holder = (RecordHolder) row.getTag();
        }

        holder.txtName.setText((item.name));

        holder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedToMenuGroups(item.id);
            }
        });

        return row;

    }

    static class RecordHolder {
        TextView txtName;

    }

    public abstract void proceedToMenuGroups(int catId);
}
