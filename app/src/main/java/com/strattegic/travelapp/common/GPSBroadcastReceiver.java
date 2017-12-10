package com.strattegic.travelapp.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.strattegic.travelapp.data.LocationContainer;
import com.strattegic.travelapp.data.LocationData;
import com.strattegic.travelapp.helpers.LocationTrackingHelper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Strattegic on 08/12/2017.
 */

public class GPSBroadcastReceiver extends BroadcastReceiver {

    protected static Gson gson;
    protected Context context;
    protected SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        LocationResult result = (LocationResult) intent.getExtras().get("com.google.android.gms.location.EXTRA_LOCATION_RESULT");
        if( result != null ){
            Log.d("GPS", result.toString()); //To Implement
            Toast.makeText(context, result.toString(), Toast.LENGTH_SHORT).show();
            LocationData data = new LocationData();
            data.setLat( result.getLastLocation().getLatitude() );
            data.setLat( result.getLastLocation().getLongitude() );
            sendLocation(data);
        }
        else {
            Toast.makeText(context, "No location available", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sends the location data to the server.
     * <p>After that the last sent date is saved in the preferences.</p>
     * @param data
     * @return
     */
    private boolean sendLocation(LocationData data) {
        String url = "http://192.168.100.68/api/locations";

        if( gson == null ){
            gson = new Gson();
        }
        OkHttpClient client = new OkHttpClient();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, gson.toJson(data));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    // do something wih the result
                    // preferences.edit().putLong(LocationTrackingHelper.DATA__LAST_DATA_SEND, new Date().getTime()).commit();
                    Log.d("GPS", response.body().toString());
                }
            }
        });
        return false;
    }

    private void saveLocation(LocationData data) {

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
}
