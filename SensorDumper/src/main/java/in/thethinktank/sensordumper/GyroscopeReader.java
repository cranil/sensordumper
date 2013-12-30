package in.thethinktank.sensordumper;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by anil on 26/12/13.
 */
public class GyroscopeReader implements SensorReader, SensorEventListener {
    SensorManager mSensorManager ;
    Sensor mSensor ;
    boolean mFirst = true ;
    float axp, ayp, azp, ax, ay, az ;
    Timer mTimer;
    PrintWriter mPrintWriter;

    GyroscopeReader(SensorManager sensorManager, File dumpFile) throws RuntimeException {
        mSensorManager = sensorManager;
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        try {
            mPrintWriter = new PrintWriter(new FileOutputStream(dumpFile, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (mSensor == null){
            throw new RuntimeException("No accelerometer found :-(");
        }
    }

    GyroscopeReader(SensorManager sensorManager, PrintWriter printWriter) throws RuntimeException {
        mSensorManager = sensorManager;
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mPrintWriter = printWriter;
        if (mSensor == null){
            throw new RuntimeException("No accelerometer found :-(");
        }
    }
    @Override
    public void setPrintWriter(PrintWriter printWriter) {
        mPrintWriter = printWriter ;
    }
    @Override
    public void dump(long millisecondsStartAt, long millisecondsRate) {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                synchronized (GyroscopeReader.this) {
                    GyroscopeReader.this.dump();
                }
            }
        }, millisecondsStartAt, millisecondsRate);
    }

    @Override
    public boolean start() {
        try{
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    private void dump(){
        String dmp = String.format("%d,%f,%f,%f",System.currentTimeMillis(),axp, ayp, azp);
        mPrintWriter.println(dmp);
        // Log.i(AccelerometerReader.class.getName(),dmp);
    }

    @Override
    public void stop() {
        mSensorManager.unregisterListener(this);
        mTimer.cancel();
        mPrintWriter.close();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float alpha = 0.8f ;

        ax = event.values[0] ;
        ay = event.values[1] ;
        az = event.values[2] ;

        axp = ax ;
        ayp = ay ;
        azp = az ;

/*        if(mFirst){
            axp = ax ;
            axp = ay ;
            axp = az ;
            mFirst = false;
        } else {
            axp = alpha*ax + (1-alpha)*axp ;
            axp = alpha*ay + (1-alpha)*ayp ;
            axp = alpha*az + (1-alpha)*azp ;
        }*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}