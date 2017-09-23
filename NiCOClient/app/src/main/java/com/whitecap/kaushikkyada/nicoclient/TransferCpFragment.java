package com.whitecap.kaushikkyada.nicoclient;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Kaushik Kyada on 17-04-2017.
 */

public class TransferCpFragment extends Fragment {
    TextView txt_current_cp;
    EditText edt_emailid;
    EditText edt_cpamount;
    EditText edt_password;
    Button btn_transfer;
    StringRequest strReq;
    String email;
    int current_cp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_transfercp, container, false);
        txt_current_cp=(TextView)v.findViewById(R.id.txt_current_cp);
        edt_emailid=(EditText)v.findViewById(R.id.edt_emailid);
        edt_cpamount=(EditText)v.findViewById(R.id.edt_cpamount);
        edt_password=(EditText)v.findViewById(R.id.edt_password);
        btn_transfer=(Button)v.findViewById(R.id.btn_transfer);
        SharedPreferences prefs = getActivity().getSharedPreferences("userdata", MODE_PRIVATE);
        String restoredText = prefs.getString("email", "");
        if (!restoredText.equals("")) {
            email=restoredText;
            uploadData(email);
        }
        btn_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int order_cp=Integer.parseInt(edt_cpamount.getText().toString());
                if (current_cp < order_cp) {
                    Toast.makeText(getActivity(), "Not sufficient CP in account", Toast.LENGTH_LONG).show();
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
        return v;
    }
    public boolean transferCP(final String email, final String receiveremail, final String transfercp,final String remain_cp,final String password) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
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
                            Toast.makeText(getActivity(), "Receiver account not found", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(getActivity(), "Transfer successfully", Toast.LENGTH_LONG).show();
                            edt_password.setText("");
                            edt_cpamount.setText("");
                            uploadData(email);
                        }
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
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
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
