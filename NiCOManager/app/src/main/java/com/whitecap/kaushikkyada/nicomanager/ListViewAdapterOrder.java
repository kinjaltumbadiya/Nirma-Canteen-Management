package com.whitecap.kaushikkyada.nicomanager;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Kaushik Kyada on 16-04-2017.
 */

public class ListViewAdapterOrder extends BaseAdapter {
    Activity context;
    ArrayList<String> order_id;
    ArrayList<String> quantity;
    ArrayList<String> item;
    ArrayList<String> order_time;
    ArrayList<String> emails;

    int arraysize;


    public ListViewAdapterOrder(Activity context, ArrayList<String> emails, ArrayList<String> order_id, ArrayList<String> item, ArrayList<String> quantity, ArrayList<String> order_time, int arraysize) {
        super();
        this.context = context;
        this.emails = emails;
        this.order_id = order_id;
        this.item = item;
        this.quantity = quantity;
        this.order_time = order_time;
        this.arraysize=arraysize;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return arraysize;
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    private class ViewHolder {
        TextView txt_item;
        TextView txt_quantity;
        TextView txt_time;
        TextView txt_email;

    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub
        ViewHolder holder;
        LayoutInflater inflater =  context.getLayoutInflater();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.listview_orderlist_row, null);
            holder = new ViewHolder();
            holder.txt_item = (TextView) convertView.findViewById(R.id.txt_item);
            holder.txt_quantity = (TextView) convertView.findViewById(R.id.txt_quantity);
            holder.txt_time = (TextView) convertView.findViewById(R.id.txt_time);
            holder.txt_email = (TextView) convertView.findViewById(R.id.txt_email);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txt_item.setText(item.get(position));
        holder.txt_quantity.setText("Quantity: "+quantity.get(position));
        holder.txt_time.setText("Order Time: "+order_time.get(position));
        holder.txt_email.setText(emails.get(position));

        return convertView;
    }
}
