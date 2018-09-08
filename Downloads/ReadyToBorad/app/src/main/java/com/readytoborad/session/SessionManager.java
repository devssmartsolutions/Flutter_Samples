package com.readytoborad.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.readytoborad.activity.LoginActivity;
import com.readytoborad.model.UserDetail;


/**
 * Created by harendrasinghbisht on 31/03/16.
 */
public class SessionManager {

    // Shared Preferences
    SharedPreferences pref, pref1;

    // Editor for Shared preferences
    SharedPreferences.Editor editor, editor1;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "tansmeetpref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_USER_ID = "userid";
    public static final String KEY_FATHER_NAME = "father_name";
    public static final String KEY_MOTHER_NAME = "mother_name";
    public static final String KEY_PHONE_NUMBER = "phone_no";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_DRIVER = "driver";
    public static final String KEY_BUS_NUMBER = "bus_no";
    public static final String KEY_BUS_ID = "bus_id";
    public static final String KEY_TOKEN = "header_token";
    public static final String KEY_USER_TYPE = "user_type";
    public static final String KEY_FIRST_LOGIN = "is_first_login";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        pref1 = _context.getSharedPreferences("tpm", PRIVATE_MODE);
        editor1 = pref1.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(UserDetail userDetail, String type, String token) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USER_TYPE, type);

        editor.putString(KEY_USER_ID, userDetail.getParentId());
        editor.putString(KEY_FATHER_NAME, userDetail.getFatherName());
        editor.putString(KEY_MOTHER_NAME, userDetail.getMotherName());
        //editor.putString(KEY_DRIVER, userDetail.getDriverName());
      //  editor.putString(KEY_LATITUDE, userDetail.get());
        //editor.putString(KEY_LONGITUDE, userDetail.getLongitude());
        editor.putString(KEY_PHONE_NUMBER, userDetail.getPhoneNo());
       // editor.putString(KEY_BUS_NUMBER, userDetail.getBusNo());
      //  editor.putString(KEY_FIRST_LOGIN, userDetail.getPopupFlag());

        // commit changes
        editor.apply();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
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
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.apply();

    }

    public void setKeyFirstLogin(String keyFirstLogin) {
        editor.putString(KEY_FIRST_LOGIN, keyFirstLogin);

        // commit changes
        editor.apply();
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public String getKeyBusNumber() {
        return pref.getString(KEY_BUS_NUMBER, null);
    }

    public String getKeyDriver() {
        return pref.getString(KEY_BUS_NUMBER, null);
    }

    public String getKeyFatherName() {
        return pref.getString(KEY_FATHER_NAME, null);
    }

    public String getKeyMotherName() {
        return pref.getString(KEY_MOTHER_NAME, null);
    }

    public String getKeyLatitude() {
        return pref.getString(KEY_LATITUDE, null);
    }
    public void setKeyLatitude(String keyLatitude) {
        editor.putString(KEY_LATITUDE, keyLatitude);

        // commit changes
        editor.apply();
    }

    public String getKeyBusId() {
        return pref.getString(KEY_BUS_ID, null);
    }
    public void setKeyBusId(String keyBusId) {
        editor.putString(KEY_BUS_ID, keyBusId);

        // commit changes
        editor.apply();
    }

    public String getKeyLongitude() {
        return pref.getString(KEY_LONGITUDE, null);
    }

    public void setKeyLongitude(String keyLongitude) {
        editor.putString(KEY_LONGITUDE, keyLongitude);

        // commit changes
        editor.apply();
    }


    public String getKeyFirstLogin() {
        return pref.getString(KEY_FIRST_LOGIN, null);
    }

    public String getKeyToken() {
        return pref.getString(KEY_TOKEN, null);
    }

    public String getKeyUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    public String getKeyUserType() {
        return pref.getString(KEY_USER_TYPE, null);
    }
    public void setKeyUserType(String user_type) {
        editor.putString(KEY_USER_TYPE, user_type);

        // commit changes
        editor.apply();
    }
    public String getKeyPhoneNumber() {
        return pref.getString(KEY_PHONE_NUMBER, null);
    }

}
