package in.thethinktank.sensordumper;

import android.hardware.Sensor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by anil on 26/12/13.
 */
public class LocationReader implements SensorReader, LocationListener {
    LocationManager mLocationManager;
    Sensor mSensor ;
    boolean mFirst = true ;
    float la, lo, accuracy ;
    Timer mTimer;
    PrintWriter mPrintWriter;

    LocationReader(LocationManager locationManager, File dumpFile) throws RuntimeException {
        mLocationManager = locationManager;
        try {
            mPrintWriter = new PrintWriter(new FileOutputStream(dumpFile, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    LocationReader(LocationManager locationManager, PrintWriter printWriter) throws RuntimeException {
        mLocationManager = locationManager;
        mPrintWriter = printWriter;
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
                synchronized (LocationReader.this) {
                    LocationReader.this.dump();
                }
            }
        }, millisecondsStartAt, millisecondsRate);
    }

    @Override
    public boolean start() {
        try{
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    private void dump(){
        if(mPrintWriter != null)
            mPrintWriter.println(System.currentTimeMillis() + "," + la + "," + lo + ", " + accuracy);
    }

    @Override
    public void stop() {
        mLocationManager.removeUpdates(this);
        mTimer.cancel();
        mPrintWriter.close();
    }

    @Override
    public void onLocationChanged(Location location) {
        la = (float)location.getLatitude();
        lo = (float)location.getLongitude();
        accuracy = location.getAccuracy();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
