package com.mum6ojumbo.accelerometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    SensorManager mSensorManager;//Manages the sensors on the device...
    Sensor mSensor;
    double xval,yval,zval;
    boolean orientationChanged;
    int displayToast=0,currDisp=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager =(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(mSensor == null)
            Toast.makeText(this,"No Accelerometer!1",Toast.LENGTH_SHORT).show();

    }

    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }
    protected void onPause(){
        super.onPause();
        if(mSensor!=null){
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.i("SensorEventChanged","values: X Axis"+sensorEvent.values[0]+" Y-axis="+sensorEvent.values[1]+" Z-axis="+sensorEvent.values[2]);
        if((sensorEvent.values[0]>=-5.0 && sensorEvent.values[0]<=5.0)&&(sensorEvent.values[1]>= 8.5 && sensorEvent.values[1]<=10.0)){
            //def orientation
            displayToast=0;


        }
        else if((sensorEvent.values[0]>=-10.0 && sensorEvent.values[0]<=-8.5)&&(sensorEvent.values[1]>= -4.0 && sensorEvent.values[1]<=-0.08)) {
            // rotated right
            displayToast=1;

        }
        else if((sensorEvent.values[0]>=8.0 && sensorEvent.values[0]<=10.5)&&(sensorEvent.values[1]>= 0.55 && sensorEvent.values[1]<=3.05)){
            //Rotated Left
            displayToast=2;

        }
        if(currDisp!=displayToast){
            currDisp=displayToast;
            switch(displayToast){
                case 0: Toast.makeText(this,"Default Orientation!!",Toast.LENGTH_SHORT).show();
                        orientationChanged=false;
                        break;
                case 1: Toast.makeText(this,"Rotated Right!!",Toast.LENGTH_SHORT).show();
                    orientationChanged=false;
                    break;
                case 2: Toast.makeText(this,"Rotated Left!!",Toast.LENGTH_SHORT).show();
                    orientationChanged=false;
                    break;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.i("SensorAccuracyChanged", "values="+sensor.getName());
        xval=0.0;yval=0.0;zval=0.0;

    }
}
