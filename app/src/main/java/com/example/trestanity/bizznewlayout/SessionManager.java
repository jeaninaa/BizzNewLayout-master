package com.example.trestanity.bizznewlayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Trestanity on 05/08/2017.
 */
public class SessionManager {

    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;


    // Shared Preferences
    SharedPreferences pref2;
    // Editor for Shared preferences
    SharedPreferences.Editor editor2;
    // Sharedpref file name
    private static final String PREF_NAME2 = "bizzName";
    // Bizz name (make variable public to access from outside)
    public static final String KEY_BIZZ_NAME = "bizzName";

    // Shared Preferences
    SharedPreferences pref3;
    // Editor for Shared preferences
    SharedPreferences.Editor editor3;
    // Sharedpref file name
    private static final String PREF_NAME3 = "currentLocation";

    // Bizz name (make variable public to access from outside)
    public static final String KEY_CURRENT_LAT = "currentLatitude";
    public static final String KEY_CURRENT_LON = "currentLongitude";

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_USERNAME = "name";

    // Email address (make variable public to access from outside)
    public static final String KEY_PASSWORD = "email";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        pref2 = _context.getSharedPreferences(PREF_NAME2, PRIVATE_MODE);
        pref3 = _context.getSharedPreferences(PREF_NAME3, PRIVATE_MODE);
        editor = pref.edit();
        editor2 = pref2.edit();
        editor3 = pref3.edit();
    }

    /**
     * Create bizzname session
     * */
    public void createLocation(double lat, double lon){
        editor3.clear();
        editor3.commit();

        // Storing name in pref
        editor3.putString(KEY_CURRENT_LAT, String.valueOf(lat));
        editor3.putString(KEY_CURRENT_LON, String.valueOf(lon));

        // commit changes
        editor3.commit();
    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getLocation(){
        HashMap<String, String> location = new HashMap<String, String>();

        // current location
        location.put(KEY_CURRENT_LAT, pref3.getString(KEY_CURRENT_LAT, null));
        location.put(KEY_CURRENT_LON, pref3.getString(KEY_CURRENT_LON, null));

        return location;
    }

    /**
     * Create bizzname session
     * */
    public void createBizzSession(String bizz){
        editor2.clear();
        editor2.commit();

        // Storing name in pref
        editor2.putString(KEY_BIZZ_NAME, bizz);

        // commit changes
        editor2.commit();
    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getBizz(){
        HashMap<String, String> bizz = new HashMap<String, String>();

        // user name
        bizz.put(KEY_BIZZ_NAME, pref2.getString(KEY_BIZZ_NAME, null));

        return bizz;
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String name, String email){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_USERNAME, name);

        // Storing email in pref
        editor.putString(KEY_PASSWORD, email);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));

        // user email id
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

}
