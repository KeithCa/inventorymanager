package com.example.mypc.inventory;
import static com.example.mypc.inventory.constants.FIRST_COLUMN;
import static com.example.mypc.inventory.constants.SECOND_COLUMN;

import java.util.ArrayList;
import java.util.HashMap;
import helper.SQLiteHandler;
import volley.AppController;
import volley.Config_URL;
import helper.SessionManager;

import java.util.Map;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.ArrayAdapter;
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
import java.util.List;
import android.widget.ListView;
import java.util.ArrayList;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;


public class Sale_List extends Activity {
    private ArrayList<HashMap<String, String>> list;
    private static final String TAG = Inventory_Add.class.getSimpleName();
    private ListView ListView;
    private SQLiteHandler db;
    private SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale_list);

    }

    private void populateListView() {
        ListView ListView=(ListView)findViewById(R.id.listView1);
        list=new ArrayList<HashMap<String,String>>();


        db = new SQLiteHandler(getApplicationContext());

        list = db.getSomeSaleDetails();
        ListViewAdapters adapter=new ListViewAdapters(this, list);
        ListView.setAdapter(adapter);
        registerForContextMenu(ListView);
        db.close();

    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select The Action");
        menu.add(0, v.getId(), 0, "View");
        menu.add(0, v.getId(), 0, "Delete");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
        if(item.getTitle().equals("View")){
            Intent i = new Intent(Sale_List.this, Inventory_View.class);
            i.putExtra("id", list.get(info.position));
            Log.d(TAG, "passing value to inv view : " + list.get(info.position));
            startActivity(i);
        }
        else{
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        populateListView();
    }

}