package com.floreantpos.v14.mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.floreantpos.response.TicketItemResponse;
import com.floreantpos.response.TicketResponse;
import com.floreantpos.v14.mobile.R;
import com.floreantpos.v14.mobile.tasks.IncreaseItemToTicketTask;
import com.floreantpos.v14.mobile.tasks.ReduceItemFromTicketTask;
import com.floreantpos.v14.mobile.utils.ActivityInvoker;

import java.util.List;

public class TicketItemsAdapter extends ArrayAdapter<TicketItemResponse>  {

    private final LayoutInflater mInflater;
    private Context context;
    private int ticketId;
    private int userId;

    public TicketItemsAdapter(Context context, int ticketId, int userId) {
        super(context, android.R.layout.simple_list_item_2);
        this.context=context;
        this.ticketId=ticketId;
        this.userId=userId;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<TicketItemResponse> data) {
        clear();
        if (data != null) {
            for (TicketItemResponse appEntry : data) {
                add(appEntry);
            }
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final View view;

        view = mInflater.inflate(R.layout.ticket_item_row, parent, false);

        final TicketItemResponse item = getItem(position);

        final TextView noTv = (TextView) view.findViewById(R.id.no_tv);
        noTv.setText(String.valueOf(position+1));
        noTv.setFocusable(false);

        final TextView itemNameTV = (TextView) view.findViewById(R.id.item_name_tv);
        itemNameTV.setText((item.name));
        itemNameTV.setFocusable(false);

        final TextView qtyTV = (TextView) view.findViewById(R.id.qty_tv);
        qtyTV.setText(String.valueOf(item.itemCount));

        qtyTV.setFocusable(false);

        final ImageButton reduceButton = (ImageButton) view.findViewById(R.id.reduceButton);
        reduceButton.setFocusable(false);

        reduceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ReduceItemFromTicketTask reduceItemFromTicketTask = new ReduceItemFromTicketTask(ticketId, item.id, userId) {
                    @Override
                    public void onSuccess(TicketResponse response) {
                        setData(response.ticketItems);
                        notifyDataSetChanged();
                        ActivityInvoker.notifyChangesToTicketList(context);
                    }

                    @Override
                    public void onFailed() {
                        Toast.makeText(context, context.getString(R.string.connection_problem), Toast.LENGTH_LONG).show();
                    }
                };

                reduceItemFromTicketTask.execute();
            }
        });


        ImageButton increaseButton = (ImageButton) view.findViewById(R.id.increaseButton);
        increaseButton.setFocusable(false);

        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IncreaseItemToTicketTask increaseItemToTicketTask = new IncreaseItemToTicketTask(ticketId, item.id, userId) {
                    @Override
                    public void onSuccess(TicketResponse ticketResponse) {
                        setData(ticketResponse.ticketItems);
                        notifyDataSetChanged();

                        ActivityInvoker.notifyChangesToTicketList(context);
                    }

                    @Override
                    public void onFailed() {

                        Toast.makeText(context, context.getString(R.string.connection_problem), Toast.LENGTH_LONG).show();
                    }
                };

                increaseItemToTicketTask.execute();
            }
        });


        Toast.makeText(context,""+item.ticketItemModifiers.size(),Toast.LENGTH_SHORT).show();

        if (item.ticketItemModifiers.size()>0) {
            TextView modifierName = (TextView) view.findViewById(R.id.modifierNameTV);
            modifierName.setText((item.ticketItemModifiers.get(0).name));
            modifierName.setFocusable(false);

            TextView modifierQty = (TextView) view.findViewById(R.id.modifierQtyTV);

            modifierQty.setText(String.valueOf(item.ticketItemModifiers.get(0).itemCount));
            modifierQty.setFocusable(false);

        } else {
            View modifierBand = view.findViewById(R.id.modifierBand);
            modifierBand.setVisibility(View.GONE);
        }

        return view;
    }

}
