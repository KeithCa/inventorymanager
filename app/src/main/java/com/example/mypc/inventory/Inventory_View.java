package com.example.mypc.inventory;
import static com.example.mypc.inventory.constants.FIRST_COLUMN;
import static com.example.mypc.inventory.constants.SECOND_COLUMN;

import java.util.ArrayList;
import java.util.HashMap;
import helper.SQLiteHandler;
import volley.AppController;
import volley.Config_URL;
import helper.SessionManager;
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

public class Inventory_View extends Activity {
    private EditText inputEmail;
    private static final String TAG = Inventory_View.class.getSimpleName();
    private SessionManager session;
    private SQLiteHandler db;
    private TextView result_title;
    private TextView result_stock;
    private TextView result_ean;
    private TextView result_supplier;
    private TextView result_offer;
    private TextView result_price;
    private Button btnHistory;
    private Button btnUpdate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_view);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        result_title = (TextView) findViewById(R.id.result_title);
        result_stock = (TextView) findViewById(R.id.result_stock);
        result_ean = (TextView) findViewById(R.id.result_ean);
        result_supplier = (TextView) findViewById(R.id.result_supplier);
        result_offer = (TextView) findViewById(R.id.result_offer);
        result_price = (TextView) findViewById(R.id.result_price);

        Intent i = getIntent();
        HashMap<String, String> id = (HashMap<String, String>) i.getSerializableExtra("id");
        HashMap<String, String> inv = db.getAllInvDetails(id.get("title"));


        final String title = inv.get("title");
        final String ean = inv.get("ean");
        final String supplier = inv.get("supplier");
        final String price = inv.get("price");
        final String offer = inv.get("offer");
        final String stock = inv.get("stock");

        result_offer.setText(offer);
        result_price.setText(price);
        result_ean.setText(ean);
        result_title.setText(title);
        result_stock.setText(stock);
        result_supplier.setText(supplier);

        btnHistory = (Button) findViewById(R.id.result_history);
        // Inventory button click event
        btnHistory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Inventory_View.this, history_list.class);
                Log.d(TAG, "passing ean to history list: " + ean.toString());
                intent.putExtra("ean", ean);
                startActivity(intent);
            }
        });


    }
}