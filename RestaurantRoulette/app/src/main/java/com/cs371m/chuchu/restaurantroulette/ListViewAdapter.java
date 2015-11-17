package com.cs371m.chuchu.restaurantroulette;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {

    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    TextView txtFirst;
    TextView txtSecond;
    TextView txtThird;

    public ListViewAdapter(Activity activity, ArrayList<HashMap<String, String>> list) {
        super();
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=activity.getLayoutInflater();

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.column_row, null);

            txtFirst = (TextView) convertView.findViewById(R.id.restaurant);
            txtSecond = (TextView) convertView.findViewById(R.id.datetime);
            txtThird = (TextView) convertView.findViewById(R.id.distance);
        }

        HashMap<String, String> map = list.get(position);
        txtFirst.setText(map.get("restaurant"));
        txtSecond.setText(map.get("datetime"));
        txtThird.setText(map.get("distance"));

        return convertView;
    }

}