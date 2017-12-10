package com.strattegic.travelapp.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;

import com.strattegic.travelapp.R;
import com.strattegic.travelapp.helpers.LocationTrackingHelper;

/**
 * Created by Strattegic on 01/12/2017.
 */

public class TrackerActivity extends MainActivity implements CompoundButton.OnCheckedChangeListener {

    private final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    private static final String[] LOCATION_PERMS={
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };
    private Switch toggleLocationTrackingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView frameLayout = (ScrollView) findViewById( R.id.content );
        View wiz = getLayoutInflater().inflate( R.layout.activity_tracker, null );
        frameLayout.addView( wiz );

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.getMenu().findItem(R.id.navigation_tracker).setChecked(true);

        toggleLocationTrackingButton = (Switch) findViewById(R.id.switchTrackingToggle);
        toggleLocationTrackingButton.setOnCheckedChangeListener( this );

        SeekBar bar = (SeekBar) findViewById(R.id.seekBar_tracking_interval);
        bar.setEnabled(false);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // toggle settings
        findViewById(R.id.seekBar_tracking_interval).setEnabled( isChecked );
        findViewById(R.id.switch_cellular).setEnabled( isChecked );

        // toggle location tracking
        toggleLocationTracking( isChecked );
    }

    private void toggleLocationTracking( boolean enableTracking ) {

        toggleLocationTrackingButton.setChecked(false);

        if (!enableTracking) {
            // disable the tracking
            LocationTrackingHelper.toggleGPSTracking(false, this);
        } else if (!hasLocationPermission()) {
            // request permissions
            ActivityCompat.requestPermissions(this, LOCATION_PERMS, PERMISSIONS_REQUEST_FINE_LOCATION);
            /*if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation why the location permission is needed
            } else {
                ActivityCompat.requestPermissions(this, LOCATION_PERMS, PERMISSIONS_REQUEST_FINE_LOCATION);
            }*/
        } else {
            // activate the tracking
            LocationTrackingHelper.toggleGPSTracking(true, this);
            toggleLocationTrackingButton.setChecked(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    toggleLocationTracking(true);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private boolean hasLocationPermission(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
