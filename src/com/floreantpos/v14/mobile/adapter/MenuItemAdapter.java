package com.floreantpos.v14.mobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import com.floreantpos.v14.mobile.R;
import com.floreantpos.v14.mobile.activity.GV;
import com.floreantpos.v14.mobile.activity.PlainVolley;
import com.floreantpos.response.MenuItemResponse;

import java.util.ArrayList;
import java.util.List;

public abstract class MenuItemAdapter extends ArrayAdapter<MenuItemResponse> {
    private Context context;
    private int layoutResourceId;
    private List<MenuItemResponse> data = new ArrayList<MenuItemResponse>();

    public MenuItemAdapter(Context context, int layoutResourceId, List<MenuItemResponse> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder;

        final MenuItemResponse item = data.get(position);

        String url = GV.URL+ "/FetchMenuIcon?itemId=" + item.id;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RecordHolder();
            holder.txtName = (TextView) row.findViewById(R.id.item_text);

            holder.imageItem = (NetworkImageView) row.findViewById(R.id.item_image);


            row.setTag(holder);

        } else {
            holder = (RecordHolder) row.getTag();
        }

        holder.txtName.setText((item.name));

        holder.imageItem.setImageUrl(url, PlainVolley.getInstance().getImageLoader());

        holder.imageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMenuItem(item);
            }
        });
        holder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMenuItem(item);
            }
        });

        return row;

    }

    static class RecordHolder {
        TextView txtName;
        NetworkImageView imageItem;

    }

    public abstract void pickMenuItem(MenuItemResponse item);
}
