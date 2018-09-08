package com.readytoborad.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vicky Garg on 6/11/2017.
 */

public class BusModel implements Parcelable {

    @SerializedName("bus_id")
    @Expose
    int busId;

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public String getBusNo() {
        return busNo;
    }

    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }

    @SerializedName("bus_no")
    @Expose
    String busNo;

    protected BusModel(Parcel in) {
        busId = in.readInt();
        busNo = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(busId);
        dest.writeString(busNo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BusModel> CREATOR = new Creator<BusModel>() {
        @Override
        public BusModel createFromParcel(Parcel in) {
            return new BusModel(in);
        }

        @Override
        public BusModel[] newArray(int size) {
            return new BusModel[size];
        }
    };
}
