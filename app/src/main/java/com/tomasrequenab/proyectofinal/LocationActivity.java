package com.tomasrequenab.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
    private SensorEventListener sensorEventListener;
    private int clic = 0;

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

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if(sensor == null) {
            finish();
        }

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float x = sensorEvent.values[0];

                if (x <= 5 && clic == 0) { //giro derecha
                    clic++;
                    getLocation();
                } else if(x > 5 && clic == 1) { //giro derecha-izquierda
                    clic++;
                    getLocation();
                }
                if(clic == 2){ //derecha, reproduce sonido
                    getLocation();
                    clic=0;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        start();
    }

    public void getLocation(){
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

    public String coderLocation(Double latitude, Double longitude) {
        Geocoder geocoder;
        List<Address> addresses;

        String country = null;
        String state = null;
        String city = null;
        String knonName = null;
        String location = null;

        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            country = addresses.get(0).getCountryName();
            state = addresses.get(0).getAdminArea();
            city = addresses.get(0).getLocality();
            knonName = addresses.get(0).getFeatureName();
            location = country + state + city + knonName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return location;
    }

    private void sonido() {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.clic);
        mediaPlayer.start();
    }

    private void start() {
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stop() {
        sensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    protected void onPause() {
        stop();
        super.onPause();
    }

    @Override
    protected void onResume() {
        start();
        super.onResume();
    }
}