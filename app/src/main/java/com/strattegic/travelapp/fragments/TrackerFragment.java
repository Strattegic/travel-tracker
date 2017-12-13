package com.strattegic.travelapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.FileObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.strattegic.travelapp.R;
import com.strattegic.travelapp.common.TrackingDefines;
import com.strattegic.travelapp.helpers.LocationTrackingHelper;

/**
 * Created by Stratti on 12/12/2017.
 */

public class TrackerFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tracker, container, false);
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

    SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.navigation);
        bottomNavigationView.getMenu().findItem(R.id.navigation_tracker).setChecked(true);

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
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

        FileObserver observer = new FileObserver(getContext().getFilesDir().getPath()) { // set up a file observer to watch this directory on sd card

            @Override
            public void onEvent(int event, String file) {
                if( "locations.json".equals( file ) && FileObserver.MODIFY == event ){
                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView)getView().findViewById(R.id.textView_last_locations)).setText(LocationTrackingHelper.getLastLocationsAsText(getContext()));
                        }
                    });
                }
            }
        };
        observer.startWatching(); //START OBSERVING
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if( buttonView.equals(toggleLocationTrackingButton) ) {
            // toggle settings
            getActivity().findViewById(R.id.seekBar_tracking_interval).setEnabled(isChecked);
            getActivity().findViewById(R.id.switch_cellular).setEnabled(isChecked);

            // toggle location tracking
            toggleLocationTracking(isChecked);
        }
        else if( buttonView.equals(toggleCellular) ){
            // cellular
            sharedPref.edit().putBoolean(TrackingDefines.SETTINGS_TRACKING_USE_CELLULAR, isChecked).apply();
        }
    }

    /**
     * Enables / disables the location tracking
     * it also toggles the PendingIntent that runs in the background
     * @param enableTracking
     */
    private void toggleLocationTracking( boolean enableTracking ) {

        toggleLocationTrackingButton.setChecked(false);

        if (!enableTracking) {
            // disable the tracking
            LocationTrackingHelper.toggleGPSTracking(false, getActivity());
            sharedPref.edit().putBoolean(TrackingDefines.SETTINGS_TRACKING_ENABLED, false).apply();
        } else if (!hasLocationPermission()) {
            // request permissions
            ActivityCompat.requestPermissions(getActivity(), LOCATION_PERMS, PERMISSIONS_REQUEST_LOCATION);
        } else {
            // activate the tracking
            LocationTrackingHelper.toggleGPSTracking(true, getActivity());
            toggleLocationTrackingButton.setChecked(true);
            sharedPref.edit().putBoolean(TrackingDefines.SETTINGS_TRACKING_ENABLED, true).apply();
        }
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

    private boolean hasLocationPermission(){
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        sharedPref.edit().putInt(TrackingDefines.SETTINGS_TRACKING_INTERVAL, i).apply();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
