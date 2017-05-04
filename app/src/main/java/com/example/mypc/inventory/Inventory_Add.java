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


public class Inventory_Add extends Activity {
    private static final String TAG = Inventory_Add.class.getSimpleName();
    private EditText result_title;
    private EditText result_ean;
    private EditText result_supplier;
    private EditText result_offer;
    private EditText result_price;
    private EditText result_stock;
    private EditText result_reorder;
    private Button btnAdd;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        result_title = (EditText) findViewById(R.id.result_title);
        result_ean = (EditText) findViewById(R.id.result_ean);
        result_supplier = (EditText) findViewById(R.id.result_supplier);
        result_offer = (EditText) findViewById(R.id.result_offer);
        result_price = (EditText) findViewById(R.id.result_price);
        result_stock = (EditText) findViewById(R.id.result_stock);
        result_reorder = (EditText) findViewById(R.id.result_reorder);

        Intent i = getIntent();
        final String name = i.getStringExtra("name");
        final String ean = i.getStringExtra("ean");
        final String supplier = i.getStringExtra("supplier");
        final String offer = i.getStringExtra("offer");
        final String price = i.getStringExtra("price");

        result_title.setText(name);
        result_ean.setText(ean);
        result_supplier.setText(supplier);
        result_offer.setText(offer);
        result_price.setText(price);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (!session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(Inventory_Add.this,
                    Activity_Login.class);
            startActivity(intent);
            finish();
        }

        Button btnAdd;
        btnAdd = (Button) findViewById(R.id.inv_add);

        // Logout button click event
        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String stocks = result_stock.getText().toString();
                int stock = Integer.parseInt(stocks);
                final String reorders = result_reorder.getText().toString();
                int reorder = Integer.parseInt(reorders);
                String new_offer = result_offer.getText().toString();
                String new_price = result_price.getText().toString();
                String new_name = result_offer.getText().toString();
                String new_ean = result_price.getText().toString();
                String new_supplier = result_price.getText().toString();
                db.addInv(new_name, new_ean, new_supplier, new_offer, new_price, stock, reorder);
                String tag_string_req = "req_login";
                StringRequest strReq = new StringRequest(Method.POST,
                        Config_URL.URL_INV, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Online store Response: " + response.toString());


                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {

                                Toast.makeText(getApplicationContext(), "inv stored", Toast.LENGTH_LONG).show();


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
                        // Posting params to invadd url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("title", name);
                        params.put("ean", ean);
                        params.put("supplier", supplier);
                        params.put("offer", offer);
                        params.put("price", price);
                        params.put("stock", stocks);
                        params.put("reorder", reorders);
                        Log.d(TAG, "company name" + session.cname);
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
        });


    }
}
