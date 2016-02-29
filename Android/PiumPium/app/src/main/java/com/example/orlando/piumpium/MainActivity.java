package com.example.orlando.piumpium;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.orlando.arduino.Arduino;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    TextView xVal;
    TextView yVal;
    TextView zVal;
    TextView xASCIIVal;
    TextView yASCIIVal;
    TextView zASCIIVal;
    Button btnConectar;
    char xASCII;
    char yASCII;
    char zASCII;
    Arduino arduino;
    private long lastUpdate = 0;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private int last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        arduino = new Arduino(this);

        xVal = (TextView) findViewById(R.id.xLabel);
        yVal = (TextView) findViewById(R.id.yLabel);
        zVal = (TextView) findViewById(R.id.zLabel);
        xASCIIVal = (TextView) findViewById(R.id.xASCIILabel);
        yASCIIVal = (TextView) findViewById(R.id.yASCIILabel);
        zASCIIVal = (TextView) findViewById(R.id.zASCIILabel);
        btnConectar = (Button) findViewById(R.id.btnConnect);
        btnConectar.setOnClickListener(this);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {

                }

                last_x = (int)x + 10;
                last_y = (int)y + 10;
                last_z = (int)z + 10;

                xVal.setText(String.valueOf(last_x));
                yVal.setText(String.valueOf(last_y));
                zVal.setText(String.valueOf(last_z));

                xASCII = (char)(last_x + 65);
                yASCII = (char)(last_y + 65);
                zASCII = (char)(last_z + 65);

                xASCIIVal.setText(Character.toString(xASCII));
                yASCIIVal.setText(Character.toString(yASCII));
                zASCIIVal.setText(Character.toString(zASCII));
                
                arduino.write(Character.toString(xASCII));
                arduino.write(Character.toString(yASCII));
                arduino.write(Character.toString(zASCII));
            }
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Button btn = (Button)v;
        switch(btn.getId()){
            case R.id.btnConnect:
                arduino.connect();
                break;
        }
    }
}
