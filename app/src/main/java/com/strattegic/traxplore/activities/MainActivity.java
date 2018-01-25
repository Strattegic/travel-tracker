package com.strattegic.traxplore.activities;

import android.content.Intent;
import android.media.MediaCas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

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

        // initialize the session manager for all children activities / fragments
        session = new SessionManager(getApplicationContext());
        this.webservice = new TraxploreWebservice(session);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, new HomeFragment());
        transaction.commit();

        if( !session.checkLogin() ){
            findViewById( R.id.textView_notLoggedInMessage ).setVisibility(View.VISIBLE);
            findViewById( R.id.textView_notLoggedInMessage ).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // the user is not logged in, direct him to the login page
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    // Closing all the Activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    // Add new Flag to start new Activity
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    // Staring Login Activity
                    getApplicationContext().startActivity(i);
                }
            });
        }
        else{
            findViewById( R.id.textView_notLoggedInMessage ).setVisibility(View.INVISIBLE);
        }
    }

    public TraxploreWebservice getWebservice() {
        return webservice;
    }

    public SessionManager getSessionManager(){
        return session;
    }
}
