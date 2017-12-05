package com.travelapp.strattegic.travelapp;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Strattegic on 19.11.17.
 */

public class TrackerActivity extends MainActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout dynamicContent = findViewById(R.id.content_view);
        View wizard = getLayoutInflater().inflate(R.layout.activity_tracker, null);
        dynamicContent.addView(wizard);
        getBottomNavigationView().getMenu().findItem(R.id.navigation_tracker).setChecked(true);
    }
}
