package com.example.mypc.inventory;

import static com.example.mypc.inventory.constants.FIRST_COLUMN;
import static com.example.mypc.inventory.constants.SECOND_COLUMN;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapters2 extends BaseAdapter{

    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    TextView txtFirst;
    TextView txtSecond;
    public ListViewAdapters2(Activity activity,ArrayList<HashMap<String, String>> list){
        super();
        this.activity=activity;
        this.list=list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub



        LayoutInflater inflater=activity.getLayoutInflater();

        if(convertView == null){

            convertView=inflater.inflate(R.layout.colum_row2, null);

            txtFirst=(TextView) convertView.findViewById(R.id.title);
            txtSecond=(TextView) convertView.findViewById(R.id.stock);

        }

        HashMap<String, String> map=list.get(position);
        txtFirst.setText(map.get("offer"));
        txtSecond.setText(map.get("price"));

        return convertView;
    }

}
