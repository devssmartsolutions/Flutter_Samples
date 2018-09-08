package com.readytoborad.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "child_id",
        "child_name",
        "bus_id",
        "alarm_time",
        "latitude",
        "longitude",
        "address",
        "city",
        "state",
        "pincode"
})
@Entity
public class ChildData implements Serializable{
    @PrimaryKey
    @JsonProperty("child_id")
    @NonNull
    @ColumnInfo(name = "childId")
    private String childId;
    @JsonProperty("child_name")
    @ColumnInfo(name = "childname")
    private String childName;
    @JsonProperty("bus_id")
    @ColumnInfo(name = "busid")
    private String busId;
    @JsonProperty("alarm_time")
    @ColumnInfo(name = "alarmTime")
    private String alarmTime;
    @JsonProperty("latitude")
    @ColumnInfo(name = "latitude")
    private String latitude;
    @JsonProperty("longitude")
    @ColumnInfo(name = "longitude")
    private String longitude;
    @JsonProperty("address")
    @ColumnInfo(name = "address")
    private String address;
    @JsonProperty("city")
    @ColumnInfo(name = "city")
    private String city;
    @JsonProperty("state")
    @ColumnInfo(name = "state")
    private String state;
    @JsonProperty("pincode")
    @ColumnInfo(name = "pincode")
    private String pincode;



    @JsonProperty("child_id")
    public String getChildId() {
        return childId;
    }

    @JsonProperty("child_id")
    public void setChildId(String childId) {
        this.childId = childId;
    }

    @JsonProperty("latitude")
    public String getLatitude() {
        return latitude;
    }

    @JsonProperty("latitude")
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("longitude")
    public String getLongitude() {
        return longitude;
    }

    @JsonProperty("longitude")
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    @JsonProperty("address")
    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    @JsonProperty("city")
    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("state")
    public String getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("pincode")
    public String getPincode() {
        return pincode;
    }

    @JsonProperty("pincode")
    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    @JsonProperty("child_name")
    public String getChildName() {
        return childName;
    }

    @JsonProperty("child_name")
    public void setChildName(String childName) {
        this.childName = childName;
    }

    @JsonProperty("bus_id")
    public String getBusId() {
        return busId;
    }

    @JsonProperty("bus_id")
    public void setBusId(String busId) {
        this.busId = busId;
    }

    @JsonProperty("alarm_time")
    public String getAlarmTime() {
        return alarmTime;
    }

    @JsonProperty("alarm_time")
    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }


}


