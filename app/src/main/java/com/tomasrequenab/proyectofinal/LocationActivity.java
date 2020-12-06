package com.tomasrequenab.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {

    private LocationTracker tracker;
    private TextView txtLatitude, txtLongitude, txtLocation;
    private SensorManager sensorManager;
    private Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        tracker = new LocationTracker(LocationActivity.this);

        txtLatitude = findViewById(R.id.txtLatitud);
        txtLongitude = findViewById(R.id.txtLongitud);
        txtLocation = findViewById(R.id.txtLocation);

        // Request location permissions
        try {
            int permissionResult = ContextCompat.checkSelfPermission(
                    getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            );
            if (permissionResult != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        101
                );
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        if(sensor == null) {
            finish();
        }

        // Logout button click listener
        findViewById(R.id.logout_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
                loadRegister();
            }
        });
    }

    @Override
    protected void onPause() {
        stopListenSensor();
        super.onPause();
    }

    @Override
    protected void onResume() {
        startListenSensor();
        super.onResume();
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float z = sensorEvent.values[2];

            if (z > 8)
                getLocation();
            else
                clearLocation();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {}
    };

    private void getLocation() {
        if (tracker.canGetLocation()) {
            double latitude = tracker.getLatitude();
            double longitude = tracker.getLongitude();
            txtLatitude.setText(String.valueOf(latitude));
            txtLongitude.setText(String.valueOf(longitude));
            txtLocation.setText(geocodeLocation(latitude, longitude));
        } else {
            tracker.showSettingsAlert();
        }
    }

    private void clearLocation() {
        txtLatitude.setText("Latitud");
        txtLongitude.setText("Longitud");
        txtLocation.setText("Dirección");
    }

    public String geocodeLocation(Double latitude, Double longitude) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String country = addresses.get(0).getCountryName();
            String state = addresses.get(0).getAdminArea();
            String city = addresses.get(0).getLocality();
            String knonName = addresses.get(0).getFeatureName();

            return country + ", " + state + ", " + city + ", " + knonName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void startListenSensor() {
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stopListenSensor() {
        sensorManager.unregisterListener(sensorEventListener);
    }

    private void logout() {
        getApplicationContext().getSharedPreferences("", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }

    private void loadRegister() {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}