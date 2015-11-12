package com.cs371m.chuchu.restaurantroulette;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by chuchu on 11/12/15.
 */
public class Events {
    public static ArrayList<HashMap<String, String>> getEvents() {
        ArrayList<HashMap<String, String>> events = new ArrayList<HashMap<String, String>>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null) {

                } else {

                }
            }
        });
        return events;
    }
}
