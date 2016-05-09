package com.example.orlando.piumpium;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.gesture.GestureOverlayView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.orlando.arduino.Arduino;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    TextView xVal;
    TextView yVal;
    TextView zVal;
    TextView xASCIIVal;
    TextView yASCIIVal;
    TextView panelX;
    TextView panelY;
    Button btnConectar;
    Button btnDesonectar;
    Button onBtn;
    Button shootBtn;
    GestureOverlayView panel;
    char xASCII;
    char yASCII;
    Arduino arduino;
    View layout;
    private long lastUpdate = 0;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private float last_x, last_y, last_z;
    double fx = 0;
    double fy = 0;
    double fz = 0;
    double roll, pitch;
    private static final int SHAKE_THRESHOLD = 600;
    private static final float ALPHA = 0.5f;
    boolean writeAccel = true;
    boolean on = false;
    boolean disparar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        arduino = new Arduino(this);
        layout = this.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        xVal = (TextView) findViewById(R.id.xLabel);
        yVal = (TextView) findViewById(R.id.yLabel);
        zVal = (TextView) findViewById(R.id.zLabel);
        panelX = (TextView) findViewById(R.id.panelX);
        panelY = (TextView) findViewById(R.id.panelY);
        panel = (GestureOverlayView) findViewById(R.id.panel);
        panel.addOnGestureListener(new GestureOverlayView.OnGestureListener() {
            @Override
            public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
                Toast.makeText(MainActivity.this, "started", Toast.LENGTH_SHORT).show();
                writeAccel = false;
            }
            @Override
            public void onGesture(GestureOverlayView overlay, MotionEvent event) {
                int x;
                int y;
                x = (int)event.getX();
                x = constrain(x, 0, overlay.getWidth());
                x = map(x, 0, overlay.getWidth(), 0, 180);
                y = (int) event.getY();
                y = constrain(y, 0, overlay.getHeight());
                y = map(y, 0, overlay.getHeight(), 0, 180);

                if (x != -1 && y != -1 && x != 180 && y != 180){
                    panelX.setText(String.valueOf(x));
                    panelY.setText(String.valueOf(y));
                    arduino.write(Character.toString((char) (x)));
                    arduino.write(Character.toString((char)(y)));
                }
            }

            @Override
            public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
                Toast.makeText(MainActivity.this, "ended", Toast.LENGTH_SHORT).show();
                writeAccel = true;
            }

            @Override
            public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
                Toast.makeText(MainActivity.this, "cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        xASCIIVal = (TextView) findViewById(R.id.xASCIILabel);
        yASCIIVal = (TextView) findViewById(R.id.yASCIILabel);
        btnConectar = (Button) findViewById(R.id.btnConnect);
        btnConectar.setOnClickListener(this);
        btnDesonectar = (Button) findViewById(R.id.btnDesconectar);
        btnDesonectar.setOnClickListener(this);
        shootBtn = (Button) findViewById(R.id.shootBtn);
        shootBtn.setOnClickListener(this);
        onBtn = (Button) findViewById(R.id.onBtn);
        onBtn.setOnClickListener(this);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    int map(int x, int in_min, int in_max, int out_min, int out_max)
    {
        try{
            return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
        }catch(ArithmeticException e){
            return 0;
        }
    }
    int constrain(int x, int min, int max){
        if(x < min){
            x = min;
        }else if(x > max){
            x = max;
        }
        return x;
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

                last_x = x;
                last_y = y;
                last_z = z;

                fx = last_x * ALPHA + (fx * (1.0 - ALPHA));
                fy = last_y * ALPHA + (fy * (1.0 - ALPHA));
                fz = last_z * ALPHA + (fz * (1.0 - ALPHA));

                roll  = (Math.atan2(-fy, fz)*180.0)/Math.PI;
                pitch = (Math.atan2(fx, Math.sqrt(fy * fy + fz * fz))*180.0)/Math.PI;

                xVal.setText(String.format("%.2f", fx));
                yVal.setText(String.format("%.2f", fy));
                zVal.setText(String.format("%.2f", fz));

                roll = map((int)roll, -180, 180, 180, 0);
                pitch = map((int)pitch, -180, 180, 0, 180);

                disparar = false;

                if (roll >= 60 && roll <= 120 && pitch >= 60 && pitch <= 120 && !disparar){
                    arduino.write(Character.valueOf((char)roll).toString() + Character.valueOf((char)pitch).toString());
                }


                xASCII = (char)(int)(roll);
                yASCII = (char)(int)(pitch);

                xASCIIVal.setText(String.valueOf(roll));
                yASCIIVal.setText(String.valueOf(pitch));
            }
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
    public void onClick(View v) {
        Button btn = (Button)v;
        switch(btn.getId()){
            case R.id.btnConnect:
                arduino.connect();
                break;
            case R.id.btnDesconectar:
                arduino.disconnect();
                break;
            case R.id.onBtn:
                if(on){
                    arduino.write(Character.toString((char)200));
                    on = false;
                }else{
                    arduino.write(Character.toString((char)250));
                    on = true;
                }
                break;
            case R.id.shootBtn:
                disparar = true;
                arduino.write(Character.toString((char)300));
                break;
        }
    }
}
