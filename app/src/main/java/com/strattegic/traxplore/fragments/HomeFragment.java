package com.strattegic.traxplore.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.strattegic.traxplore.R;
import com.strattegic.traxplore.common.TraxploreWebserviceCallback;
import com.strattegic.traxplore.data.Journey;
import com.strattegic.traxplore.data.LocationData;
import com.strattegic.traxplore.helpers.LocationTrackingHelper;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;

/**
 * Created by Stratti on 12/12/2017.
 */

public class HomeFragment extends MainFragment implements OnMapReadyCallback {
    private GoogleMap map;
    private ArrayList<LatLng> allLocations;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allLocations = new ArrayList<>();
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.navigation);
        bottomNavigationView.getMenu().findItem(R.id.navigation_home).setChecked(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if( getSessionManager().checkLogin() ) {

            getWebservice().getJourneys(new TraxploreWebserviceCallback(getContext()) {
                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    Gson gson = new GsonBuilder().setDateFormat(LocationTrackingHelper.GSON_DATE_FORMAT).create();

                    String body = response.body().string();

                    if (response.code() == 401) {
                        // unauthorized! go back to the login activity
                    }
                    final Journey[] journeys = gson.fromJson(body, Journey[].class);
                    Log.i("Journeys", "successfully received locations" + journeys);

                    // update the UI
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateJourneys(journeys);
                        }
                    });
                }
            });

            /*
            // get the current locations for the user and update the view accordingly
            getWebservice().getLocations(new TraxploreWebserviceCallback(getContext()) {
                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    Gson gson = new GsonBuilder().setDateFormat(LocationTrackingHelper.GSON_DATE_FORMAT).create();

                    String body = response.body().string();

                    if (response.code() == 401) {
                        // unauthorized! go back to the login activity
                    }
                    final LocationData[] locations = gson.fromJson(body, LocationData[].class);
                    Log.i("Locations", "successfully received locations" + locations);

                    // update the UI
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateLocations(locations);
                        }
                    });
                }
            });
            */
        }
    }

    private void updateJourneys(Journey[] journeys){
        ArrayList<String> spinnerOptions = new ArrayList<>();

        for (int i = 0; i < journeys.length; i++) {
            spinnerOptions.add( journeys[i].getName() );
        }

        Spinner journeysSpinner = getActivity().findViewById(R.id.spinner_journeys);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerOptions);
        journeysSpinner.setAdapter(adapter);
    }

    private void updateLocations(LocationData[] locations) {

        LinearLayout table = getActivity().findViewById( R.id.linearLayout_table_last_locations_content );
        TableRow.LayoutParams params = new TableRow.LayoutParams( TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT );
        params.weight = 0.3f;
        TableRow.LayoutParams paramsLong = new TableRow.LayoutParams( TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT );
        paramsLong.weight = 0.19f;

        for (int i = 0; i < locations.length; i++) {
            TableRow row = new TableRow( getActivity().getBaseContext() );

            // Date
            TextView dateText = new TextView( getActivity().getBaseContext() );
            dateText.setText( locations[i].getAddedOn() != null ? locations[i].getAddedOn().toString() : "" );
            dateText.setLayoutParams(paramsLong);
            row.addView( dateText );

            // lat
            TextView latText = new TextView( getActivity().getBaseContext() );
            latText.setText( String.valueOf( locations[i].getLat() ) );
            latText.setLayoutParams(params);
            row.addView( latText );

            // lon
            TextView lonText = new TextView( getActivity().getBaseContext() );
            lonText.setText( String.valueOf( locations[i].getLon() ) );
            lonText.setLayoutParams(params);
            row.addView( lonText );

            // add row to the table
            table.addView( row );

            allLocations.add(new LatLng(locations[i].getLat(), locations[i].getLon()));
            drawLocationsOnMap();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        drawLocationsOnMap();
    }

    private void drawLocationsOnMap(){
        if( !allLocations.isEmpty() && map != null ){
            map.addPolyline(new PolylineOptions()
                    .addAll(allLocations)
                    .width(5)
                    .color(Color.RED));
        }
    }
}
