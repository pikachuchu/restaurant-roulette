package com.cs371m.chuchu.restaurantroulette;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by chuchu on 11/16/15.
 */
public class LocationHelper {
    public static LatLng getCurrentLocation(Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        // Getting the name of the best provider
        String provider = LocationManager.NETWORK_PROVIDER;

        Location location = null;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        } else {
            Log.d("Location", "correct permissions");
            location = locationManager.getLastKnownLocation(provider);
        }

        double latitude = 30.286373;
        double longitude = -97.736638;
        if (location != null) {
            System.out.println("found a location");
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d("Location", latitude + "," + longitude);
        }
        return new LatLng(latitude, longitude);
    }
}
