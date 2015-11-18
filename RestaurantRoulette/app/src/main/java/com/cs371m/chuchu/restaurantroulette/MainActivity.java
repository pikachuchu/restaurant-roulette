package com.cs371m.chuchu.restaurantroulette;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SortItemsDialog.SortItemsListener {

    private ListView listView;
    private ArrayList<HashMap<String,String>> nearbyEventsList;
    private ArrayList<HashMap<String,String>> myEventsList;
    private Toast toast;
    private boolean nearbyEventsDisplayed = true;
    private Button myEventsButton;
    private TextView title;
    protected final int CREATE_EVENTS_REQUEST = 333;
    protected final int EVENT_DETAILS_REQUEST = 334;

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
                    startActivityForResult(intent, CREATE_EVENTS_REQUEST);
                }
            }
        });

        title = (TextView) findViewById(R.id.title);
        myEventsButton = (Button) findViewById(R.id.myEvents);
        myEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ParseUser.getCurrentUser() == null) {
                    doToast("Please Login");
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                } else {
                    Button button = (Button) v;
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
                intent.putExtra("isMyEvent", !nearbyEventsDisplayed);
                if (nearbyEventsDisplayed) {
                    intent.putExtra("event", nearbyEventsList.get(position));
                } else {
                    intent.putExtra("event", myEventsList.get(position));
                }
                startActivityForResult(intent, EVENT_DETAILS_REQUEST);
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
            intent.putExtra("nearbyEvents", nearbyEventsList);
            intent.putExtra("myEvents", myEventsList);
            intent.putExtra("nearbyEventsDisplayed", nearbyEventsDisplayed);
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
        } else if (id == R.id.action_sort_by) {
            SortItemsDialog sortItemsDialog = new SortItemsDialog();
            sortItemsDialog.show(getFragmentManager(), "sort");
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchEvents() {
        final LatLng currLoc = LocationHelper.getCurrentLocation(MainActivity.this);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event")
                .whereGreaterThan("datetime", new Date())
                .whereNear("location", new ParseGeoPoint(currLoc.latitude, currLoc.longitude));

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null) {
                    Log.d("Events", e.getMessage());
                } else {
                    myEventsList = new ArrayList<>();
                    nearbyEventsList = new ArrayList<>();
                    for (ParseObject po : objects) {
                        List<String> attendees = po.getList("attendees");

                        boolean isMyEvent = false;
                        boolean checkUsername = ParseUser.getCurrentUser() != null;
                        String username = "";
                        if (checkUsername) {
                            username = ParseUser.getCurrentUser().getUsername();
                        }
                        String attendeesStr = "";
                        for (String attendee : attendees) {
                            attendeesStr += attendee + ", ";
                            if (checkUsername && attendee.equals(username)) {
                                isMyEvent = true;
                                break;
                            }
                        }
                        // only add nearby events that have room
                        if (!isMyEvent && attendees.size() == po.getInt("max_attendees")) {
                            break;
                        }
                        HashMap<String, String> curr = new HashMap<>();

                        curr.put("objectId", po.getObjectId());
                        curr.put("host", po.getString("host"));
                        curr.put("restaurant", po.getString("restaurant"));
                        curr.put("address", po.getString("address"));

                        // get location data
                        ParseGeoPoint point = po.getParseGeoPoint("location");

                        curr.put("latitude", String.valueOf(point.getLatitude()));
                        curr.put("longitude", String.valueOf(point.getLongitude()));

                        float[] distance = new float[1];
                        Location.distanceBetween(currLoc.latitude, currLoc.longitude,
                                point.getLatitude(), point.getLongitude(), distance);

                        double miles = distance[0] * 0.000621371;
                        curr.put("distance", String.format("%.2f mi", miles));
                        curr.put("doubleDistance", String.valueOf(miles));

                        // get date
                        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                        curr.put("datetime", df.format(po.getDate("datetime")));

                        // get price
                        String price = "";
                        for (int i = 0; i < po.getInt("price"); i++) {
                            price += "$";
                        }
                        curr.put("price", price);

                        // get list of current attendees
                        curr.put("maxAttendees", String.valueOf(po.getInt("max_attendees")));
                        if (!attendeesStr.equals("")) {
                            curr.put("attendees", attendeesStr.substring(0, attendeesStr.length() - 2));
                        }

                        // add event to appropriate list
                        if (isMyEvent) {
                            myEventsList.add(curr);
                        } else {
                            nearbyEventsList.add(curr);
                        }
                    }
                    // sort lists by time
                    onSelectSortItem("Time");

                    setListViewAdapter();
                }
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CREATE_EVENTS_REQUEST) {
                nearbyEventsDisplayed = false;
                fetchEvents();
                myEventsButton.setText("Nearby Events");
                title.setText("My Events");
            } else if (requestCode == EVENT_DETAILS_REQUEST) {
                fetchEvents();
            }
        }
    }

    public void doToast(String text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onSelectSortItem(String sortOption) {
        String sortKey = "";

        if (sortOption.equals("Distance")) {
            sortKey = "doubleDistance";
        } else if (sortOption.equals("Time")) {
            sortKey = "datetime";
        } else if (sortOption.equals("Name")) {
            sortKey = "restaurant";
        } else {
            doToast("Cannot sort by: " + sortOption);
            return;
        }

        EventsListComparator comparator = new EventsListComparator(sortKey);
        Collections.sort(myEventsList, comparator);
        Collections.sort(nearbyEventsList, comparator);
        setListViewAdapter();
    }

    private void setListViewAdapter() {
        // set listView adapter
        if (nearbyEventsDisplayed) {
            ListViewAdapter adapter = new ListViewAdapter(MainActivity.this, nearbyEventsList);
            listView.setAdapter(adapter);
        } else {
            ListViewAdapter adapter = new ListViewAdapter(MainActivity.this, myEventsList);
            listView.setAdapter(adapter);
        }
    }
}
