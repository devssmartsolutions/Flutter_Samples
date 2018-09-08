package com.readytoborad.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.readytoborad.R;
import com.readytoborad.database.LocationData;
import com.readytoborad.interfaces.ApiInterface;
import com.readytoborad.model.BusModel;
import com.readytoborad.model.ChildLocation;
import com.readytoborad.model.DriverDashResponse;
import com.readytoborad.model.ResponseModel;
import com.readytoborad.network.ApiClient;
import com.readytoborad.service.FetchAddressIntentService;
import com.readytoborad.session.SessionManager;
import com.readytoborad.util.AppUtils;
import com.readytoborad.util.Constants;
import com.readytoborad.util.MyInfoWindowAdapter;
import com.readytoborad.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DriverMapFragment extends Fragment implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private View rootView;
    private boolean mPermissionDenied = false;
    private GoogleMap googleMap;
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    static Location mLastLocation;
    private String lat, lon;
    private Marker mCurrLocationMarker;
    private LocationData locationData;
    private AddressResultReceiver mResultReceiver;
    protected String mAddressOutput;
    protected boolean mAddressRequested;
    protected BusModel busModel;
    private DriverDashResponse driverDashResponse;
    private boolean map_display = false;
    private ApiInterface apiService;
    SessionManager sessionManager;
    SweetAlertDialog progressBar;
    boolean flag = false;
    int count = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.map_view, container, false);
        mContext = getActivity();
        mResultReceiver = new AddressResultReceiver(new Handler());
        mAddressRequested = false;
        mAddressOutput = "";

        sessionManager = new SessionManager(mContext);
        apiService = ApiClient.getClient().create(ApiInterface.class);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);
        //buildGoogleApiClient();


        driverDashResponse = getArguments().getParcelable("RouteInfo");


        return rootView;
    }

    private void showDailogFail(String message) {
        new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Alert")
                .setContentText("" + message)
                .setConfirmText("OK")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000); // Update location every second
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission((AppCompatActivity) mContext, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        } else if (googleMap != null) {
            // Access to the location has been granted to the app.
            // buildGoogleApiClient();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                lat = String.valueOf(mLastLocation.getLatitude());
                lon = String.valueOf(mLastLocation.getLongitude());
                Log.i("TAG", "Latitude=" + lat + "\nLongitude=" + lon);
                startIntentService();
//                updateDriverLocation();
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

       /* if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }*/
        mLastLocation = location;
        if (mLastLocation != null && count <=3 ) {
            startIntentService();
//            updateDriverLocation();
        }

    }

    private void setLocationMarker(Location location) {

        if(mCurrLocationMarker !=null)
            mCurrLocationMarker.remove();
        count++;
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("" + mAddressOutput);
        if (getActivity() != null)
            googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter(getActivity()));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bus));
        mCurrLocationMarker = googleMap.addMarker(markerOptions);
        // mCurrLocationMarker.showInfoWindow();
        //move map camera
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(15f).tilt(3).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        if (driverDashResponse != null) {
            List<ChildLocation> childLocations = driverDashResponse.getChildLocationsData();
            if (childLocations != null) {
                for (int i = 0; i < childLocations.size(); i++) {
                    try {


                        ChildLocation childLocation = childLocations.get(i);
                        if (childLocation.getLatitude() == null || childLocation.getLongitude() == null)
                            continue;
                        LatLng latLng1 = new LatLng(Double.parseDouble(childLocation.getLatitude()), Double.parseDouble(childLocation.getLongitude()));
                        MarkerOptions markerOptions1 = new MarkerOptions();
                        markerOptions1.position(latLng1);
                        markerOptions1.title("" + childLocation.getChild_name() + "\n" + " Address : " + childLocation.getAddress());
                        markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        googleMap.addMarker(markerOptions1);
                        CameraPosition build = new CameraPosition.Builder()
                                .target(latLng).zoom(15f).tilt(20).build();
                        googleMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(build));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }


    public boolean updateDriverLocation() {
        flag = false;
        if (mLastLocation != null) {
            String token = sessionManager.getKeyToken();


            showProgressDialog(mContext);


            Call<ResponseModel> dashBoardParserCall = apiService.updateLocations(
                    token, String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()));
            dashBoardParserCall.enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                    progressBar.dismiss();
                    if (response.body().getSuccess() == 1) {

                        AppUtils.showToast(mContext, response.body().getMessage());
                        setLocationMarker(mLastLocation);
                       flag = true;

                    } else {

                    }

                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                   progressBar.dismiss();
                }
            });
           // progressBar.dismiss();
        } else
            AppUtils.showToast(mContext, "Location not available");

        return flag;
    }

    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        enableMyLocation();
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        MarkerOptions markerOptions = new MarkerOptions();
        LatLng chdLatLng = new LatLng(30.7362900, 76.7884000);
        markerOptions.position(chdLatLng);
        if (getActivity() != null)
            googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter(getActivity()));
      //  mCurrLocationMarker = googleMap.addMarker(markerOptions);
        googleMap.setOnMyLocationButtonClickListener(this);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(chdLatLng).zoom(5f).tilt(3).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        enableMyLocation();

    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission((AppCompatActivity) mContext, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        } else if (googleMap != null) {
            // Access to the location has been granted to the app.
            buildGoogleApiClient();
            googleMap.setMyLocationEnabled(true);

        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
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
                mAddressOutput = "";
                mAddressOutput = TextUtils.join(System.getProperty("line.separator"), addressFragments);
            }
            setLocationMarker(mLastLocation);
            //  displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //    showToast(getString(R.string.address_found));
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
            //  updateUIWidgets();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
       /* if (requestCode != LOCATION_PERMISSION_REQUEST_CODE ) {
            return;
        }*/

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            startIntentService();
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    public void showProgressDialog(Context context) {

        progressBar = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Updating location...");
        progressBar.setCancelable(false);
        progressBar.show();

    }
}
/*
1. app name should be changed to ready2board
        2. login screen changes
        3. tab placements
        4. popup account
        5. first login set to one while loction is set (Himani)
        6. login successful popup should be transparent
        7. dashboard location progress bar
        8. children dropdown should be above tab
        9. profile menu should be in settings tab (ignore)
*/
