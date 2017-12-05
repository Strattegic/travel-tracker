package com.travelapp.strattegic.travelapp;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by macbookair on 19.11.17.
 */

public class HomeActivity extends MainActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout content = findViewById(R.id.content_view);
        View wizard = getLayoutInflater().inflate(R.layout.activity_home, null);
        content.addView(wizard);
        getBottomNavigationView().getMenu().findItem(R.id.navigation_home).setChecked(true);
    }
}
