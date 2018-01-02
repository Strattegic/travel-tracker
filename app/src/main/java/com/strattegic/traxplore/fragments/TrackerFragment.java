package com.strattegic.traxplore.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.strattegic.traxplore.R;
import com.strattegic.traxplore.helpers.LocationsFileCallback;
import com.strattegic.traxplore.common.SessionManager;
import com.strattegic.traxplore.common.TrackingDefines;
import com.strattegic.traxplore.helpers.LocationsFileHelper;

/**
 * Created by Stratti on 12/12/2017.
 */

public class TrackerFragment extends MainFragment
        implements CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracker, container, false);
    }

    private final int PERMISSIONS_REQUEST_LOCATION = 1;
    // required permission for the tracking
    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private Switch toggleLocationTrackingButton;
    private Switch toggleCellular;
    private SeekBar seekBarInterval;
    private Button logoutButton;

    private SharedPreferences sharedPref;
    private SessionManager sessionManager;

    private LocationsFileHelper locationsFileHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());
        locationsFileHelper = new LocationsFileHelper(getContext());

        ((TextView) view.findViewById(R.id.textView_username)).setText(sessionManager.getEmail());
        logoutButton = view.findViewById(R.id.button_logout);
        logoutButton.setOnClickListener(this);

        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.navigation);
        bottomNavigationView.getMenu().findItem(R.id.navigation_tracker).setChecked(true);

        sharedPref = getContext().getSharedPreferences(TrackingDefines.TRACKING_PREFS_NAME, Context.MODE_PRIVATE);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        toggleLocationTrackingButton = view.findViewById(R.id.switchTrackingToggle);
        toggleLocationTrackingButton.setOnCheckedChangeListener( this );

        // cellular switch
        toggleCellular = view.findViewById(R.id.switch_cellular);
        toggleCellular.setOnCheckedChangeListener( this );

        // upload interval
        seekBarInterval = view.findViewById(R.id.seekBar_tracking_interval);
        seekBarInterval.setOnSeekBarChangeListener(this);

        boolean trackingEnabled = sharedPref.getBoolean(TrackingDefines.SETTINGS_TRACKING_ENABLED, false);
        toggleLocationTrackingButton.setChecked(trackingEnabled);

        // everything that belongs to the tracking will be deactivated unless the tracking is enabled
        if( !trackingEnabled ) {
            SeekBar bar = getActivity().findViewById(R.id.seekBar_tracking_interval);
            bar.setEnabled(false);
        }
        toggleCellular.setChecked(sharedPref.getBoolean(TrackingDefines.SETTINGS_TRACKING_USE_CELLULAR, false));
        seekBarInterval.setProgress( sharedPref.getInt(TrackingDefines.SETTINGS_TRACKING_INTERVAL, TrackingDefines.SETTINGS_TRACKING_DEFAULT_INTERVAL) );

        locationsFileHelper.addLocationsFileChangeListener(new LocationsFileCallback(){

            @Override
            public void onLocationsFileChanged() {
                updateLastLocations();
            }
        });
        updateLastLocations();
    }

    public void updateLastLocations(){
        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView)getView().findViewById(R.id.textView_last_locations)).setText(locationsFileHelper.getLastLocationsAsText(getContext()));
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if( buttonView.equals(toggleLocationTrackingButton) ) {
            // toggle settings
            getActivity().findViewById(R.id.seekBar_tracking_interval).setEnabled(isChecked);
            getActivity().findViewById(R.id.switch_cellular).setEnabled(isChecked);

            // the user wants to enable the tracking but does not have enough permissions
            // lets ask him first
            if( isChecked && !hasLocationPermissions() ){
                // Log.d("GPS", "No permission given to track the location, trying to get it now");
                toggleLocationTracking(false);
                requestPermissions(LOCATION_PERMS, PERMISSIONS_REQUEST_LOCATION);
                return;
            }

            // toggle location tracking
            // only used if the user already has the location permission
            toggleLocationTracking(isChecked);
        }
        else if( buttonView.equals(toggleCellular) ){
            // cellular
            sharedPref.edit().putBoolean(TrackingDefines.SETTINGS_TRACKING_USE_CELLULAR, isChecked).apply();
        }
    }

    /**
     * Enables / disables the location tracking
     * the permission check has to be made before this is used
     * it also toggles the PendingIntent that runs in the background
     * @param enableTracking
     */
    private void toggleLocationTracking( boolean enableTracking ) {

        // the seekbar always returns 0 based values
        getLocationTrackingHelper().toggleGPSTracking(enableTracking, getActivity(), sharedPref.getInt(TrackingDefines.SETTINGS_TRACKING_INTERVAL, 0));
        toggleLocationTrackingOptions( enableTracking );
        sharedPref.edit().putBoolean(TrackingDefines.SETTINGS_TRACKING_ENABLED, enableTracking).apply();
    }

    /**
     * Toggles all the buttons that belong to the Location tracking options
     * @param enabled
     */
    private void toggleLocationTrackingOptions( boolean enabled ){
        toggleLocationTrackingButton.setChecked(enabled);
        seekBarInterval.setEnabled(enabled);
        toggleCellular.setEnabled(enabled);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    toggleLocationTracking(true);
                }
                return;
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        sharedPref.edit().putInt(TrackingDefines.SETTINGS_TRACKING_INTERVAL, seekBar.getProgress()).apply();
        toggleLocationTracking(sharedPref.getBoolean(TrackingDefines.SETTINGS_TRACKING_ENABLED, true));
    }

    @Override
    public void onClick(View view) {
        if(view == logoutButton){
            sessionManager.logout();
        }
    }

    /**
     * Checks wether or not the application has the sufficient permissions to track the location
     * @return
     */
    public boolean hasLocationPermissions(){
        return ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if( key.equals(TrackingDefines.SETTINGS_TRACKING_ENABLED ) ){
            toggleLocationTrackingButton.setChecked(sharedPreferences.getBoolean(TrackingDefines.SETTINGS_TRACKING_ENABLED, false));
        }
    }
}
