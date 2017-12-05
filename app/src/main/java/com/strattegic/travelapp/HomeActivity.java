package com.strattegic.travelapp;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by Stratti on 01/12/2017.
 */

public class HomeActivity extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView frameLayout = (ScrollView) findViewById( R.id.content );
        View wiz = getLayoutInflater().inflate( R.layout.activity_home, null );
        frameLayout.addView( wiz );

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.getMenu().findItem(R.id.navigation_home).setChecked(true);
    }
}
