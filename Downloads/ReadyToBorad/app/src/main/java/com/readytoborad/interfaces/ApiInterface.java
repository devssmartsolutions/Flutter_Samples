package com.readytoborad.interfaces;

import com.readytoborad.database.ChildData;
import com.readytoborad.model.CommonChildResponse;
import com.readytoborad.model.DashBoardParser;
import com.readytoborad.model.DriverDashResponse;
import com.readytoborad.model.DriverLocationByChild;
import com.readytoborad.model.ResponseModel;
import com.readytoborad.model.CommonResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by harendrasinghbisht on 16/01/17.
 */

public interface ApiInterface {
    @FormUrlEncoded
    @POST("users/login")
    Call<ResponseModel> getuserlogin(@Field("username") String uname, @Field("password") String pass, @Field("device_token") String deviceToken, @Field("device_type") String deviceType, @Field("type") String userType);

    @FormUrlEncoded
    @POST("parents/updatelocation")
    Call<CommonResponse> submitLocation(@Header("Authorization") String basicCredentials, @Field("address") String address, @Field("latitude") String latitude, @Field("longitude") String longitude, @Field("city") String city, @Field("state") String state, @Field("pincode") String pincode);

    @FormUrlEncoded
    @POST("users/forgotpassword")
    Call<CommonResponse> forgotPassword(@Header("Authorization") String basicCredentials, @Field("username") String email);

    @FormUrlEncoded
    @POST("users/changepassword")
    Call<CommonResponse> changePassword(@Header("Authorization") String basicCredentials, @Field("password") String password, @Field("newPassword") String newPassword);

    @FormUrlEncoded
    @POST("parents/getdashboarddetails")
    Call<DashBoardParser> getDashboard(@Header("Authorization") String basicCredentials, @Field("username") String userid);


    @GET("driver/getallbuses")
    Call<DriverDashResponse> getallbuses(@Header("Authorization") String basicCredentials);


    @FormUrlEncoded
    @POST("driver/fetchallbuspickuppoints")
    Call<DriverDashResponse> fetchallbuspickuppoints(@Header("Authorization") String basicCredentials, @Field("bus_id") String bus_id);

    @FormUrlEncoded
    @POST("driver/endbustrip")
    Call<ResponseModel> endbustrip(@Header("Authorization") String basicCredentials, @Field("bus_id") String bus_id);

    @FormUrlEncoded
    @POST("/users/accessTokenLogin")
    Call<ResponseBody> getAccessToken(@Header("Authorization") String basicCredentials, @Field("device_token") String deviceToken, @Field("device_type") String deviceType);


    @GET("users/logout")
    Call<ResponseModel> logout(@Header("Authorization") String basicCredentials);

    @FormUrlEncoded
    @POST("driver/updateLocations")
    Call<ResponseModel> updateLocations(@Header("Authorization") String basicCredentials, @Field("driver_latitude") String driver_latitude, @Field("driver_longitude") String driver_longitude);


    @FormUrlEncoded
    @POST("parents/setuppickuppoint")
    Call<ResponseModel> setUppickupPoint(@Header("Authorization") String basicCredentials, @Body ChildData childData);

    @FormUrlEncoded
    @POST("parents/driverlocationbychildid")
    Call<DriverLocationByChild> driverLocationByChildId(@Header("Authorization") String basicCredentials, @Field("child_id") String child_id);

    @FormUrlEncoded
    @POST("/parents/updatechildalarmtime")
    Call<ResponseBody> updateChildAlarm(@Header("Authorization") String basicCredentials, @Field("child_id") String child_id, @Field("alarm_time") String alarm_time);

    @FormUrlEncoded
    @POST("parents/updatechildpickuppoint")
    Call<CommonResponse> updatePickupPoints(@Header("Authorization") String basicCredentials,@Field("child_id") String child_id, @Field("latitude") String latitue,@Field("longitude") String longitude,@Field("address") String address,@Field("city") String city,@Field("state") String state,@Field("pincode") String postalCode);

    @GET("/parents/getallchildrenalarmtime")
    Call<CommonChildResponse> getAllAlarm(@Header("Authorization") String basicCredentials);

    @GET("/parents/getallchildrenpickuppoints")
    Call<CommonChildResponse> getAllChildrenPickUpPoints(@Header("Authorization") String basicCredentials);

}
