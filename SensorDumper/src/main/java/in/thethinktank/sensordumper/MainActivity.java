package in.thethinktank.sensordumper;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {
    boolean mFlag = true;
    Intent mIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button)findViewById(R.id.start_stop_dump_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v ;
                if(mFlag){
                    b.setBackgroundColor(Color.RED);
                    b.setText(R.string.stop_dump_button_text);
                    mFlag = false ;
                    mIntent = new Intent(MainActivity.this, SensorDumperService.class);
                    startService(mIntent);
                } else {
                    b.setBackgroundColor(Color.GREEN);
                    b.setText(R.string.start_dump_button_text);
                    mFlag = true ;
                    if(mIntent != null)
                        stopService(mIntent);
                }
            }
        });
        button = (Button)findViewById(R.id.start_stop_wifi_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v ;
                if(mFlag){
                    b.setBackgroundColor(Color.RED);
                    b.setText(R.string.stop_stream_button_text);
                    mFlag = false ;
                    mIntent = new Intent(MainActivity.this, WifiStreamerService.class);
                    startService(mIntent);
                } else {
                    b.setBackgroundColor(Color.GREEN);
                    b.setText(R.string.start_stream_button_text);
                    mFlag = true ;
                    if(mIntent != null)
                        stopService(mIntent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    @Override
    protected void onDestroy() {
        if(mIntent != null)
            stopService(mIntent);
        super.onDestroy();
    }
}
