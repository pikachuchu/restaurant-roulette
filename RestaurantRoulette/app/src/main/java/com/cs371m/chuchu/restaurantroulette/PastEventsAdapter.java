package com.cs371m.chuchu.restaurantroulette;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class PastEventsAdapter extends BaseAdapter {

    public ArrayList<HashMap<String, Object>> list;
    Activity activity;
    TextView txtFirst;
    TextView txtSecond;

    public PastEventsAdapter(Activity activity, ArrayList<HashMap<String, Object>> list) {
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

        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.column_row, null);
            txtFirst = (TextView) convertView.findViewById(R.id.restaurant);
            txtSecond = (TextView) convertView.findViewById(R.id.datetime);
        }

        HashMap<String, Object> map = list.get(position);
        txtFirst.setText((String) map.get("restaurant"));
        txtSecond.setText((String) map.get("datetime"));

        return convertView;
    }

}