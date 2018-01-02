package com.strattegic.travelapp.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.android.gms.location.LocationResult;
import com.strattegic.travelapp.data.LocationData;
import com.strattegic.travelapp.helpers.LocationTrackingHelper;
import com.strattegic.travelapp.helpers.LocationsFileHelper;

import java.util.Date;

/**
 * Created by Strattegic on 08/12/2017.
 * Is called every time the location was changed
 */

public class GPSBroadcastReceiver extends BroadcastReceiver {

    private final String ANDROID_LOCATION_RESULT_KEY = "com.google.android.gms.location.EXTRA_LOCATION_RESULT";

    @Override
    public void onReceive(Context context, Intent intent) {
        LocationResult result = (LocationResult) intent.getExtras().get(ANDROID_LOCATION_RESULT_KEY);
        if( result != null && shouldUpdate(context) ){

            // TODO: log the location
            // Toast.makeText(context, result.toString(), Toast.LENGTH_SHORT).show();
            LocationData data = new LocationData();
            data.setLat( result.getLastLocation().getLatitude() );
            data.setLon( result.getLastLocation().getLongitude() );
            data.setAddedOn(new Date());
            // LocationTrackingHelper.sendLocation(data);

            LocationsFileHelper fileHelper = new LocationsFileHelper(context);
            fileHelper.saveLocationOnDevice(data);
        }
        else {
            // TODO: log that there is no location available
            // Toast.makeText(context, "No location available", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean shouldUpdate(Context context){
        SharedPreferences prefs = context.getSharedPreferences(TrackingDefines.TRACKING_PREFS_NAME, Context.MODE_PRIVATE);

        if( prefs.getBoolean(TrackingDefines.SETTINGS_TRACKING_ENABLED, false ) ) {
            long last = prefs.getLong(TrackingDefines.TRACKING_PREFS_LAST_LOCATION_TIMESTAMP, 0);
            int interval = prefs.getInt(TrackingDefines.SETTINGS_TRACKING_INTERVAL, 0);

            // because the interval is 0-based, we have to add +1 to make the minimum interval work
            // otherwise it would always update the location as soon as the tracker view
            // is refreshed because last=shouldRunTime
            long shouldRunTime = last + (interval+1) * LocationTrackingHelper.LOCATION_TRACKING_RATE;

            if (System.currentTimeMillis() > shouldRunTime) {
                return true;
            }
        }
        return false;
    }
}
