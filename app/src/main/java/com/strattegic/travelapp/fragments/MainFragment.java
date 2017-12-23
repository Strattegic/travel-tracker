package com.strattegic.travelapp.fragments;

import android.support.v4.app.Fragment;

import com.strattegic.travelapp.activities.MainActivity;
import com.strattegic.travelapp.common.LoomisaWebservice;
import com.strattegic.travelapp.helpers.LocationTrackingHelper;

/**
 * Created by Stratti on 21/12/2017.
 */

public class MainFragment extends Fragment {

    public LocationTrackingHelper getLocationTrackingHelper(){
        return ((MainActivity)this.getActivity()).getLocationTrackingHelper();
    }

    public LoomisaWebservice getWebservice(){
        return ((MainActivity)this.getActivity()).getWebservice();
    }
}
