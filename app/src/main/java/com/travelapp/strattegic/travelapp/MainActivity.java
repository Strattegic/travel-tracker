package com.travelapp.strattegic.travelapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent in = new Intent(getBaseContext(), HomeActivity.class);
                    startActivity(in);
                    overridePendingTransition(0, 0);
                    break;
                case R.id.navigation_tracker:
                    Intent in2 = new Intent(getBaseContext(), TrackerActivity.class);
                    startActivity(in2);
                    overridePendingTransition(0, 0);
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getBottomNavigationView().setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    protected BottomNavigationView getBottomNavigationView()
    {
        return findViewById(R.id.navigation);
    }

}
