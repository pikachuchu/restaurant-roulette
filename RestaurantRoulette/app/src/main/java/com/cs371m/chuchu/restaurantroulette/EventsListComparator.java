package com.cs371m.chuchu.restaurantroulette;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

/**
 * Created by chuchu on 11/17/15.
 */
public class EventsListComparator implements Comparator<Map<String, String>> {
    private String sortKey;

    public EventsListComparator(String sortKey) {
        this.sortKey = sortKey;
    }

    @Override
    public int compare(Map<String, String> lhs, Map<String, String> rhs) {
        String left = lhs.get(sortKey);
        String right = rhs.get(sortKey);
        if (sortKey.equals("doubleDistance")) {
            double leftDist = Double.parseDouble(left);
            double rightDist = Double.parseDouble(right);
            if (leftDist < rightDist) {
                return -1;
            } else if (leftDist > rightDist) {
                return 1;
            } else {
                return 0;
            }
        } else if (sortKey.equals("datetime"))  {
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            try {
                Date leftDate = df.parse(left);
                Date rightDate = df.parse(right);
                return leftDate.compareTo(rightDate);
            } catch (ParseException e) {
                Log.d("EventsListComparator", e.getMessage());
                return left.compareToIgnoreCase(right);
            }
        }
        return left.compareToIgnoreCase(right);
    }
}
