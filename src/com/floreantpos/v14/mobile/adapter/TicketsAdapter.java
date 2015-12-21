package com.floreantpos.v14.mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.floreantpos.response.TicketResponse;
import com.floreantpos.v14.mobile.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class TicketsAdapter extends ArrayAdapter<TicketResponse>  {

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
    private static NumberFormat nf = new DecimalFormat("#.0");

    private final LayoutInflater mInflater;

    public TicketsAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_2);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<TicketResponse> data) {
        clear();
        if (data != null) {
            for (TicketResponse appEntry : data) {
                add(appEntry);
            }
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final View view;

        view = mInflater.inflate(R.layout.ticket_row, parent, false);

        final TicketResponse item = getItem(position);

        final TextView orderTypeTV = (TextView) view.findViewById(R.id.servant_name_tv);
        orderTypeTV.setText(item.ownerName);

        final TextView tableNumberTV = (TextView) view.findViewById(R.id.table_number_tv);
        tableNumberTV.setText(String.valueOf(item.tableNumber));

        final TextView checkingTimeTV = (TextView) view.findViewById(R.id.checkin_time_tv);
        checkingTimeTV.setText(sdf.format(item.createDate));

        final TextView totalTV = (TextView) view.findViewById(R.id.total_order_tv);
        totalTV.setText(nf.format(item.totalAmount));

        return view;
    }

}
