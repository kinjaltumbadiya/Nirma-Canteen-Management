package com.whitecap.kaushikkyada.nicoclient;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Kaushik Kyada on 17-04-2017.
 */

public class FoodHistoryFragment extends Fragment {
    ListView list_order_list;
    StringRequest strReq;
    String email;
    ListViewAdapterOrder lviewAdapter;
    ArrayList<String> order_id;
    ArrayList<String> quantity;
    ArrayList<String> item;
    ArrayList<String> order_time;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_my_order, container, false);
        list_order_list = (ListView) v.findViewById(R.id.list_order_list);

        order_id = new ArrayList<>();
        quantity = new ArrayList<>();
        item = new ArrayList<>();
        order_time = new ArrayList<>();
        SharedPreferences prefs = getActivity().getSharedPreferences("userdata", MODE_PRIVATE);
        String restoredText = prefs.getString("email", "");
        if (restoredText != null) {
            email = restoredText;
            updateList(email);
        }

        return v;
    }
    public boolean updateList(final String email) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Checking your order list...");
        progressDialog.setCancelable(true);

        progressDialog.show();
        strReq = new StringRequest(Request.Method.POST, AppConfig.db_food_history_list, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("Response", s.toString());
                try {
                    JSONObject jObj = new JSONObject(s);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        order_id.clear();
                        quantity.clear();
                        item.clear();
                        order_time.clear();
                        int arraysize=0;
                        for (int i = 0; i < (jObj.length() / 4); i++) {
                            order_id.add(i, jObj.getString("order_id" + i));
                            quantity.add(i, jObj.getString("quantity" + i));
                            item.add(i, jObj.getString("item" + i));
                            order_time.add(i, jObj.getString("order_time" + i));
                            arraysize++;
                        }
                        lviewAdapter = new ListViewAdapterOrder(getActivity(),order_id,item,quantity,order_time,arraysize);
                        list_order_list.setAdapter(lviewAdapter);
                        progressDialog.hide();


                    } else {
                        String errormsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity(), errormsg + "", Toast.LENGTH_LONG).show();
                        progressDialog.hide();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Json error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.hide();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getActivity(), "volleyError error:" + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);


        return false;
    }


}
