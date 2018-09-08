package com.readytoborad.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harendrasinghbisht on 04/02/17.
 */

public class StudentModel implements Parcelable{
    @SerializedName("student_name")
    @Expose
    private String studentName;
    @SerializedName("school_name")
    @Expose
    private String schoolName;
    @SerializedName("tagline")
    @Expose
    private String tagline;
    @SerializedName("school_address")
    @Expose
    private String schoolAddress;
    @SerializedName("school_country")
    @Expose
    private String schoolCountry;
    @SerializedName("school_state")
    @Expose
    private String schoolState;
    @SerializedName("school_pincode")
    @Expose
    private String schoolPincode;
    @SerializedName("school_longitude")
    @Expose
    private String schoolLongitude;
    @SerializedName("school_latitude")
    @Expose
    private String schoolLatitude;
    @SerializedName("school_city")
    @Expose
    private String schoolCity;
    @SerializedName("school_landline")
    @Expose
    private String schoolLandline;
    @SerializedName("school_landline1")
    @Expose
    private String schoolLandline1;
    @SerializedName("school_logo")
    @Expose
    private String schoolLogo;
    @SerializedName("bus_no")
    @Expose
    private String busNo;
    @SerializedName("bus_longitude")
    @Expose
    private String busLongitude;
    @SerializedName("bus_latitude")
    @Expose
    private String busLatitude;
    @SerializedName("driver_name")
    @Expose
    private String driverName;
    @SerializedName("driver_number")
    @Expose
    private String driverNumber;

    protected StudentModel(Parcel in) {
        studentName = in.readString();
        schoolName = in.readString();
        tagline = in.readString();
        schoolAddress = in.readString();
        schoolCountry = in.readString();
        schoolState = in.readString();
        schoolPincode = in.readString();
        schoolLongitude = in.readString();
        schoolLatitude = in.readString();
        schoolCity = in.readString();
        schoolLandline = in.readString();
        schoolLandline1 = in.readString();
        schoolLogo = in.readString();
        busNo = in.readString();
        busLongitude = in.readString();
        busLatitude = in.readString();
        driverName = in.readString();
        driverNumber = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(studentName);
        dest.writeString(schoolName);
        dest.writeString(tagline);
        dest.writeString(schoolAddress);
        dest.writeString(schoolCountry);
        dest.writeString(schoolState);
        dest.writeString(schoolPincode);
        dest.writeString(schoolLongitude);
        dest.writeString(schoolLatitude);
        dest.writeString(schoolCity);
        dest.writeString(schoolLandline);
        dest.writeString(schoolLandline1);
        dest.writeString(schoolLogo);
        dest.writeString(busNo);
        dest.writeString(busLongitude);
        dest.writeString(busLatitude);
        dest.writeString(driverName);
        dest.writeString(driverNumber);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StudentModel> CREATOR = new Creator<StudentModel>() {
        @Override
        public StudentModel createFromParcel(Parcel in) {
            return new StudentModel(in);
        }

        @Override
        public StudentModel[] newArray(int size) {
            return new StudentModel[size];
        }
    };

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getSchoolAddress() {
        return schoolAddress;
    }

    public void setSchoolAddress(String schoolAddress) {
        this.schoolAddress = schoolAddress;
    }

    public String getSchoolCountry() {
        return schoolCountry;
    }

    public void setSchoolCountry(String schoolCountry) {
        this.schoolCountry = schoolCountry;
    }

    public String getSchoolState() {
        return schoolState;
    }

    public void setSchoolState(String schoolState) {
        this.schoolState = schoolState;
    }

    public String getSchoolPincode() {
        return schoolPincode;
    }

    public void setSchoolPincode(String schoolPincode) {
        this.schoolPincode = schoolPincode;
    }

    public String getSchoolLongitude() {
        return schoolLongitude;
    }

    public void setSchoolLongitude(String schoolLongitude) {
        this.schoolLongitude = schoolLongitude;
    }

    public String getSchoolLatitude() {
        return schoolLatitude;
    }

    public void setSchoolLatitude(String schoolLatitude) {
        this.schoolLatitude = schoolLatitude;
    }

    public String getSchoolCity() {
        return schoolCity;
    }

    public void setSchoolCity(String schoolCity) {
        this.schoolCity = schoolCity;
    }

    public String getSchoolLandline() {
        return schoolLandline;
    }

    public void setSchoolLandline(String schoolLandline) {
        this.schoolLandline = schoolLandline;
    }

    public String getSchoolLandline1() {
        return schoolLandline1;
    }

    public void setSchoolLandline1(String schoolLandline1) {
        this.schoolLandline1 = schoolLandline1;
    }

    public String getSchoolLogo() {
        return schoolLogo;
    }

    public void setSchoolLogo(String schoolLogo) {
        this.schoolLogo = schoolLogo;
    }

    public String getBusNo() {
        return busNo;
    }

    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }

    public String getBusLongitude() {
        return busLongitude;
    }

    public void setBusLongitude(String busLongitude) {
        this.busLongitude = busLongitude;
    }

    public String getBusLatitude() {
        return busLatitude;
    }

    public void setBusLatitude(String busLatitude) {
        this.busLatitude = busLatitude;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverNumber() {
        return driverNumber;
    }

    public void setDriverNumber(String driverNumber) {
        this.driverNumber = driverNumber;
    }
}
