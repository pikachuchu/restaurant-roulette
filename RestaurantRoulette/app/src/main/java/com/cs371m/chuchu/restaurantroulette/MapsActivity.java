package com.cs371m.chuchu.restaurantroulette;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    // TODO: make into fragment that replaces listView in MainActivity

    private GoogleMap mMap;
    public LatLng myPosition;
    private ArrayList<HashMap<String, String>> myEventsList;
    private ArrayList<HashMap<String, String>> nearbyEventsList;
    private boolean nearbyEventsDisplayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent callingIntent = getIntent();
        myEventsList = (ArrayList<HashMap<String, String>>) callingIntent.getSerializableExtra("myEvents");
        nearbyEventsList = (ArrayList<HashMap<String, String>>) callingIntent.getSerializableExtra("nearbyEvents");
        nearbyEventsDisplayed = callingIntent.getBooleanExtra("nearbyEventsDisplayed", true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_toggle_view) {
            finish();
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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        LatLng location = LocationHelper.getCurrentLocation(this);
        myPosition = new LatLng(location.latitude, location.longitude);
        mMap.addCircle(new CircleOptions().center(myPosition));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(14));

        mMap.setOnInfoWindowClickListener(this);

        if (nearbyEventsDisplayed) {
            setEventMarkers(nearbyEventsList);
        } else {
            setEventMarkers(myEventsList);
        }
    }

    private void setEventMarkers(ArrayList<HashMap<String, String>> events) {
        for (int i = 0; i < events.size(); i++) {
            HashMap<String, String> event = events.get(i);
            double latitude = Double.parseDouble(event.get("latitude"));
            double longitude = Double.parseDouble(event.get("longitude"));
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(i + ": " + event.get("restaurant"))
                    .snippet(event.get("datetime")));
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d("MapsActivity", "onInfoWindowClick");
        int index = Integer.parseInt(marker.getTitle().split(":")[0]);
        Intent intent = new Intent(this, EventDetails.class);
        intent.putExtra("isMyEvent", !nearbyEventsDisplayed);
        if (nearbyEventsDisplayed) {
            intent.putExtra("event", nearbyEventsList.get(index));
        } else {
            intent.putExtra("event", myEventsList.get(index));
        }
        startActivity(intent);
    }
}
