package com.example.orlando.piumpium;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.example.orlando.arduino.Arduino;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener{

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
    private Button shootBtn;
    private Button btnConectar;
    private Button btnDesconectar;
    private TextView rollText;
    private TextView pitchText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        //Inicializacion y configuracion de variables
        arduino = new Arduino(this);
        layout = this.getWindow().findViewById(Window.ID_ANDROID_CONTENT);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        shootBtn = (Button)findViewById(R.id.shootBtn);
        shootBtn.setOnClickListener(this);
        btnConectar = (Button)findViewById(R.id.btnConectar);
        btnConectar.setOnClickListener(this);
        btnDesconectar = (Button)findViewById(R.id.btnDesconectar);
        btnDesconectar.setOnClickListener(this);

        rollText = (TextView)findViewById(R.id.rolltxt);
        pitchText = (TextView)findViewById(R.id.pitchtxt);

    }

    int map(int x, int in_min, int in_max, int out_min, int out_max) {
        try{
            return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
        }catch(ArithmeticException e){
            return 0;
        }
    }

    /**
     * Metodo que se manda a llamar cada vez que el acelerometro detecta movimiento
     * @param sensorEvent
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        //Configuracion y uso del API del acelerometro
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

                //se definen last_x, last_y y last_z como los valores finales del acelerometro
                last_x = x;
                last_y = y;
                last_z = z;

                //se limpian un poco los valores fx, fy, fz para mayos estabilidad
                fx = last_x * ALPHA + (fx * (1.0 - ALPHA));
                fy = last_y * ALPHA + (fy * (1.0 - ALPHA));
                fz = last_z * ALPHA + (fz * (1.0 - ALPHA));

                // Se calculan los angulos roll y pitch a partir de los valores del acelerometro
                roll  = (Math.atan2(-fy, fz)*180.0)/Math.PI;
                pitch = (Math.atan2(fx, Math.sqrt(fy * fy + fz * fz))*180.0)/Math.PI;

                //Se mapean los angulos de 0 a 180 para poder usar en los servomotores
                roll = map((int)roll, -180, 180, 180, 0);
                pitch = map((int)pitch, -180, 180, 0, 180);

                // Se muestra en tiempo real el valor de los angulos en la aplicacion
                rollText.setText(String.valueOf(roll));
                pitchText.setText(String.valueOf(pitch));

                //Los valores en los que el acelerometro es estable son entre 60 y 120
                //asi que se toman estos valores para decidir si mandar los datos al arduino o no
                if (roll >= 60 && roll <= 120 && pitch >= 60 && pitch <= 120){
                    arduino.write(Character.valueOf((char)roll).toString() + Character.valueOf((char)pitch).toString());
                }
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
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btnConectar:
                arduino.connect();//Boton para conectar el arduino
                break;
            case R.id.btnDesconectar:
                arduino.disconnect();//Bptpn para desconectar el arduino
                break;
            case R.id.shootBtn:
                arduino.write(String.valueOf((char)183)); //Si se presiiona el disparo se manda el ascii del valor 183 a arduino
                break;
        }
    }
}
