package com.cs371m.chuchu.restaurantroulette;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by chuchu on 11/14/15.
 */
public class MyEvents extends AppCompatActivity {

    private ListView listView;
    private ArrayList<HashMap<String, String>> eventsList;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        listView = (ListView) findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Intent intent = new Intent(MyEvents.this, EventDetails.class);
                intent.putExtra("event", eventsList.get(position));
                startActivity(intent);
            }
        });

        Button eventsNearbyButton = (Button) findViewById(R.id.eventsNearby);
        eventsNearbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ParseUser.getCurrentUser() == null) {
                    doToast("Please Login");
                    Intent intent = new Intent(MyEvents.this, Login.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MyEvents.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        FloatingActionButton createEventButton = (FloatingActionButton) findViewById(R.id.createEvent);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyEvents.this, CreateEvent.class);
                startActivity(intent);
            }
        });

        fetchEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_toggle_view) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivityForResult(intent, 1);
            return true;
        } else if (id == R.id.action_account) {
            if (ParseUser.getCurrentUser() == null) {
                Intent intent = new Intent(this, Login.class);
                startActivityForResult(intent, 1);
            } else {
                Intent intent = new Intent(this, Account.class);
                startActivityForResult(intent, 1);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchEvents() {
         ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
         query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null) {
                    Log.d("Events", e.getMessage());
                } else {
                    ArrayList<HashMap<String, String>> events = new ArrayList<>();
                    for (ParseObject po : objects) {
                        // only add events user is not yet attending
                        // TODO: delete past events
                        List<String> attendees = po.getList("attendees");
                        boolean addObject = false;
                        boolean checkUsername = ParseUser.getCurrentUser() != null;
                        String username = "";
                        if (checkUsername) {
                            username = ParseUser.getCurrentUser().getUsername();
                        }
                        String attendeesStr = "";
                        for (String attendee : attendees) {
                            if (checkUsername && attendee.equals(username)) {
                                addObject = true;
                                break;
                            }
                            attendeesStr += attendee + ", ";
                        }
                        if (addObject) {
                            HashMap<String, String> curr = new HashMap<>();
                            curr.put("objectId", po.getObjectId());
                            curr.put("host", po.getString("host"));
                            curr.put("restaurant", po.getString("restaurant"));
                            curr.put("date", po.getString("date"));
                            curr.put("time", po.getString("time"));
                            curr.put("price", po.getString("price"));
                            curr.put("number", po.getString("number"));
                            curr.put("attendees", attendeesStr);
                            events.add(curr);
                        }
                    }
                    ListViewAdapter adapter=new ListViewAdapter(MyEvents.this, events);
                    listView.setAdapter(adapter);
                    eventsList = events;
                }
            }
        });
    }

    public void doToast(String text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }
}
