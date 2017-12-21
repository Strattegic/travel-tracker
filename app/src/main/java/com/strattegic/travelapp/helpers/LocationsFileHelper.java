package com.strattegic.travelapp.helpers;

import android.content.Context;
import android.os.FileObserver;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.strattegic.travelapp.data.LocationContainer;
import com.strattegic.travelapp.data.LocationData;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Stratti on 21/12/2017.
 */

public class LocationsFileHelper {

    private static final String LOCATION_FILE_NAME = "locations.json";

    private final Context context;
    protected Gson gson;

    public LocationsFileHelper(Context context) {
        gson = new GsonBuilder().setDateFormat(LocationTrackingHelper.GSON_DATE_FORMAT).create();
        this.context = context;
    }

    /**
     * Adds a listener on the locations file
     * The given callback will be executed as soon as the locations file was modified
     * @param callback
     */
    public void addLocationsFileChangeListener(final LocationsFileCallback callback){
        FileObserver observer = new FileObserver(context.getFilesDir().getPath()) { // set up a file observer to watch this directory on sd card

            @Override
            public void onEvent(int event, String file) {
                if( LOCATION_FILE_NAME.equals( file ) && FileObserver.MODIFY == event ){
                    callback.onLocationsFileChanged();
                }
            }
        };
        observer.startWatching(); //START OBSERVING
    }

    public String getLastLocationsAsText(Context context){

        StringBuilder builder = new StringBuilder();
        LocationContainer locationContainer = getLocationsFromFile();

        for( LocationData loc : locationContainer.getLocations() ){
            builder.append(loc.getLat()).append(", ").append(loc.getLon());
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * TODO: fertig implementieren
     * @param data
     */
    public void saveLocationOnDevice(LocationData data) {

        LocationContainer locationContainer = getLocationsFromFile();
        locationContainer.addLocation( data );
        saveLocationsToFile(locationContainer);
    }

    /**
     * returns the locations.json file if it was created earlier
     * if not, the file will be created and returned
     * @return
     */
    private LocationContainer getLocationsFromFile() {

        FileInputStream locationsFileInput = null;

        // First try to open the file
        // if this fails we create a new locations.json file
        try {
            locationsFileInput = context.openFileInput(LOCATION_FILE_NAME);
        } catch (FileNotFoundException e) {
            // file was not found - create it
            try {
                context.openFileOutput("locations.json", Context.MODE_PRIVATE).close();
                locationsFileInput = context.openFileInput(LOCATION_FILE_NAME);
            } catch (IOException e1) {
                // could not create the new file!
                Log.d("Locations", "Locations file could not be created!", e1);
                return null;
            }
        }

        // after the file was either opened or created we can start importing the data
        try {
            JsonReader jsonReader = new JsonReader( new BufferedReader( new InputStreamReader(locationsFileInput, "UTF-8")));

            LocationContainer locationContainer = gson.fromJson(jsonReader, LocationContainer.class);
            locationsFileInput.close();
            jsonReader.close();

            if( locationContainer == null ){
                return new LocationContainer();
            }
            return locationContainer;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Locations", "Could not open the locations file!", e);
        }
        return null;
    }

    /**
     * Opens the locations file and saves the given LocationContainer in it.
     * @param locationContainer
     */
    private void saveLocationsToFile(LocationContainer locationContainer){
        try {
            FileOutputStream locationsOutput = context.openFileOutput(LOCATION_FILE_NAME, Context.MODE_PRIVATE);
            locationsOutput.write(gson.toJson(locationContainer).getBytes());
            locationsOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Locations", "Could not save the locations file!", e);
        }
    }
}
