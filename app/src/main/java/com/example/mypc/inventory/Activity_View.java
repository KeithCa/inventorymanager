package com.example.mypc.inventory;

import helper.SQLiteHandler;
import volley.AppController;
import volley.Config_URL;
import helper.SessionManager;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class Activity_View extends Activity
{
    private Button btnLogin;
    private static final String TAG = Activity_View.class.getSimpleName();
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private TextView result_title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        result_title = (TextView) findViewById(R.id.result_title);
        Intent i = getIntent();
        String barcode = i.getStringExtra("barcode");

        String tag_string_req = "req_login";


        StringRequest strReq = new StringRequest(Method.GET,
                "https://api.upcitemdb.com/prod/trial/lookup?upc="+barcode, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "View Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = false;

                    // Check for error node in json
                    if (!error) {

                        // Now store the user in SQLite


                        JSONArray user = jObj.getJSONArray("items");
                        JSONObject c = user.getJSONObject(0);
                        String name = c.getString("title");
                        Log.d(TAG, "title Response: " + name);
                        name.toString();
                        result_title.setText(name);


                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    }

