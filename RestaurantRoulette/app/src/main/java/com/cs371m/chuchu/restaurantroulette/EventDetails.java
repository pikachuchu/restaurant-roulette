package com.cs371m.chuchu.restaurantroulette;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventDetails extends AppCompatActivity {

    private Toast toast;
    private boolean isMyEvent;
    private HashMap<String, String> event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        TextView restaurant = (TextView) findViewById(R.id.restaurant);
        TextView host = (TextView) findViewById(R.id.host);
        TextView distance = (TextView) findViewById(R.id.distance);
        TextView address = (TextView) findViewById(R.id.address);
        TextView datetime = (TextView) findViewById(R.id.datetime);
        TextView attendees = (TextView) findViewById(R.id.attendees);
        TextView maxAttendees = (TextView) findViewById(R.id.maxAttendees);
        TextView price = (TextView) findViewById(R.id.price);

        Intent intent = getIntent();
        event = (HashMap<String, String>) intent.getSerializableExtra("event");
        restaurant.append(event.get("restaurant"));
        host.append(event.get("host"));
        datetime.append(event.get("datetime"));
        address.append(event.get("address"));
        distance.append(event.get("distance"));
        maxAttendees.append(event.get("maxAttendees"));
        if (event.get("attendees") == null || event.get("attendees").equals("")) {
            attendees.append("No attendees");
        } else {
            attendees.append(event.get("attendees"));
        }

        if (event.get("price").equals("")) {
            price.append("N/A");
        } else {
            price.append(event.get("price"));
        }


        Button rsvpButton = (Button) findViewById(R.id.rsvpButton);
        Button backButton = (Button) findViewById(R.id.backButton);

        isMyEvent = getIntent().getBooleanExtra("isMyEvent", false);

        if (isMyEvent) {
            if (event.get("host").equals(ParseUser.getCurrentUser().getUsername())) {
                rsvpButton.setText("Cancel Event");
            } else {
                rsvpButton.setText("Cancel RSVP");
            }
        }
        final String objectId = event.get("objectId");

        rsvpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ParseUser.getCurrentUser() == null) {
                    doToast("Please Login");
                    Intent intent = new Intent(EventDetails.this, Login.class);
                    startActivity(intent);
                }
                ParseQuery<ParseObject> query = new ParseQuery<>("Event");
                query.getInBackground(objectId, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e != null) {
                            Log.d("EventDetails", e.getMessage());
                        } else {
                            String username = ParseUser.getCurrentUser().getUsername();
                            if (isMyEvent) {
                                // delete event if host cancels
                                if (object.get("host").equals(username)) {
                                    object.deleteInBackground(new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                doToast("Successfully canceled event!");
                                                setResult(RESULT_OK);
                                                finish();
                                            }
                                        }
                                    });
                                    return;
                                }
                                // otherwise, remove from attendees list
                                List<String> toRemove = new ArrayList<>();
                                toRemove.add(username);
                                object.removeAll("attendees", toRemove);
                            } else {
                                object.addUnique("attendees", ParseUser.getCurrentUser().getUsername());
                            }
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        if (isMyEvent) {
                                            doToast("Successfully canceled RSVP!");
                                        } else {
                                            doToast("Successfully RSVPed!");
                                        }
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                }
                            });
                    }
                }
            });
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // display map when clicking on address
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:?q="
                        + event.get("latitude") + "," + event.get("longitude")
                        + "(" + event.get("restaurant") + ")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_show_events) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_account) {
            Intent intent = new Intent(this, Account.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void doToast(String text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }
}
