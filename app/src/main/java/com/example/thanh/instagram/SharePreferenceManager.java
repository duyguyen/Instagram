package com.example.thanh.instagram;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.thanh.instagram.models.User;

public class SharePreferenceManager {

    // == variables ==
    private static final String FILENAME = "INSTAGRAMLOGIN";
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String IMAGE= "image";
    private static final String ID = "id";
    private static Context mContext;
    private static SharePreferenceManager mSharePreferenceManager;

    /*
     * Use the singular pattern because we don't want to instance object more than 1 time
     * */

    private SharePreferenceManager(Context context) {
        this.mContext = context;
    }

    // == get the only object ==
    public static synchronized SharePreferenceManager getInstance(Context mContext) {
        if (mSharePreferenceManager == null) {
            mSharePreferenceManager = new SharePreferenceManager(mContext);
        }
        return mSharePreferenceManager;
    }

    // == save data ==
    public void storeUserData(User user) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit(); // access to the file INSTAGRAMLOGIN
        editor.putString(USERNAME, user.getUsername());
        editor.putString(EMAIL, user.getEmail());
        editor.putString(IMAGE, user.getImage());
        editor.putInt(ID, user.getId());
        editor.apply();

    }

    // == The user was login or not ==
    public boolean isUserLogIn() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE); // access the file
        if (sharedPreferences.getString(USERNAME, null) != null) {
            // the user has already login
            return true;
        }
        return false;

    }

    // == log user out ==
    public void logUserOut() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit(); // access to the file INSTAGRAMLOGIN
        editor.clear(); // access to the file and clear
        editor.apply(); // in order to execute above
        /*
         * get the user from the current activity
         * */
    }

    // == get the data of the user to display ==
    public User getUserData() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        User user = new User(
                sharedPreferences.getInt(ID, -1),
                sharedPreferences.getString(EMAIL, null),
                sharedPreferences.getString(USERNAME, null),
                sharedPreferences.getString(IMAGE, null)

        );
        return user;
    }

}
