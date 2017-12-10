package com.strattegic.travelapp.helpers;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.strattegic.travelapp.common.GPSBroadcastReceiver;

/**
 * Created by Stratti on 08/12/2017.
 */

public class LocationTrackingHelper {

    public static final String PREFERENCES__SEND_LOCATION_DATA = "SEND_LOCATION_DATA";
    public static final String DATA__LAST_DATA_SEND = "LAST_DATA_SEND";
    private static AlarmManager am;
    private static LocationRequest locationRequest;
    private static LocationCallback mLocationCallback;

    public static void toggleGPSTracking( boolean enabled, Context context ){

        if( am == null ){
            am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }

        Intent intent = new Intent( context, GPSBroadcastReceiver.class );

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 5 * 1000;//start 5 seconds after first register.

        if( enabled ){
            // Schedule the alarm!
            // TODO: Interval should be set by the user
            am.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, (9*1000), pendingIntent);
        }
        else{
            am.cancel(pendingIntent);
        }

    }
}
