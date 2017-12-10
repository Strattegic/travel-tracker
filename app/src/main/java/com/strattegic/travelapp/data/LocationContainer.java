package com.strattegic.travelapp.data;

import java.util.ArrayList;

/**
 * Created by Stratti on 16/03/2017.
 */

public class LocationContainer {
    ArrayList<LocationData> locations;

    public LocationContainer() {
        this.locations = new ArrayList<>();
    }

    public ArrayList<LocationData> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<LocationData> locations) {
        this.locations = locations;
    }

    public void addLocation(LocationData data) {
        locations.add( data );
    }
}
