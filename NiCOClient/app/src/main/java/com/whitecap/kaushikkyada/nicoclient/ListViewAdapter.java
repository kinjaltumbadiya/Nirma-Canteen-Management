package com.whitecap.kaushikkyada.nicoclient;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Kaushik Kyada on 16-04-2017.
 */

public class ListViewAdapter extends BaseAdapter {
    Activity context;
    String title[];
    String description[];
    int[] images;

    public ListViewAdapter(Activity context, String[] title, String[] description, int[] images ) {
        super();
        this.context = context;
        this.title = title;
        this.description = description;
        this.images = images;

    }

    public int getCount() {
        // TODO Auto-generated method stub
        return title.length;
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
        TextView txtViewTitle;
        TextView txtViewDescription;
        ImageView img_food;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub
        ViewHolder holder;
        LayoutInflater inflater =  context.getLayoutInflater();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.listview_row, null);
            holder = new ViewHolder();
            holder.txtViewTitle = (TextView) convertView.findViewById(R.id.textView1);
            holder.txtViewDescription = (TextView) convertView.findViewById(R.id.textView2);
            holder.img_food = (ImageView) convertView.findViewById(R.id.img_food);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtViewTitle.setText(title[position]);
        holder.txtViewDescription.setText(description[position]);
        holder.img_food.setImageDrawable(convertView.getResources().getDrawable(images[position]));
        return convertView;
    }
}
