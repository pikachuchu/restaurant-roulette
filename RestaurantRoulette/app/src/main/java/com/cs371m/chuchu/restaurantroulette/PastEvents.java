package com.cs371m.chuchu.restaurantroulette;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by chuchu on 11/26/15.
 */
public class PastEvents extends AppCompatActivity {
    private ArrayList<HashMap<String, Object>> pastEvents;
    private ListView listView;
    final int FINISH_RATING_RESULT = 5920;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_events);

        listView = (ListView) findViewById(R.id.pastEvents);

        fetchPastEvents();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PastEvents.this, RateAttendees.class);
                intent.putExtra("event", pastEvents.get(position));
                startActivityForResult(intent, FINISH_RATING_RESULT);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_show_events) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_account) {
            if (ParseUser.getCurrentUser() == null) {
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, Account.class);
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_past_events, menu);
        return true;
    }

    private void fetchPastEvents() {
        pastEvents = new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event")
            .whereLessThan("datetime", new Date());
        final String username = ParseUser.getCurrentUser().getUsername();

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null) {
                    Log.d("PastEvents", e.getMessage());
                    return;
                }
                for (ParseObject po : objects) {
                    List<String> attendees = po.getList("attendees");
                    List<String> ratedBy = po.getList("ratedBy");
                    if (!attendees.contains(username) || (ratedBy != null && ratedBy.contains(username))) {
                        continue;
                    }
                    attendees.remove(username);
                    if (attendees.size() == 0) {
                        continue;
                    }
                    HashMap<String, Object> curr = new HashMap<String, Object>();
                    curr.put("attendees", attendees);
                    curr.put("objectId", po.getObjectId());
                    curr.put("host", po.getString("host"));
                    curr.put("restaurant", po.getString("restaurant"));
                    curr.put("address", po.getString("address"));

                    // get date
                    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                    curr.put("datetime", df.format(po.getDate("datetime")));

                    pastEvents.add(curr);
                }
                PastEventsAdapter adapter = new PastEventsAdapter(PastEvents.this, pastEvents);
                listView.setAdapter(adapter);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == FINISH_RATING_RESULT) {

            // delete event from past events
            HashMap<String, Object> ratedEvent = (HashMap<String, Object>) data.getSerializableExtra("event");
            pastEvents.remove(ratedEvent);
            listView.setAdapter(new PastEventsAdapter(PastEvents.this, pastEvents));
            // TODO: remove event immediately from listView
            listView.invalidateViews();

            ParseQuery<ParseObject> query = new ParseQuery<>("Event");
            query.getInBackground((String) ratedEvent.get("objectId"), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e != null) {
                        Log.d("PastEvents", e.getMessage());
                        return;
                    }
                    List<String> list = new ArrayList<>();
                    list.add(ParseUser.getCurrentUser().getUsername());
                    object.addAllUnique("ratedBy", list);
                    object.saveInBackground();
                }
            });
        }
    }
}
