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
import android.support.v7.app.AppCompatActivity;
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


public class Inventory_Sell extends Activity {
    private static final String TAG = Inventory_Add.class.getSimpleName();
    private TextView result_title;
    private TextView result_ean;
    private EditText result_price;
    private EditText result_stock;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        result_title = (TextView) findViewById(R.id.result_title);
        result_ean = (TextView) findViewById(R.id.result_ean);
        result_price = (EditText) findViewById(R.id.result_price);
        result_stock = (EditText) findViewById(R.id.result_stock);

        Intent i = getIntent();
        final String name = i.getStringExtra("name");
        final String ean = i.getStringExtra("ean");
        final String price = i.getStringExtra("price");

        result_title.setText(name);
        result_ean.setText(ean);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (!session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(Inventory_Sell.this,
                    Activity_Login.class);
            startActivity(intent);
            finish();
        }

        Button btnSell;
        btnSell = (Button) findViewById(R.id.inv_sell);

        // Logout button click event
        btnSell.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int stock = Integer.parseInt(result_stock.getText().toString());
                final String stocks = result_stock.getText().toString();
                final String new_price = result_price.getText().toString();
                if(!db.addSale(name, ean, new_price, stock)){
                    Toast.makeText(getApplicationContext(),
                            "Not enough in stock", Toast.LENGTH_LONG).show();
                }
                else{
                String tag_string_req = "req_login";
                StringRequest strReq = new StringRequest(Method.POST,
                        Config_URL.URL_SALE, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Online store Response: " + response.toString());


                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {

                                Toast.makeText(getApplicationContext(), "inv stored", Toast.LENGTH_LONG).show();

                                // Launch login activity

                            } else {

                                // Error occurred in registration. Get the error
                                // message
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Registration Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting params to register url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("title", name);
                        params.put("ean", ean);
                        params.put("price", new_price);
                        params.put("stock", stocks);
                        params.put("cname", session.cname);

                        return params;
                    }

                };

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
                Intent i = new Intent(getApplicationContext(),
                        Activity_Main.class);
                startActivity(i);
                finish();
            }
        }});


    }
}
