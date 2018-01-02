package com.strattegic.travelapp.common;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;

/**
 * Created by Strattegic on 21/12/2017.
 */

public abstract class LoomisaWebserviceCallback implements Callback {

    private final Context context;

    public LoomisaWebserviceCallback(Context context){
        this.context = context;
    }
    @Override
    public void onFailure(Call call, IOException e) {
        // Standard webservice error
        // Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();
    }
}
