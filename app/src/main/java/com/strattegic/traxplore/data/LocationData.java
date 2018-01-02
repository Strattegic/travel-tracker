package com.strattegic.traxplore.data;

import java.util.Date;

/**
 * Created by Stratti on 08/12/2017.
 */

public class LocationData {
    private double lon;
    private double lat;
    private Date added_on;
    private boolean alreadySent;

    public LocationData(){
        alreadySent = false;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public Date getAddedOn() {
        return added_on;
    }

    public void setAddedOn(Date added_on) {
        this.added_on = added_on;
    }

    public boolean isAlreadySent() {
        return alreadySent;
    }

    public void setAlreadySent(boolean alreadySent) {
        this.alreadySent = alreadySent;
    }
}
