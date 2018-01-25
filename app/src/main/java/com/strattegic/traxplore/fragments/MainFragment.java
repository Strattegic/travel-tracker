package com.strattegic.traxplore.fragments;

import android.support.v4.app.Fragment;

import com.strattegic.traxplore.activities.MainActivity;
import com.strattegic.traxplore.common.SessionManager;
import com.strattegic.traxplore.common.TraxploreWebservice;
import com.strattegic.traxplore.helpers.LocationTrackingHelper;

/**
 * Created by Stratti on 21/12/2017.
 */

public class MainFragment extends Fragment {

    public LocationTrackingHelper getLocationTrackingHelper(){
        return ((MainActivity)this.getActivity()).getLocationTrackingHelper();
    }

    public TraxploreWebservice getWebservice(){
        return ((MainActivity)this.getActivity()).getWebservice();
    }

    public SessionManager getSessionManager(){
        return ((MainActivity)this.getActivity()).getSessionManager();
    }
}
