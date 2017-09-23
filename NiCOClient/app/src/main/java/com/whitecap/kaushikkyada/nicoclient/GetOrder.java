package com.whitecap.kaushikkyada.nicoclient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class GetOrder extends AppCompatActivity {
    TextView txt_name;
    TextView txt_price;
    TextView txt_pay_cp;
    TextView txt_current_cp;
    EditText edt_quantity;
    ImageView img_food;
    Button btn_pay;
    StringRequest strReq;
    String email;
    int current_cp;
    int order_cp;
    int qunatity;
    String item;
    private static AtomicInteger sNextGeneratedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txt_name = (TextView) findViewById(R.id.txt_name);
        txt_price = (TextView) findViewById(R.id.txt_price);
        txt_pay_cp = (TextView) findViewById(R.id.txt_pay_cp);
        txt_current_cp = (TextView) findViewById(R.id.txt_current_cp);
        img_food = (ImageView) findViewById(R.id.img_food);
        edt_quantity = (EditText) findViewById(R.id.edt_quantity);
        btn_pay = (Button) findViewById(R.id.btn_pay);
        sNextGeneratedId = new AtomicInteger(1);
        SharedPreferences prefs = getSharedPreferences("userdata", MODE_PRIVATE);
        String restoredText = prefs.getString("email", "");
        if (restoredText != null) {
            email=restoredText;
        }
        Intent i = getIntent();
        final int price = i.getExtras().getInt("price");
        item = i.getExtras().getString("item");
        int image = i.getExtras().getInt("image");
        txt_name.setText(item);
        txt_price.setText(price + " CP");
        img_food.setImageDrawable(getResources().getDrawable(image));
        uploadData(email);
        edt_quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                qunatity = Integer.parseInt(edt_quantity.getText().toString());
                txt_pay_cp.setText("Total Pay: " + qunatity + "x" + price + "=" + (qunatity * price) + " CP");
                order_cp = qunatity * price;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_quantity.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter Quantity first", Toast.LENGTH_LONG).show();
                }else {
                    if (current_cp < order_cp) {
                        Toast.makeText(getApplicationContext(), "Not sufficient CP in account", Toast.LENGTH_LONG).show();
                    } else {
                        int r_cp = current_cp - order_cp;
                        String remain_cp = r_cp + "";
                        generateorder(email, item, edt_quantity.getText().toString(), remain_cp);
                    }
                }
            }
        });

    }

    public boolean generateorder(final String email, final String item, final String qunatity,final String remain_cp) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Generating order details...");
        progressDialog.setCancelable(true);

        progressDialog.show();
        strReq = new StringRequest(Request.Method.POST, AppConfig.db_generate_order, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("Response", s.toString());
                try {
                    JSONObject jObj = new JSONObject(s);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Toast.makeText(getApplicationContext(),"Order is generated check your order list", Toast.LENGTH_LONG).show();
                        progressDialog.hide();
                        finish();
                    } else {
                        String errormsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errormsg + "", Toast.LENGTH_LONG).show();
                        progressDialog.hide();
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.hide();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "volleyError error:" + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("item", item);
                params.put("qunatity", qunatity);
                params.put("remain_cp", remain_cp);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
                String currentDateandTime = sdf.format(new Date());
                params.put("time", currentDateandTime);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
        return false;
    }


    public boolean uploadData(final String email) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting account details...");
        progressDialog.setCancelable(true);

        progressDialog.show();
        strReq = new StringRequest(Request.Method.POST, AppConfig.db_get_cp, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("Response", s.toString());
                try {
                    JSONObject jObj = new JSONObject(s);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        String cp = jObj.getString("cp");
                        current_cp = Integer.parseInt(cp);
                        txt_current_cp.setText("Current Account CP: " + current_cp);
                        progressDialog.hide();

                    } else {
                        String errormsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errormsg + "", Toast.LENGTH_LONG).show();
                        progressDialog.hide();
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.hide();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "volleyError error:" + volleyError.getMessage(), Toast.LENGTH_LONG).show();
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
