package com.strattegic.travelapp.common;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.strattegic.travelapp.data.LocationData;

import java.io.IOException;

import ca.mimic.oauth2library.OAuth2Client;
import ca.mimic.oauth2library.OAuthError;
import ca.mimic.oauth2library.OAuthResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Stratti on 18/12/2017.
 */

public class LoomisaWebservice {

    public static final String WEBSERVICE_URL_LOCATIONS = "http://192.168.100.68/api/locations";
    public static final String WEBSERVICE_URL_LOGIN = "http://192.168.100.68/oauth/token";

    private static LoomisaWebservice instance;
    private static Gson gson;

    private LoomisaWebservice(){
        // Singleton
    }

    public static LoomisaWebservice getInstance(){
        if( instance == null ){
            instance = new LoomisaWebservice();
            gson = new Gson();
    }
        return instance;
    }

    /**
     * Uploads all remaining locations that were tracked by the current user
     * @param data
     * @param callback
     */
    public void uploadLocations(LocationData data, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, gson.toJson(data));

        Request request = new Request.Builder()
                .url(WEBSERVICE_URL_LOCATIONS)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void login(String email, String password, Context context) {
        SessionManager sessionManager = new SessionManager(context);
        OAuth2Client.Builder builder = new OAuth2Client.Builder("2", "asPTKtMKrxirzwgJEUpjXVpGJIjEl8fHFi4UL4Yq", WEBSERVICE_URL_LOGIN)
                .grantType("password")
                .scope("")
                .username(email)
                .password(password);

        OAuthResponse response = null;
        try {
            response = builder.build().requestAccessToken();

            if (response.isSuccessful()) {
                String accessToken = response.getAccessToken();
                String refreshToken = response.getRefreshToken();
                sessionManager.login(email, accessToken, refreshToken);

            } else {
                OAuthError error = response.getOAuthError();
                String errorMsg = error.getError();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
