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
        long last = prefs.getLong(TrackingDefines.TRACKING_PREFS_LAST_LOCATION_TIMESTAMP, 0);
        int interval = prefs.getInt(TrackingDefines.SETTINGS_TRACKING_INTERVAL, 0);
        long shouldRunTime = last + interval * LocationTrackingHelper.LOCATION_TRACKING_RATE;

        if( System.currentTimeMillis() > shouldRunTime ){
            return true;
        }
        return false;
    }
}
