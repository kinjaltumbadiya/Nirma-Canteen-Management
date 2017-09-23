package com.whitecap.kaushikkyada.nicocashior;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextView txt_current_cp;
    EditText edt_emailid;
    EditText edt_cpamount;
    EditText edt_password;
    Button btn_transfer,btn_logout;
    StringRequest strReq;
    String email;
    int current_cp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt_current_cp=(TextView)findViewById(R.id.txt_current_cp);
        edt_emailid=(EditText)findViewById(R.id.edt_emailid);
        edt_cpamount=(EditText)findViewById(R.id.edt_cpamount);
        edt_password=(EditText)findViewById(R.id.edt_password);
        btn_transfer=(Button)findViewById(R.id.btn_transfer);
        btn_logout=(Button)findViewById(R.id.btn_logout);
        SharedPreferences prefs = getSharedPreferences("userdata", MODE_PRIVATE);
        String restoredText = prefs.getString("email", "");
        if (!restoredText.equals("")) {
            email=restoredText;
            uploadData(email);
        }
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("userdata", MODE_PRIVATE).edit();
                editor.clear();
                editor.commit();
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        btn_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int order_cp=Integer.parseInt(edt_cpamount.getText().toString());
                if (current_cp < order_cp) {
                    Toast.makeText(getApplicationContext(), "Not sufficient CP in account", Toast.LENGTH_LONG).show();
                } else {
                    int r_cp = current_cp - order_cp;
                    String transfercp=order_cp+"";
                    String remain_cp = r_cp + "";
                    String receiveremail = edt_emailid.getText().toString().trim();
                    String password = edt_password.getText().toString().trim();
                    if (!receiveremail.equals("")) {
                        transferCP(email, receiveremail, transfercp, remain_cp,password);
                    }
                }
            }
        });
    }
    public boolean transferCP(final String email, final String receiveremail, final String transfercp,final String remain_cp,final String password) {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Transferring cp details...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        strReq = new StringRequest(Request.Method.POST, AppConfig.db_transfer_cp, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("Response", s.toString());
                try {
                    JSONObject jObj = new JSONObject(s);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        boolean notfound = jObj.getBoolean("notfound");
                        if (notfound){
                            Toast.makeText(getApplicationContext(), "Receiver account not found", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(getApplicationContext(), "Transfer successfully", Toast.LENGTH_LONG).show();
                            edt_password.setText("");
                            edt_cpamount.setText("");
                            uploadData(email);
                        }
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
                params.put("email", email);
                params.put("receiveremail", receiveremail);
                params.put("transfercp", transfercp);
                params.put("remain_cp", remain_cp);
                params.put("password", password);
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
