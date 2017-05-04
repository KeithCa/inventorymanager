package com.example.mypc.inventory;

import helper.SQLiteHandler;
import volley.AppController;
import volley.Config_URL;
import helper.SessionManager;
import android.graphics.Typeface;

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
    private TextView result_ean;
    private TextView result_supplier;
    private TextView result_offer;
    private TextView result_price;
    private TextView result_stock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        Typeface face1 = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Semibold.ttf");

        Typeface face2 = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");

        result_title = (TextView) findViewById(R.id.result_title);
        result_ean = (TextView) findViewById(R.id.result_ean);
        result_supplier = (TextView) findViewById(R.id.result_supplier);
        result_offer = (TextView) findViewById(R.id.result_offer);
        result_price = (TextView) findViewById(R.id.result_price);
        result_stock = (TextView) findViewById(R.id.result_stock);

        Intent i = getIntent();
        final String barcode = i.getStringExtra("barcode");

        String tag_string_req = "req_login";

        Log.d(TAG, "Barcode: " + barcode);
        StringRequest strReq = new StringRequest(Method.GET,
                "https://api.upcitemdb.com/prod/trial/lookup?upc="+barcode, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Android store Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = false;

                    // Check for error node in json
                    if (!error) {

                        // Now store the user in SQLite


                        JSONArray user = jObj.getJSONArray("items");
                        JSONObject c = user.getJSONObject(0);
                        final String name = c.getString("title");
                        final String ean = c.getString("ean");
                        final String supplier = c.getString("publisher");
                        name.toString();
                        ean.toString();
                        supplier.toString();
                        result_title.setText(name);
                        result_ean.setText(ean);
                        result_supplier.setText(supplier);

                        JSONArray offers = c.getJSONArray("offers");
                        JSONObject o = offers.getJSONObject(0);
                        final String offer = o.getString("merchant");
                        final String price = o.getString("price");
                        offer.toString();
                        price.toString();
                        result_offer.setText(offer);
                        result_price.setText(price);


                        String tag_string_req = "req_login";
                        StringRequest strReq = new StringRequest(Method.POST,
                                Config_URL.URL_SEARCH, new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "Online store Response: " + response.toString());


                                try {
                                    JSONObject jObj = new JSONObject(response);
                                    boolean error = jObj.getBoolean("error");
                                    if (!error) {

                                        Toast.makeText(getApplicationContext(), "search stored", Toast.LENGTH_LONG).show();

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
                                params.put("supplier", supplier);
                                params.put("offer", offer);
                                params.put("price", price);

                                return params;
                            }

                        };

                        // Adding request to request queue
                        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


                        Button btnAdd;
                        btnAdd = (Button) findViewById(R.id.inv_add);

                        // Logout button click event
                        btnAdd.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(Activity_View.this, Inventory_Add.class);
                                i.putExtra("name", name);
                                i.putExtra("ean", ean);
                                i.putExtra("supplier", supplier);
                                i.putExtra("offer", offer);
                                i.putExtra("price", price);
                                startActivity(i);
                            }
                        });

                        Button btnSell;
                        btnSell = (Button) findViewById(R.id.inv_sell);

                        if(db.checkInv(ean)){
                            btnSell.setVisibility(View.VISIBLE);
                            int test = db.getStock(ean);

                            String cast = String.valueOf(test);
                            Log.e(TAG, "stock Error: " + cast);
                            result_stock.setText(cast);
                        }

                        // Logout button click event
                        btnSell.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(Activity_View.this, Inventory_Sell.class);
                                i.putExtra("name", name);
                                i.putExtra("ean", ean);
                                i.putExtra("price", price);
                                startActivity(i);
                            }
                        });





                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Item was not found on database ", Toast.LENGTH_LONG).show();
                    Button btnAdd;
                    btnAdd = (Button) findViewById(R.id.inv_add);

                    // Logout button click event
                    btnAdd.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                    Intent i = new Intent(Activity_View.this, Inventory_Add.class);
                    i.putExtra("ean", barcode);
                    startActivity(i);
                        }
                    });
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


