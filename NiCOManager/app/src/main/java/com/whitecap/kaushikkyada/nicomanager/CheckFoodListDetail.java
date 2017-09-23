package com.whitecap.kaushikkyada.nicomanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.HashMap;
import java.util.Map;

public class CheckFoodListDetail extends AppCompatActivity {
    String order_id;
    String item;
    String quantity;
    String order_time;
    String emails;
    TextView txt_order_id;
    Button btn_scan;
    TextView txt_item;
    TextView txt_quantity;
    TextView txt_time;
    TextView txt_email;
    FrameLayout frm_button;
    private IntentIntegrator qrScan;
    StringRequest strReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_food_list_detail);
        Intent i = getIntent();
        emails = i.getExtras().getString("emails");
        order_id = i.getExtras().getString("order_id");
        item = i.getExtras().getString("item");
        quantity = i.getExtras().getString("quantity");
        order_time = i.getExtras().getString("order_time");
        txt_order_id = (TextView) findViewById(R.id.txt_order_id);
        txt_email = (TextView) findViewById(R.id.txt_email);
        btn_scan = (Button) findViewById(R.id.btn_scan);
        frm_button = (FrameLayout) findViewById(R.id.frm_button);
        txt_item = (TextView) findViewById(R.id.txt_item);
        txt_quantity = (TextView) findViewById(R.id.txt_quantity);
        txt_time = (TextView) findViewById(R.id.txt_time);

        txt_order_id.setText("Order Id: " + order_id);
        txt_item.setText(item);
        txt_quantity.setText("Quantity: " + quantity);
        txt_time.setText("Oreder Time: " + order_time);
        txt_email.setText(emails);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan = new IntentIntegrator(CheckFoodListDetail.this);
                qrScan.initiateScan();
            }
        });
        frm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Frm", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(CheckFoodListDetail.this,CheckFoodList.class);
                startActivity(i);
                finish();
            }
        });

    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {

                    String str=result.getContents();
                    if (str.equals(order_id))
                    {
                        Toast.makeText(getApplicationContext(),"Order id matched", Toast.LENGTH_SHORT).show();
                        frm_button.setVisibility(View.VISIBLE);
                        btn_scan.setVisibility(View.GONE);
                        uploadData(emails,order_id);

                    }else{
                        Toast.makeText(getApplicationContext(),"Order id doesn't match", Toast.LENGTH_SHORT).show();
                    }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public boolean uploadData(final String emails, final String order_id) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating delivery status...");
        progressDialog.setCancelable(true);

        progressDialog.show();
        strReq = new StringRequest(Request.Method.POST, AppConfig.db_update_food_status, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("Response", s.toString());
                try {
                    JSONObject jObj = new JSONObject(s);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                            progressDialog.hide();
                    } else {
                        String errormsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errormsg + "", Toast.LENGTH_LONG).show();
                        progressDialog.hide();
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
                params.put("email", emails);
                params.put("order_id", order_id);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);


        return false;
    }


}
