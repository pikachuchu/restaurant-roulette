package com.cs371m.chuchu.restaurantroulette;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class CreateEvent extends AppCompatActivity {

    Toast toast;

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


}
