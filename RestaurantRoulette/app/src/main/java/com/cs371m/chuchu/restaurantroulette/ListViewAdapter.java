package com.cs371m.chuchu.restaurantroulette;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter{

    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    TextView txtFirst;
    TextView txtSecond;
    TextView txtThird;
    TextView txtFourth;
    TextView txtFifth;
    TextView txtSixth;

    public ListViewAdapter(Activity activity,ArrayList<HashMap<String, String>> list){
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

            convertView=inflater.inflate(R.layout.column_row, null);

            txtFirst=(TextView) convertView.findViewById(R.id.host);
            txtSecond=(TextView) convertView.findViewById(R.id.restaurant);
            txtThird=(TextView) convertView.findViewById(R.id.time);
            txtFourth=(TextView) convertView.findViewById(R.id.type);
            txtFifth=(TextView) convertView.findViewById(R.id.price);
            txtSixth=(TextView) convertView.findViewById(R.id.distance);

        }

        HashMap<String, String> map=list.get(position);
        txtFirst.setText(map.get("Host"));
        txtSecond.setText(map.get("Restaurant"));
        txtThird.setText(map.get("When?"));
        txtFourth.setText(map.get("Type"));
        txtFifth.setText(map.get("Price"));
        txtSixth.setText(map.get("Distance"));

        return convertView;
    }

}