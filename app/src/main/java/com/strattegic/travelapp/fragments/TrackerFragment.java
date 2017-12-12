package com.strattegic.travelapp.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;

import com.strattegic.travelapp.R;
import com.strattegic.travelapp.helpers.LocationTrackingHelper;

/**
 * Created by Stratti on 12/12/2017.
 */

public class TrackerFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.navigation);
        bottomNavigationView.getMenu().findItem(R.id.navigation_tracker).setChecked(true);

        toggleLocationTrackingButton = view.findViewById(R.id.switchTrackingToggle);
        toggleLocationTrackingButton.setOnCheckedChangeListener( this );

        SeekBar bar = getActivity().findViewById(R.id.seekBar_tracking_interval);
        bar.setEnabled(false);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // toggle settings
        getActivity().findViewById(R.id.seekBar_tracking_interval).setEnabled( isChecked );
        getActivity().findViewById(R.id.switch_cellular).setEnabled( isChecked );

        // toggle location tracking
        toggleLocationTracking( isChecked );
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
        } else if (!hasLocationPermission()) {
            // request permissions
            ActivityCompat.requestPermissions(getActivity(), LOCATION_PERMS, PERMISSIONS_REQUEST_LOCATION);
        } else {
            // activate the tracking
            LocationTrackingHelper.toggleGPSTracking(true, getActivity());
            toggleLocationTrackingButton.setChecked(true);
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
}
