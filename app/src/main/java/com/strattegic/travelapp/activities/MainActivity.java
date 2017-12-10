package com.strattegic.travelapp.activities;

import android.app.AlarmManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.strattegic.travelapp.R;

public class MainActivity extends AppCompatActivity {
    private static AlarmManager alarmManager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_tracker:
                    Intent trackerIntent = new Intent( getBaseContext(), TrackerActivity.class );
                    startActivity( trackerIntent );
                    overridePendingTransition(0, 0);
                    break;
                case R.id.navigation_home:
                    Intent homeIntent = new Intent( getBaseContext(), HomeActivity.class );
                    startActivity( homeIntent );
                    overridePendingTransition(0, 0);
                    break;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    /*
    public static void toggleGPSTracking( boolean enabled, Context context ){

        if( alarmManager == null ){
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }

        Intent intent = new Intent( context, GPSBroadcastReceiver.class );

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // We want the alarm to go off 5 seconds from now.
        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 5 * 1000;//start 5 seconds after first register.

        if( enabled ){
            // Schedule the alarm!
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, 60000, pendingIntent);
        }
        else{
            alarmManager.cancel(pendingIntent);
        }
    }
    */
}
