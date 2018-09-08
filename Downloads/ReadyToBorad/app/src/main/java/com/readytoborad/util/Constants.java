package com.readytoborad.util;

/**
 * Created by harendrasinghbisht on 15/01/17.
 */

public class Constants {
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final int SET_LOCATION_REQUEST = 200;

    public static final String PACKAGE_NAME =
            "app.android.schoolbell.util";

    public static final String EMPTY_STRING = "";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String MATRIX_API = "https://maps.googleapis.com/maps/api/distancematrix/json?";

    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";

    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    public static final String LOCATION_SOURCE_EXTRA = PACKAGE_NAME + ".LOCATION_SOURCE_EXTRA";
    public static final String LOCATION_DESTINATION_EXTRA = PACKAGE_NAME + ".LOCATION_DESTINATION_EXTRA";
    public static final String DATA_TRACK= "DATA_TRACK";
    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final int LOCATION_ALERT = 100;

    public static final int SCREEN_ID = -1;
    public static final int PARENT_NOTIFICATION_ID = 1000;
    public static final int NOTIFICATION_ID = 1001;
    public static final int PICKUP_POINTS_ID = 1002;
    public static final int ALARM_ID = 1003;
    public static final String CHILD_DATA = "child_data";


}