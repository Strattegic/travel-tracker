package com.strattegic.traxplore.common;

import android.content.Context;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Strattegic on 21/12/2017.
 */

public abstract class TraxploreWebserviceCallback implements Callback {

    private final Context context;

    public TraxploreWebserviceCallback(Context context){
        this.context = context;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        // Standard webservice error
        // Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();
    }
}
