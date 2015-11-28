package com.cs371m.chuchu.restaurantroulette;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
//import com.google.gson.Gson;
import com.google.android.gms.maps.model.LatLngBounds;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CreateEvent extends AppCompatActivity {

    Toast toast;
    int PLACE_PICKER_REQUEST = 1;

    private EditText restaurant;
    private EditText date;
    private EditText time;
    private EditText number;
    private Place place;
    private Date eventDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        restaurant = (EditText) findViewById(R.id.restaurant);
        date = (EditText) findViewById(R.id.date);
        time = (EditText) findViewById(R.id.time);
        number = (EditText) findViewById(R.id.numPeople);

        Button createEventButton = (Button) findViewById(R.id.createEventButton);

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getFragmentManager(), "timePicker");
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getFragmentManager(), "datePicker");
            }
        });

        restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                    // set location to GDC
                    builder.setLatLngBounds(new LatLngBounds.Builder().include(new LatLng(30.286373,-97.736638)).build());
                    startActivityForResult(builder.build(CreateEvent.this), PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException e) {
                    // ...
                    Log.d("CreateEvent", e.getMessage());
                } catch (GooglePlayServicesNotAvailableException e) {
                    // ...
                    Log.d("CreateEvent", e.getMessage());
                }
            }
        });

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: make sure all fields are present
                String validator = validateFields();
                if (!validator.equals("valid")) {
                    doToast(validator);
                    return;
                }
                Log.d("CreateEvent", validator);
                ArrayList<String> attendees = new ArrayList<>();
                String currUser = ParseUser.getCurrentUser().getUsername();
                attendees.add(currUser);
                ParseObject newEvent = new ParseObject("Event");
                newEvent.put("host", currUser);
                newEvent.put("restaurant", place.getName());
                newEvent.put("place_id", place.getId());
                newEvent.put("price", place.getPriceLevel());
                newEvent.put("location", new ParseGeoPoint(place.getLatLng().latitude, place.getLatLng().longitude));
                newEvent.put("address", place.getAddress());
                newEvent.put("datetime", eventDate);
                newEvent.put("max_attendees", Integer.parseInt(number.getText().toString().trim()));
                newEvent.put("attendees", attendees);

                newEvent.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            doToast(e.getMessage());
                        } else {
                            doToast("Event successfully created!");
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                });
            }
        });

    }

    private String validateFields() {
        StringBuilder result = new StringBuilder("Please ");
        boolean error = false;
        if (place == null) {
            result.append("pick a restaurant");
            error = true;
        }
        if (date.getText().toString().equals("")) {
            if (error) {
                result.append(", and ");
            }
            result.append("enter a date");
            error = true;
        }
        if (time.getText().toString().equals("")) {
            if (error) {
                result.append(", and ");
            }
            result.append("enter a time");
            error = true;
        }
        if (number.getText().toString().equals("")) {
            if (error) {
                result.append(", and ");
            }
            result.append("enter max attendees");
            error = true;
        } else {
            // formula to calculate world population based on date; from http://galen.metapath.org/popclk.html
            long baseTime = new Date(100, 6, 1).getTime();
            long currTime = new Date().getTime();
            long adjtime = currTime - baseTime;
            long pop = (long) Math.exp(22.528293835777973 + 4.0275867940663167699e-13*adjtime
                    - 1.1020778551110890530e-25*adjtime*adjtime);
            try {
                int num = Integer.parseInt(number.getText().toString());
                if (num > pop) {
                    if (error) {
                        result.append(", and ");
                    }
                    result.append("enter max attendees less than world population of " + pop);
                    error = true;
                }
            } catch(NumberFormatException e) {
                if (error) {
                    result.append(", and ");
                }
                result.append("enter max attendees less than world population of " + pop);
                error = true;
            }
        }
        if (!date.getText().toString().equals("") && !time.getText().toString().equals("")) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
                eventDate = dateFormat.parse(date.getText().toString() + " " + time.getText().toString());
                if (eventDate.before(new Date())) {
                    if (error) {
                        result.append(", and ");
                    }
                    result.append("enter date in the future");
                    error = true;
                }
            } catch (java.text.ParseException e) {
                Log.d("CreateEvent", e.getMessage());
            }
        }
        return error ? result.toString() : "valid";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_event, menu);
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
        } else if (id == R.id.action_rate) {
            Intent intent = new Intent(this, PastEvents.class);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            place = PlacePicker.getPlace(data, this);
            restaurant.setText(place.getName());
        }
    }

    // Date and Time Pickers

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            EditText timeText = (EditText) getActivity().findViewById(R.id.time);
            String timeStr = String.format("%02d:%02d", hourOfDay, minute);
            timeText.setText(timeStr);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            EditText dateText = (EditText) getActivity().findViewById(R.id.date);
            String dateStr = String.format("%02d/%02d/%04d", month + 1, day, year);
            dateText.setText(dateStr);
        }
    }
}
