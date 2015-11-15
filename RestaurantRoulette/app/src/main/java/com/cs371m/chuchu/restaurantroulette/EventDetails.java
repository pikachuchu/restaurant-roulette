package com.cs371m.chuchu.restaurantroulette;

import android.content.Intent;
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

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.List;

public class EventDetails extends AppCompatActivity {

    private Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        TextView restaurant = (TextView) findViewById(R.id.restaurant);
        TextView host = (TextView) findViewById(R.id.host);
        TextView date = (TextView) findViewById(R.id.date);
        TextView time = (TextView) findViewById(R.id.time);
        TextView attendees = (TextView) findViewById(R.id.attendees);
        TextView maxPeople = (TextView) findViewById(R.id.maxPeople);
        TextView price = (TextView) findViewById(R.id.price);

        Intent intent = getIntent();
        HashMap<String, String> event = (HashMap<String, String>) intent.getSerializableExtra("event");
        restaurant.append(event.get("restaurant"));
        host.append(event.get("host"));
        date.append(event.get("date"));
        time.append(event.get("time"));
        maxPeople.append(event.get("number"));
        price.append(event.get("price"));


        attendees.append(event.get("attendees"));

        Button rsvpButton = (Button) findViewById(R.id.rsvpButton);
        Button cancelButton = (Button) findViewById(R.id.cancelButton);

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
                            object.addUnique("attendees", ParseUser.getCurrentUser().getUsername());
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    doToast("Successfully RSVPed!");
                                    finish();
                                }
                            });
                        }
                    }
                });
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
