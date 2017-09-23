package com.whitecap.kaushikkyada.nicoclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Kaushik Kyada on 17-04-2017.
 */

public class PurchaseFoodFragment extends Fragment implements AdapterView.OnItemClickListener {
    ListView list_purchase_food;
    ListViewAdapter lviewAdapter;
    String email = "";
    String list[]={"Burger","Pizza","Manchurian","Noodles"};
    String price[]={"40 CP","50 CP","42 CP","40 CP"};
    int images[]={R.drawable.ic_burger,R.drawable.ic_pizza,R.drawable.ic_manchurian,R.drawable.ic_noodles};

    int aprice[]={40,50,42,40};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_purchase_food, container, false);
        list_purchase_food=(ListView)v.findViewById(R.id.list_purchase_food);
        lviewAdapter = new ListViewAdapter(getActivity(), list, price, images);
        list_purchase_food.setAdapter(lviewAdapter);
        list_purchase_food.setOnItemClickListener(this);

        SharedPreferences prefs = getActivity().getSharedPreferences("userdata", MODE_PRIVATE);
        String restoredText = prefs.getString("email", "");
        if (restoredText != null) {
            email=restoredText;
        }

        return v;
    }
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
        // TODO Auto-generated method stub
        Intent i=new Intent(getActivity(),GetOrder.class);
        i.putExtra("item",list[position]);
        i.putExtra("price",aprice[position]);
        i.putExtra("image",images[position]);
        startActivity(i);

    }
}
