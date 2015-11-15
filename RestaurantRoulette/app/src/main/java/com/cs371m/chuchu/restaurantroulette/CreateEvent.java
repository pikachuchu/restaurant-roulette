package com.cs371m.chuchu.restaurantroulette;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;


import org.json.JSONObject;

public class CreateEvent extends AppCompatActivity {

    Toast toast;
    int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        final EditText restaurant = (EditText) findViewById(R.id.restaurant);
        final EditText date = (EditText) findViewById(R.id.date);
        final EditText time = (EditText) findViewById(R.id.time);
        final EditText number = (EditText) findViewById(R.id.numPeople);
        final EditText price = (EditText) findViewById(R.id.price);

        Button createEventButton = (Button) findViewById(R.id.createEventButton);
        Button pickRestaurantButton = (Button) findViewById(R.id.PickRestaurantButton);

        pickRestaurantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

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

                ParseObject newEvent = new ParseObject("Event");
                newEvent.put("restaurant", restaurant.getText().toString().trim());
                newEvent.put("date", date.getText().toString().trim());
                newEvent.put("time", time.getText().toString().trim());
                newEvent.put("number", number.getText().toString().trim());
                newEvent.put("price", price.getText().toString().trim());

                newEvent.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                        doToast(e.getMessage());
                        } else {
                            doToast("Event successfully created!");
                        }
                    }
                });
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                Gson gson = new Gson();
                String json = gson.toJson(place);
//                Place result = gson.fromJson(json, Place.class);


                String restaurantName = (String) place.getName();
                LatLng restaurantLocation = place.getLatLng();

                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }


}
