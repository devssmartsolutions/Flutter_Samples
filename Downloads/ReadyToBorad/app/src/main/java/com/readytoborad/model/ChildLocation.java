package com.readytoborad.model;

import android.os.Parcel;
import android.os.Parcelable;


public class ChildLocation implements Parcelable{
     int child_id;

    public int getChild_id() {
        return child_id;
    }

    public void setChild_id(int child_id) {
        this.child_id = child_id;
    }

    public String getChild_name() {
        return child_name;
    }

    public void setChild_name(String child_name) {
        this.child_name = child_name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    String child_name,latitude = null,longitude = null,address,city,state,pincode,bus_id,alarm_time;

    public String getBus_id() {
        return bus_id;
    }

    public void setBus_id(String bus_id) {
        this.bus_id = bus_id;
    }

    public String getAlarm_time() {
        return alarm_time;
    }

    public void setAlarm_time(String alarm_time) {
        this.alarm_time = alarm_time;
    }

    public static Creator<ChildLocation> getCREATOR() {
        return CREATOR;
    }

    protected ChildLocation(Parcel in) {
        child_id = in.readInt();
        child_name = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        address = in.readString();
        city = in.readString();
        state = in.readString();
        pincode = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(child_id);
        dest.writeString(child_name);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(pincode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChildLocation> CREATOR = new Creator<ChildLocation>() {
        @Override
        public ChildLocation createFromParcel(Parcel in) {
            return new ChildLocation(in);
        }

        @Override
        public ChildLocation[] newArray(int size) {
            return new ChildLocation[size];
        }
    };
}
