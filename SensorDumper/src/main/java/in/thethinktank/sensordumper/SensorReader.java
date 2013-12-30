package in.thethinktank.sensordumper;

import java.io.PrintWriter;

/**
 * Created by anil on 26/12/13.
 */
public interface SensorReader {
    void dump(long millisecondsStartAt, long millisecondsRate);
    void setPrintWriter(PrintWriter printWriter);
    public boolean start();
    public void stop();
}
