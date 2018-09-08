package com.readytoborad.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.readytoborad.R;
import com.readytoborad.customviews.DistanceView;
import com.readytoborad.customviews.DrawView;
import com.readytoborad.interfaces.RouteInterface;
import com.readytoborad.model.StudentModel;
import com.readytoborad.service.DistanceFetchService;
import com.readytoborad.service.FetchAddressIntentService;
import com.readytoborad.session.SessionManager;
import com.readytoborad.util.Constants;
import com.readytoborad.util.PermissionUtils;
import com.readytoborad.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static java.lang.Double.parseDouble;


public class RouteViewFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ActivityCompat.OnRequestPermissionsResultCallback, RouteInterface {
    private View rootView;
    // private ProgressBar distanceProgress;
    private ProgressBar progressBar;
    private TextView textCurrentLocation, textReaminingTime;

    //location
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private String lat, lon;
    private boolean mPermissionDenied = false;
    private AddressResultReceiver mResultReceiver;
    private DistanceResultReceiver mDistanceReceiver;
    private boolean mAddressRequested;
    protected String mAddressOutput;
    // private String token;
   // private ApiInterface apiService;
   // private SweetAlertDialog pDialog;
    private TextView text_home, text_school;


    private SessionManager sessionManager;
    private LatLng source, destination;
    private String totaltime;
    private LatLng currentLocation;
    private Long total_distance = 0L, current_distance = 0L;
    private RelativeLayout trackview;
    private int lengthprogrssbar;
    private DrawView viewDraw;
    private DistanceView distanceView;
    public static RouteInterface routeInterface;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.route_view, container, false);
        mResultReceiver = new AddressResultReceiver(new Handler());
        mDistanceReceiver = new DistanceResultReceiver(new Handler());
        sessionManager = new SessionManager(getActivity());
        routeInterface=this;
        init();
        enableLocation();
        //getDashboard();

        return rootView;
    }

    private void init() {
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress);
        textCurrentLocation = (TextView) rootView.findViewById(R.id.textCurrentLocation);
        textReaminingTime = (TextView) rootView.findViewById(R.id.textReaminingTime);
        viewDraw = (DrawView) rootView.findViewById(R.id.viewDraw);
        distanceView = (DistanceView) rootView.findViewById(R.id.distanceView);
        text_home = (TextView) rootView.findViewById(R.id.text_home);
        text_school = (TextView) rootView.findViewById(R.id.text_school);

        trackview = (RelativeLayout) rootView.findViewById(R.id.trackview);
        getAub();
        // LocationData locationData = LocationData.findById(LocationData.class, 1);
        textCurrentLocation.setText("Getting location please wait...");
        progressBar.setVisibility(View.VISIBLE);
       /* viewDraw.postDelayed(new Runnable() {

            @Override
            public void run() {

                viewDraw.invalidate();
                System.out.println("Height yourView: " + viewDraw.getHeight());
                System.out.println("Width yourView: " + viewDraw.getWidth());
            }
        }, 1);*/

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10 * 1000); // Update location every second
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        } else {
            // Access to the location has been granted to the app.
            // buildGoogleApiClient();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                lat = String.valueOf(mLastLocation.getLatitude());
                lon = String.valueOf(mLastLocation.getLongitude());
                Log.i("TAG", "Latitude=" + lat + "\nLongitude=" + lon);
                startIntentService();
            }

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());
        Log.i("TAG", "Latitude=" + lat + "\nLongitude=" + lon);

        if (mGoogleApiClient == null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        mLastLocation = location;
        if (mLastLocation != null)
            startIntentService();

    }

    /*
    * Enables the My Location layer if the fine location permission has been granted.
    */
    private void enableLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        } else {
            // Access to the location has been granted to the app.
            buildGoogleApiClient();

        }
    }

    protected void startIntentService() {
        if (getActivity() != null) {
            // Create an intent for passing to the intent service responsible for fetching the address.
            Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);

            // Pass the result receiver as an extra to the service.
            intent.putExtra(Constants.RECEIVER, mResultReceiver);

            // Pass the location data as an extra to the service.
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

            // Start the service. If the service isn't already running, it is instantiated and started
            // (creating a process for it if needed); if it is running then it remains running. The
            // service kills itself automatically once all intents are processed.
            getActivity().startService(intent);
        }
    }

    @Override
    public void changeChild(StudentModel studentModel) {
        // StudentModel studentModel = studentModelList.get(position);
        // text_home.setText(sessionManager.);
        text_school.setText(studentModel.getSchoolAddress());
        //LatLng source = new LatLng(parseDouble(sessionManager.getKeyLatitude()), parseDouble(sessionManager.getKeyLongitude()));
        LatLng source = new LatLng(parseDouble("30.7294"), parseDouble("76.8446"));
        destination = new LatLng(Double.parseDouble(studentModel.getSchoolLatitude()), Double.parseDouble(studentModel.getSchoolLongitude()));
        startIntentService1(source, destination, 0);

    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            List<Address> addressList = resultData.getParcelableArrayList(Constants.RESULT_DATA_KEY);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                }
                lat = "" + address.getLatitude();
                lon = "" + address.getLongitude();
                currentLocation = new LatLng(address.getLatitude(), address.getLongitude());
                mAddressOutput = "";
                mAddressOutput = TextUtils.join(System.getProperty("line.separator"), addressFragments);
                if (destination != null) {
                    startIntentService1(currentLocation, destination, 1);
                }
            }

            //  displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                progressBar.setVisibility(View.GONE);
                textCurrentLocation.setText("" + mAddressOutput);
                //    showToast(getString(R.string.address_found));
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
            //  updateUIWidgets();
        }
    }

    protected void startIntentService1(LatLng source, LatLng destination, int total) {
        if (getActivity() != null) {
            // Create an intent for passing to the intent service responsible for fetching the address.
            Intent intent = new Intent(getActivity(), DistanceFetchService.class);

            // Pass the result receiver as an extra to the service.
            intent.putExtra(Constants.RECEIVER, mDistanceReceiver);

            // Pass the location data as an extra to the service.
            intent.putExtra(Constants.LOCATION_SOURCE_EXTRA, source);
            intent.putExtra(Constants.LOCATION_DESTINATION_EXTRA, destination);
            intent.putExtra(Constants.DATA_TRACK, total);

            // Start the service. If the service isn't already running, it is instantiated and started
            // (creating a process for it if needed); if it is running then it remains running. The
            // service kills itself automatically once all intents are processed.
            getActivity().startService(intent);
        }
    }

    class DistanceResultReceiver extends ResultReceiver {
        public DistanceResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the distance and time string or an error message sent from the intent service.

            String data = resultData.getString(Constants.RESULT_DATA_KEY);
            int track = resultData.getInt(Constants.DATA_TRACK);
            try {
                JSONObject jsonObject = null;
                if (data != null) {
                    jsonObject = new JSONObject(data);
                }
                // Show a toast message if an address was found.
                if (resultCode == Constants.SUCCESS_RESULT) {
                    if (track == 0) {
                        totaltime = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getString("text");
                        // textReaminingTime.setText("Remaining Time\n" + jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getString("text"));
                        total_distance = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").getLong("value");
                        viewDraw.setMaxDistance(total_distance);
                        distanceView.setMaxDistance(total_distance);
                    } else {
                        totaltime = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getString("text");
                        current_distance = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").getLong("value");
                    }
                    textReaminingTime.setText("Remaining Time\n" + totaltime);
                    if (total_distance != 0 && current_distance != 0) {
                        viewDraw.setProgress(total_distance - current_distance);
                        distanceView.setProgress(total_distance - current_distance);
                        /*long progress = (long) ((total_distance - current_distance) * 100 * 1.0) / total_distance;
                       // distanceProgress.setProgress((int) progress);
                        distanceProgress.getProgressDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.login), PorterDuff.Mode.MULTIPLY);
                        int percent = (lengthprogrssbar * (int) progress) / 100;
                        percent = percent + (int) getResources().getDimension(R.dimen._65sdp);
                        Log.i("Percent", "value=" + percent);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(percent, ViewGroup.LayoutParams.WRAP_CONTENT);
                        trackview.setLayoutParams(layoutParams);*/
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            buildGoogleApiClient();

        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

   /* private void getDashboard() {
        pDialog.show();
        destination = null;
        Call<DashBoardParser> dashBoardParserCall = apiService.getDashboard(new SessionManager(getActivity()).getKeyToken(), sessionManager.getKeyFatherName());
        dashBoardParserCall.enqueue(new Callback<DashBoardParser>() {
            @Override
            public void onResponse(Call<DashBoardParser> call, Response<DashBoardParser> response) {
                pDialog.dismiss();
                if (response.body().getSuccess() == 1) {
                    studentModelList = new ArrayList<StudentModel>();
                    studentModelList.addAll(response.body().getData());
                    setSpinnerAdapter();
                } else {
                    showDailog(response.body().getMessage(), response.body().getSuccess());
                }

            }

            @Override
            public void onFailure(Call<DashBoardParser> call, Throwable t) {
                pDialog.dismiss();
            }
        });
    }*/

   /* private void setSpinnerAdapter() {
        studentNameList = new ArrayList<>();
        for (int i = 0; i < studentModelList.size(); i++) {
            studentNameList.add(studentModelList.get(i).getStudentName());

        }
        ArrayAdapter<String> a = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, studentNameList);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        child_spinner.setAdapter(a);
    }*/

    private void setInfodetails(int position) {


    }

    private void showDailog(String message, final int type) {
        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Alert")
                .setContentText("" + message)
                .setConfirmText("OK")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        if (type == 400)
                            Util.getUserLogout(getActivity());
                        else
                            sDialog.dismiss();
                    }
                })
                .show();
    }

    private void getAub() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        lengthprogrssbar = displayMetrics.widthPixels;
        Log.i("WIDTH", "X=" + lengthprogrssbar);

    }

}
