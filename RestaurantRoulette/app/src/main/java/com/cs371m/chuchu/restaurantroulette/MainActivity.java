package com.cs371m.chuchu.restaurantroulette;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<HashMap<String,String>> nearbyEventsList;
    private ArrayList<HashMap<String,String>> myEventsList;
    private Toast toast;
    private boolean nearbyEventsDisplayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton createButton = (FloatingActionButton) findViewById(R.id.createEvent);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ParseUser.getCurrentUser() == null) {
                    doToast("Please Login");
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, CreateEvent.class);
                    startActivity(intent);
                }
            }
        });

        Button myEventsButton = (Button) findViewById(R.id.myEvents);
        myEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ParseUser.getCurrentUser() == null) {
                    doToast("Please Login");
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                } else {
                    Button button = (Button) v;
                    TextView title = (TextView) findViewById(R.id.title);
                    if (nearbyEventsDisplayed) {
                        ListViewAdapter adapter = new ListViewAdapter(MainActivity.this, myEventsList);
                        listView.setAdapter(adapter);
                        button.setText("Nearby Events");
                        title.setText("My Events");
                        nearbyEventsDisplayed = false;
                    } else {
                        ListViewAdapter adapter = new ListViewAdapter(MainActivity.this, nearbyEventsList);
                        listView.setAdapter(adapter);
                        button.setText("My Events");
                        title.setText("Nearby Events");
                        nearbyEventsDisplayed = true;
                    }
                }
            }
        });

        listView = (ListView) findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
            {
                Intent intent = new Intent(MainActivity.this, EventDetails.class);
                if (nearbyEventsDisplayed) {
                    intent.putExtra("event", nearbyEventsList.get(position));
                } else {
                    intent.putExtra("event", myEventsList.get(position));
                }
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
        } else if (id == R.id.action_refresh) {
            fetchEvents();
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
                    myEventsList = new ArrayList<>();
                    nearbyEventsList = new ArrayList<>();
                    for (ParseObject po : objects) {
                        // only add events user is not yet attending and that have room
                        // TODO: delete past events
                        List<String> attendees = po.getList("attendees");
                        if (attendees.size() == po.getInt("number")) {
                            break;
                        }
                        boolean isMyEvent = false;
                        boolean checkUsername = ParseUser.getCurrentUser() != null;
                        String username = "";
                        if (checkUsername) {
                            username = ParseUser.getCurrentUser().getUsername();
                        }
                        String attendeesStr = "";
                        for (String attendee : attendees) {
                            if (checkUsername && attendee.equals(username)) {
                                isMyEvent = true;
                                break;
                            }
                            attendeesStr += attendee + ", ";
                        }
                        HashMap<String, String> curr = new HashMap<>();
                        curr.put("objectId", po.getObjectId());
                        curr.put("host", po.getString("host"));
                        curr.put("restaurant", po.getString("restaurant"));
                        curr.put("date", po.getString("date"));
                        curr.put("time", po.getString("time"));
                        curr.put("price", po.getString("price"));
                        curr.put("number", po.getString("number"));
                        if (!attendeesStr.equals("")) {
                            curr.put("attendees", attendeesStr.substring(0, attendeesStr.length() - 2));
                        }
                        if (isMyEvent) {
                            myEventsList.add(curr);
                        } else {
                            nearbyEventsList.add(curr);
                        }
                    }
                    ListViewAdapter adapter = new ListViewAdapter(MainActivity.this, nearbyEventsList);
                    listView.setAdapter(adapter);
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
