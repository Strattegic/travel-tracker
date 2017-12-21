package com.strattegic.travelapp.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.LocationResult;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.strattegic.travelapp.data.LocationContainer;
import com.strattegic.travelapp.data.LocationData;
import com.strattegic.travelapp.helpers.LocationTrackingHelper;
import com.strattegic.travelapp.helpers.LocationsFileHelper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Strattegic on 08/12/2017.
 * Is called every time the location was changed
 */

public class GPSBroadcastReceiver extends BroadcastReceiver {

    private final String ANDROID_LOCATION_RESULT_KEY = "com.google.android.gms.location.EXTRA_LOCATION_RESULT";

    @Override
    public void onReceive(Context context, Intent intent) {
        LocationResult result = (LocationResult) intent.getExtras().get(ANDROID_LOCATION_RESULT_KEY);
        if( result != null ){
            // TODO: log the location
            // Toast.makeText(context, result.toString(), Toast.LENGTH_SHORT).show();
            LocationData data = new LocationData();
            data.setLat( result.getLastLocation().getLatitude() );
            data.setLon( result.getLastLocation().getLongitude() );
            // LocationTrackingHelper.sendLocation(data);

            LocationsFileHelper fileHelper = new LocationsFileHelper(context);
            fileHelper.saveLocationOnDevice(data);
        }
        else {
            // TODO: log that there is no location available
            // Toast.makeText(context, "No location available", Toast.LENGTH_SHORT).show();
        }
    }
}
