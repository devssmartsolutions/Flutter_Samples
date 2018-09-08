package com.readytoborad.model;

/**
 * Created by anchal.kumar on 1/24/2018.
 */

public class DriverLocationByChild {

    String driver_name,driver_no,driver_initial_latitude,driver_initial_longitude,driver_current_latitude
    ,driver_current_longitude,school_latitude,school_longitude,trip_type,pickup_status,message,childID;

    int success;

    public String getChildID() {
        return childID;
    }

    public void setChildID(String childID) {
        this.childID = childID;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getDriver_name() {

        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getDriver_no() {
        return driver_no;
    }

    public void setDriver_no(String driver_no) {
        this.driver_no = driver_no;
    }

    public String getDriver_initial_latitude() {
        return driver_initial_latitude;
    }

    public void setDriver_initial_latitude(String driver_initial_latitude) {
        this.driver_initial_latitude = driver_initial_latitude;
    }

    public String getDriver_initial_longitude() {
        return driver_initial_longitude;
    }

    public void setDriver_initial_longitude(String driver_initial_longitude) {
        this.driver_initial_longitude = driver_initial_longitude;
    }

    public String getDriver_current_latitude() {
        return driver_current_latitude;
    }

    public void setDriver_current_latitude(String driver_current_latitude) {
        this.driver_current_latitude = driver_current_latitude;
    }

    public String getDriver_current_longitude() {
        return driver_current_longitude;
    }

    public void setDriver_current_longitude(String driver_current_longitude) {
        this.driver_current_longitude = driver_current_longitude;
    }

    public String getSchool_latitude() {
        return school_latitude;
    }

    public void setSchool_latitude(String school_latitude) {
        this.school_latitude = school_latitude;
    }

    public String getSchool_longitude() {
        return school_longitude;
    }

    public void setSchool_longitude(String school_longitude) {
        this.school_longitude = school_longitude;
    }

    public String getTrip_type() {
        return trip_type;
    }

    public void setTrip_type(String trip_type) {
        this.trip_type = trip_type;
    }

    public String getPickup_status() {
        return pickup_status;
    }

    public void setPickup_status(String pickup_status) {
        this.pickup_status = pickup_status;
    }
}
