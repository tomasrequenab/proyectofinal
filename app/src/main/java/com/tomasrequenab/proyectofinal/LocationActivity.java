package com.tomasrequenab.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {

    private LocationTracker tracker;
    private TextView txtLatitude, txtLongitude, txtLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        txtLatitude = findViewById(R.id.txtLatitud);
        txtLongitude = findViewById(R.id.txtLongitud);
        txtLocation = findViewById(R.id.txtLocation);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getLocation(View view){
        tracker = new LocationTracker(LocationActivity.this);
        if (tracker.canGetLocation()) {
            double latitude = tracker.getLatitude();
            double longitude = tracker.getLongitude();
            txtLatitude.setText(String.valueOf(latitude));
            txtLongitude.setText(String.valueOf(longitude));
            txtLocation.setText(coderLocation(latitude, longitude));
        } else {
            tracker.showSettingsAlert();
        }
    }

    public String coderLocation(Double latitude, Double longitude){
        Geocoder geocoder;
        List<Address> addresses;

        String address = null;
        String country = null;
        String state = null;
        String substate = null;
        String city = null;
        String postalCode = null;
        String thr = null;
        String subthr = null;
        String knonName = null;
        String location = null;

        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            address = addresses.get(0).getAddressLine(0);
            country = addresses.get(0).getCountryName();
            state = addresses.get(0).getAdminArea();
            substate = addresses.get(0).getSubAdminArea();
            city = addresses.get(0).getLocality();
            thr = addresses.get(0).getThoroughfare();
            subthr = addresses.get(0).getSubThoroughfare();
            knonName = addresses.get(0).getFeatureName();
            location = country + state + city + thr + subthr + knonName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return location;
    }
}