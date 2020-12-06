package com.tomasrequenab.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;

public class LocationActivity extends AppCompatActivity {
    SensorManager sensorManager;
    Sensor sensor;
    SensorEventListener sensorEventListener;
    int clic=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(sensor==null)
            finish();
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float x = sensorEvent.values[0];
                System.out.println("valor giro "+x);
                if(x<=5 && clic == 0){ //giro derecha
                    clic++;
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE); //fondo azul
                }else if(x>5 && clic==1){ //giro derecha-izquierda
                    clic++;
                    getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                }
                if(clic==2){ //derecha, reproduce sonido
                    sonido();
                    clic=0;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        start();
    }
    private void sonido(){
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.clic);
        mediaPlayer.start();
    }

    private void start(){
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stop(){
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