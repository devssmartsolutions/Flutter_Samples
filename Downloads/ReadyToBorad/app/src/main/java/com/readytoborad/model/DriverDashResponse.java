package com.readytoborad.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by harendrasinghbisht on 08/03/17.
 */

public class DriverDashResponse implements Parcelable{
    @SerializedName("success")
    @Expose
    private int success;
    @SerializedName("message")
    @Expose
    private String message;



    @SerializedName("busesArr")
    @Expose
    private List<BusModel> data = null;

    @SerializedName("childrenLocationArr")
    @Expose
    private List<ChildLocation> childLocationsData = null;


    protected DriverDashResponse(Parcel in) {
        success = in.readInt();
        message = in.readString();
        data = in.createTypedArrayList(BusModel.CREATOR);
        childLocationsData = in.createTypedArrayList(ChildLocation.CREATOR);
    }

    public static final Creator<DriverDashResponse> CREATOR = new Creator<DriverDashResponse>() {
        @Override
        public DriverDashResponse createFromParcel(Parcel in) {
            return new DriverDashResponse(in);
        }

        @Override
        public DriverDashResponse[] newArray(int size) {
            return new DriverDashResponse[size];
        }
    };



    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<BusModel> getData() {
        return data;
    }

    public void setData(List<BusModel> data) {
        this.data = data;
    }

    public List<ChildLocation> getChildLocationsData() {
        return childLocationsData;
    }

    public void setChildLocationsData(List<ChildLocation> data) {
        this.childLocationsData = childLocationsData;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(success);
        dest.writeString(message);
        dest.writeTypedList(data);
        dest.writeTypedList(childLocationsData);
    }
}
