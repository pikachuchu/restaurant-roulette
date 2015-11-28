package com.cs371m.chuchu.restaurantroulette;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by chuchu on 11/26/15.
 */
public class RateAttendees extends AppCompatActivity implements RateDialog.RateAttendeeListener {
    HashMap<String, Object> event;
    ArrayList<String> attendees;
    private Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_attendees);
        Intent callingIntent = getIntent();
        event = (HashMap<String, Object>) callingIntent.getSerializableExtra("event");
        TextView restaurant = (TextView) findViewById(R.id.restaurant);
        TextView host = (TextView) findViewById(R.id.host);
        TextView datetime = (TextView) findViewById(R.id.datetime);

        restaurant.append((String) event.get("restaurant"));
        host.append((String) event.get("host"));
        datetime.append((String) event.get("datetime"));

        attendees = (ArrayList<String>) event.get("attendees");
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, attendees));

        Button rateButton = (Button) findViewById(R.id.rateButton);
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                String attendee = spinner.getSelectedItem().toString();
                args.putString("attendee", attendee);
                RateDialog rateDialog = new RateDialog();
                rateDialog.setArguments(args);
                rateDialog.show(getFragmentManager(), "rate");
            }
        });

        Button finishButton = (Button) findViewById(R.id.finishButton);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("event", event);
                setResult(RESULT_OK, intent);
                finish();
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
                startActivityForResult(intent, 1);
            } else {
                Intent intent = new Intent(this, Account.class);
                startActivityForResult(intent, 1);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rate_attendees, menu);
        return true;
    }

    @Override
    public void onRate(final String attendee, final float rating, final String reliability) {
        Log.d("RateAttendees", attendee + " " + rating + " " + reliability);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Rating");
        query.whereEqualTo("username", attendee);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null) {
                    Log.d("RateAttendees", e.getMessage());
                    return;
                }
                if (objects.size() == 0) {
                    Log.d("RateAttendees", "no objects found");
                    return;
                }
                ParseObject obj = objects.get(0);
                float currRating = Float.parseFloat(String.valueOf(obj.get("rating")));
                int numRatings = (int) obj.get("numRatings");
                float newRating = (rating + currRating * numRatings) / (numRatings + 1);

                int currReliability = (int) obj.get(reliability);
                obj.put("rating", newRating);
                obj.put(reliability, currReliability + 1);
                obj.put("numRatings", numRatings + 1);
                obj.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d("RateAttendees", e.getMessage());
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Rating saved!", Toast.LENGTH_SHORT).show();
                        ((ArrayAdapter<String>) spinner.getAdapter()).remove(attendee);
                        ((ArrayAdapter<String>)spinner.getAdapter()).notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
