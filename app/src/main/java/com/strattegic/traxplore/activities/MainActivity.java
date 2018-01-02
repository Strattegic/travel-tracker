package com.strattegic.traxplore.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.strattegic.traxplore.R;
import com.strattegic.traxplore.common.TraxploreWebservice;
import com.strattegic.traxplore.common.SessionManager;
import com.strattegic.traxplore.fragments.HomeFragment;
import com.strattegic.traxplore.fragments.TrackerFragment;
import com.strattegic.traxplore.helpers.LocationTrackingHelper;

public class MainActivity extends AppCompatActivity {

    private SessionManager session;
    private TraxploreWebservice webservice;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.navigation_tracker:
                    selectedFragment = new TrackerFragment();
                    break;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, selectedFragment);
            transaction.commit();
            return true;
        }

    };

    private LocationTrackingHelper locationTrackingHelper;

    public LocationTrackingHelper getLocationTrackingHelper(){
        return locationTrackingHelper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.locationTrackingHelper = new LocationTrackingHelper();
        this.webservice = new TraxploreWebservice();

        session = new SessionManager(getApplicationContext());
        if( session.checkLogin() ) {

            BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
            bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

            //Manually displaying the first fragment - one time only
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, new HomeFragment());
            transaction.commit();
        }
    }

    public TraxploreWebservice getWebservice() {
        return webservice;
    }
}
