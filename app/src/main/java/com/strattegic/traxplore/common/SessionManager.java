package com.strattegic.traxplore.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.strattegic.traxplore.activities.LoginActivity;

/**
 * Created by Stratti on 13/12/2017.
 */

public class SessionManager {
    private final Context context;
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    private static final String PREF_NAME = "SessionPreferences";
    private static final String PREF_KEY_IS_LOGIN = "isLogin";
    private static final String PREF_KEY_EMAIL = "email";
    private static final String PREF_KEY_ACCESS_TOKEN = "accessToken";
    private static final String PREF_KEY_REFRESH_TOKEN = "refreshToken";
    private final int PRIVATE_MODE = 0;

    public SessionManager(Context context){
        this.context = context;
        this.pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        this.editor = pref.edit();
    }

    public String getAccessToken(){
        return pref.getString(PREF_KEY_ACCESS_TOKEN, null);
    }

    public String getEmail(){
        return pref.getString(PREF_KEY_EMAIL, "");
    }
    /**
     * Login a user.
     * @param email
     */
    public void login(String email, String accessToken, String refreshToken) {
        editor.putString(PREF_KEY_EMAIL, email);
        editor.putString(PREF_KEY_ACCESS_TOKEN, accessToken);
        editor.putString(PREF_KEY_REFRESH_TOKEN, refreshToken);
        editor.putBoolean(PREF_KEY_IS_LOGIN, true);
        editor.commit();
    }

    public boolean checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            context.startActivity(i);
            return false;
        }
        return true;
    }

    public void logout(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        context.startActivity(i);
    }

    private boolean isLoggedIn(){
        return pref.getBoolean(PREF_KEY_IS_LOGIN, false);
    }
}
