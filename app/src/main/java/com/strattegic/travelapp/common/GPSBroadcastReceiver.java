package com.strattegic.travelapp.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;
import com.strattegic.travelapp.data.LocationData;
import com.strattegic.travelapp.helpers.LocationTrackingHelper;

/**
 * Created by Strattegic on 08/12/2017.
 * Is called every time the location was changed
 */

public class GPSBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LocationResult result = (LocationResult) intent.getExtras().get("com.google.android.gms.location.EXTRA_LOCATION_RESULT");
        if( result != null ){
            Log.d("GPS", result.toString()); //To Implement
            Toast.makeText(context, result.toString(), Toast.LENGTH_SHORT).show();
            LocationData data = new LocationData();
            data.setLat( result.getLastLocation().getLatitude() );
            data.setLat( result.getLastLocation().getLongitude() );
            LocationTrackingHelper.sendLocation(data);
        }
        else {
            Toast.makeText(context, "No location available", Toast.LENGTH_SHORT).show();
        }
    }
}
