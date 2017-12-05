package com.strattegic.travelapp;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;

/**
 * Created by Stratti on 01/12/2017.
 */

public class TrackerActivity extends MainActivity implements CompoundButton.OnCheckedChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView frameLayout = (ScrollView) findViewById( R.id.content );
        View wiz = getLayoutInflater().inflate( R.layout.activity_tracker, null );
        frameLayout.addView( wiz );

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.getMenu().findItem(R.id.navigation_tracker).setChecked(true);

        Switch toggleLocationTrackingButton = (Switch) findViewById(R.id.switchTrackingToggle);
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
        if( enableTracking && !hasLocationPermission() ) {
            // request permissions
        }
        else if( enableTracking ) {
            // enable Tracking
        }
        else if( !enableTracking ) {
            // disable Trackingl
        }
    }

    private boolean hasLocationPermission(){
        return Build.VERSION.SDK_INT >= 23 && checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
