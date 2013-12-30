package in.thethinktank.sensordumper;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by anil on 29/12/13.
 */
public class WifiStreamerService extends Service {
    List<SensorReader> readers ;
    SensorManager mSensorManager ;
    LocationManager mLocationManager;
    BufferedWriter mBufferedWriter;
    ServerSocket mServerSocket;
    Socket mClientSocket;
    SensorEventWriter gravityEventWriter, accelerometerEventWriter, gyroscopeEventWriter;
    Sensor accelerometer, gyroscope, gravity;
    long mStartTime ;
    private class SensorEventWriter implements SensorEventListener{
        String mPrefix = null;
        BufferedWriter mBufferedWriter;
        long mPrevMilliseconds ;
        boolean mFirst = true ;
        @Override
        public void onSensorChanged(SensorEvent event) {
            String dmp ;
            long time = System.currentTimeMillis();
            if(mFirst || (time-mPrevMilliseconds)>90){
                if(mPrefix != null){
                    dmp = String.format("%s: %d,%s\n",mPrefix,
                            time-mStartTime, Arrays.toString(event.values));
                } else {
                    dmp = String.format("%d,%s",
                            time-mStartTime, Arrays.toString(event.values));
                }
                try {
                    mBufferedWriter.write(dmp);
                } catch (IOException e) {
                    Log.e(SensorEventWriter.class.getName(), e.toString());
                }
                mPrevMilliseconds = time ;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    @Override
    public void onCreate() {
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        readers = new ArrayList<SensorReader>();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mServerSocket = new ServerSocket(8888);
                        Log.i(WifiStreamerService.class.getName(),
                                "Listening on" + mServerSocket.getInetAddress().toString()+":8888");
                        // mServerSocket.setSoTimeout(1000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        mClientSocket = mServerSocket.accept();
                        mBufferedWriter = new BufferedWriter(
                                new OutputStreamWriter(mClientSocket.getOutputStream())
                        );
                        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                        gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                        gravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

                        accelerometerEventWriter = new SensorEventWriter();
                        accelerometerEventWriter.mPrefix = "Accelerometer" ;
                        accelerometerEventWriter.mBufferedWriter = mBufferedWriter;
                        gyroscopeEventWriter = new SensorEventWriter();
                        gyroscopeEventWriter.mPrefix = "Gyroscope" ;
                        gyroscopeEventWriter.mBufferedWriter = mBufferedWriter;
                        gravityEventWriter = new SensorEventWriter();
                        gravityEventWriter.mPrefix = "Gravity" ;
                        gravityEventWriter.mBufferedWriter = mBufferedWriter;
                        Log.i(WifiStreamerService.class.getName(), "Logging Sensors ...");
                        mStartTime = System.currentTimeMillis();
                        mSensorManager.registerListener(accelerometerEventWriter,
                                accelerometer, 100000);
                        mSensorManager.registerListener(gyroscopeEventWriter,
                                gyroscope, 100000);
                        mSensorManager.registerListener(gravityEventWriter,
                                gravity, 100000);
                    } catch (IOException e) {
                        Log.i(WifiStreamerService.class.getName(), "Accept failed");
                    }
                }
            });
        super.onCreate();
        thread.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(accelerometerEventWriter);
        mSensorManager.unregisterListener(gyroscopeEventWriter);
        mSensorManager.unregisterListener(gravityEventWriter);
        try {
            if(mServerSocket != null)
                mServerSocket.close();
            if(mClientSocket != null)
                mClientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(getClass().getName(),"Stopping Sensor Log");
        super.onDestroy();
    }
}
