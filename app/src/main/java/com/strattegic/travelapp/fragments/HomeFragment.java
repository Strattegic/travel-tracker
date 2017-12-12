package com.strattegic.travelapp.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.strattegic.travelapp.R;
import com.strattegic.travelapp.common.TrackingDefines;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Stratti on 12/12/2017.
 */

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ArrayList<LatLng> locations;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_home, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locations = new ArrayList<>();
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.navigation);
        bottomNavigationView.getMenu().findItem(R.id.navigation_home).setChecked(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        updateLastLocations();
    }

    private void updateLastLocations() {
        String url = TrackingDefines.WEBSERVICE_URL_LOCATIONS;
        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                LinearLayout table = getActivity().findViewById( R.id.linearLayout_table_last_locations_content );
                TableRow.LayoutParams params = new TableRow.LayoutParams( TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT );
                params.weight = 0.3f;
                TableRow.LayoutParams paramsLong = new TableRow.LayoutParams( TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT );
                paramsLong.weight = 0.19f;

                for (int i = 0; i < response.length(); i++) {
                    try {
                        TableRow row = new TableRow( getActivity().getBaseContext() );

                        // Date
                        TextView dateText = new TextView( getActivity().getBaseContext() );
                        dateText.setText( response.getJSONObject(i).get("added_on").toString() );
                        dateText.setLayoutParams(paramsLong);
                        row.addView( dateText );

                        // lat
                        TextView latText = new TextView( getActivity().getBaseContext() );
                        latText.setText( response.getJSONObject(i).get("lat").toString() );
                        latText.setLayoutParams(params);
                        row.addView( latText );

                        // lon
                        TextView lonText = new TextView( getActivity().getBaseContext() );
                        lonText.setText( response.getJSONObject(i).get("lon").toString() );
                        lonText.setLayoutParams(params);
                        row.addView( lonText );

                        // add row to the table
                        table.addView( row );

                        locations.add(new LatLng(Double.parseDouble(response.getJSONObject(i).get("lat").toString()), Double.parseDouble(response.getJSONObject(i).get("lon").toString())));
                        drawLocationsOnMap();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println( error );
            }
        });
        queue.add(jsonArrayRequest);
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
        mMap = googleMap;
        drawLocationsOnMap();
    }

    private void drawLocationsOnMap(){
        if( !locations.isEmpty() ){
            mMap.addPolyline(new PolylineOptions()
                    .addAll(locations)
                    .width(5)
                    .color(Color.RED));
        }
    }
}
