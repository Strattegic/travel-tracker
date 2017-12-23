package com.strattegic.travelapp.helpers;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.strattegic.travelapp.R;
import com.strattegic.travelapp.common.GPSBroadcastReceiver;
import com.strattegic.travelapp.common.LoomisaWebservice;
import com.strattegic.travelapp.data.LocationData;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Stratti on 08/12/2017.
 */

public class LocationTrackingHelper {

    private static final int LOCATION_TRACKING_INTENT = 1337;

    /**
     * The rate with which the progress slider in the options is multiplied
     * the slider returns something like 0-14 and has to be converted to milliseconds
     * 1.8 million = 0.5h
     */
    public static final int LOCATION_TRACKING_RATE = 1800000;

    public static final String GSON_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    protected Gson gson;
    private FusedLocationProviderClient fusedLocationClient;

    public LocationTrackingHelper(){
        this.gson = new GsonBuilder().setDateFormat(GSON_DATE_FORMAT).create();
    }

    /**
     * Toggles the location tracking on/off
     * When activating the location tracker, it initializes the tracking
     * with the LOCATION_TRACKING_MINIMUM_INTERVAL
     * @param enabled
     * @param activity
     */
    public void toggleGPSTracking(boolean enabled, final Activity activity){
        toggleGPSTracking(enabled, activity, 0);
    }

    /**
     * Toggles the location tracking on/off.
     * The interval needs to be a value between R.integer.tracker_minimum_interval
     * and R.integer.tracker_maximum_interval
     *
     * CAUTION: The interval needs to be > 0
     * It will work with the minimum interval but other calculations might be wrong
     * @param enabled
     * @param activity
     * @param interval
     */
    public void toggleGPSTracking(boolean enabled, final Activity activity, int interval){

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        Intent intent = new Intent(activity, GPSBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, LocationTrackingHelper.LOCATION_TRACKING_INTENT, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if( enabled ) {
            LocationRequest locationRequest = buildLocationRequest(interval);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            SettingsClient client = LocationServices.getSettingsClient(activity);

            // create a task that checks if all location permissions are given
            // if not, Android asks for the remaining permissions
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            task.addOnFailureListener(activity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("GPSTracking", "task failed");
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                // Show the dialog by calling startResolutionForResult()
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(activity, 33);
                            } catch (IntentSender.SendIntentException sie) {
                                sie.printStackTrace();
                            }
                            break;
                    }
                }
            });

            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.d("GPS", "No permission given to track the location");
                return;
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, pendingIntent);
        }
        else
        {
            fusedLocationClient.removeLocationUpdates(pendingIntent);
        }
    }

    /**
     * Build a location request that runs in the given interval
     * @param interval
     * @return
     */
    private LocationRequest buildLocationRequest(int interval) {
        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(interval * LOCATION_TRACKING_RATE);
        locationRequest.setFastestInterval(LOCATION_TRACKING_RATE);
        return locationRequest;
    }


    /**
     * Sends the location data to the server.
     * <p>After that the last sent date is saved in the preferences.</p>
     * @param data
     * @return
     */
    public void sendLocation(LocationData data, LoomisaWebservice webservice) {
        webservice.uploadLocations(data, new Callback(){

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.d("GPS", response.body().toString());
                    throw new IOException("Unexpected code " + response);
                } else {
                    // do something wih the result
                    // preferences.edit().putLong(LocationTrackingHelper.DATA__LAST_DATA_SEND, new Date().getTime()).commit();
                    Log.d("GPS", response.body().toString());
                }
            }
        });
    }
}
