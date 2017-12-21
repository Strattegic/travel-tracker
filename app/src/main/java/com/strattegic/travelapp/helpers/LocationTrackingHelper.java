package com.strattegic.travelapp.helpers;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
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
import com.google.gson.stream.JsonReader;
import com.strattegic.travelapp.common.GPSBroadcastReceiver;
import com.strattegic.travelapp.common.LoomisaWebservice;
import com.strattegic.travelapp.data.LocationContainer;
import com.strattegic.travelapp.data.LocationData;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Stratti on 08/12/2017.
 */

public class LocationTrackingHelper {

    private static final int LOCATION_TRACKING_INTENT = 1337;
    protected static Gson gson;
    private static FusedLocationProviderClient mFusedLocationClient;

    public static void toggleGPSTracking(boolean enabled, final Activity activity){
        LocationTrackingHelper.toggleGPSTracking(enabled, activity, 1000);
    }

    public static void toggleGPSTracking(boolean enabled, final Activity activity, int interval){

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
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
            mFusedLocationClient.requestLocationUpdates(locationRequest, pendingIntent);
        }
        else
        {
            mFusedLocationClient.removeLocationUpdates(pendingIntent);
        }
    }

    /**
     * Build a location request that runs in the given interval
     * @param interval
     * @return
     */
    private static LocationRequest buildLocationRequest(int interval) {
        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(interval);
        locationRequest.setFastestInterval(5000);
        return locationRequest;
    }


    /**
     * Sends the location data to the server.
     * <p>After that the last sent date is saved in the preferences.</p>
     * @param data
     * @return
     */
    public static void sendLocation(LocationData data) {
        LoomisaWebservice.getInstance().uploadLocations(data, new Callback(){

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

    /**
     * TODO: fertig implementieren
     * @param data
     * @param context
     */
    public static void saveLocationOnDevice(LocationData data, Context context) {

        if( gson == null ){
            gson = new Gson();
        }

        try {
            boolean hasLocationsFile = false;
            for( String applicationFile : context.fileList() )
            {
                if( applicationFile.equals("locations.json") ){
                    hasLocationsFile = true;
                    break;
                }
            }

            LocationContainer locationContainer;
            if( hasLocationsFile ) {
                FileInputStream locationsInput = context.openFileInput("locations.json");
                InputStreamReader isr = new InputStreamReader(locationsInput, "UTF-8");
                Reader reader = new BufferedReader(isr);
                JsonReader jsonReader = new JsonReader(reader);

                locationContainer = gson.fromJson(jsonReader, LocationContainer.class);

                if( locationContainer == null )
                {
                    locationContainer = new LocationContainer();
                }
                locationsInput.close();
            }
            else{
                locationContainer = new LocationContainer();
            }
            locationContainer.addLocation( data );

            FileOutputStream locationsOutput = context.openFileOutput("locations.json", Context.MODE_PRIVATE);
            locationsOutput.write(gson.toJson(locationContainer).getBytes());
            locationsOutput.close();

            //preferences.edit().putString(LocationTrackingHelper.DATA__LAST_KNOWN_LATITUDE, String.valueOf( data.getLat() ) )
            //        .putString(LocationTrackingHelper.DATA__LAST_KNOWN_LONGITUDE, String.valueOf( data.getLon() ) )
            //        .commit();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getLastLocationsAsText(Context context){

        try {
            FileInputStream locationsInput = context.openFileInput("locations.json");
            InputStreamReader isr = new InputStreamReader(locationsInput, "UTF-8");
            Reader reader = new BufferedReader(isr);
            JsonReader jsonReader = new JsonReader(reader);

            LocationContainer locationContainer = gson.fromJson(jsonReader, LocationContainer.class);
            StringBuilder builder = new StringBuilder();
            for( LocationData loc : locationContainer.getLocations() ){
                builder.append(loc.getLat()).append(", ").append(loc.getLon());
                builder.append("\n");
            }
            locationsInput.close();
            isr.close();
            reader.close();
            jsonReader.close();
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
