package in.thethinktank.sensordumper;

import android.app.Service;
import android.content.Intent;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anil on 26/12/13.
 */
public class SensorDumperService extends Service {
    List<SensorReader> readers ;
    SensorManager mSensorManager ;
    LocationManager mLocationManager;
    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        readers = new ArrayList<SensorReader>();
        File dir = new File(Environment.getExternalStorageDirectory()+"/"+"acceldump/");
        dir.mkdirs();
        long timestamp = System.currentTimeMillis();
        readers.add(new AccelerometerReader(mSensorManager, new File(dir, timestamp + "-acceldump.txt")));
        readers.add(new GyroscopeReader(mSensorManager, new File(dir, timestamp + "-gyrodump.txt")));
        readers.add(new RotationReader(mSensorManager, new File(dir, timestamp + "-rotdump.txt")));
        readers.add(new LocationReader(mLocationManager, new File(dir, timestamp + "-gpsdump.txt")));
        readers.add(new GravityReader(mSensorManager, new File(dir, timestamp + "-gravdump.txt")));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(getClass().getName(),"Logging Sensors ...");
        long startAt = 100;
        for(SensorReader reader : readers){
            Log.i(getClass().getName(),"Starting: " + reader.getClass().getName());
            reader.start();
            startAt += 100;
            reader.dump(startAt, 100);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        for(SensorReader reader : readers){
            Log.i(getClass().getName(),"Stopping: " + reader.getClass().getName());
            reader.stop();
        }
        Log.i(getClass().getName(),"Stopping Sensor Log");
        super.onDestroy();
    }
}
