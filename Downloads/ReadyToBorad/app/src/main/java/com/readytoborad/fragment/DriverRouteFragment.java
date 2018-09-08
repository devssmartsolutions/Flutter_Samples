package com.readytoborad.fragment;

import android.Manifest;
import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.readytoborad.R;
import com.readytoborad.adapter.ChidlLocationRecycleAdapter;
import com.readytoborad.model.AddressModel;
import com.readytoborad.model.DriverDashResponse;
import com.readytoborad.service.FetchAddressIntentService;
import com.readytoborad.util.Constants;
import com.readytoborad.util.DividerItemDecoration;
import com.readytoborad.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by harendrasinghbisht on 26/01/17.
 */

public class DriverRouteFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private View rootView;
    private RecyclerView route_list;
    private ChidlLocationRecycleAdapter addressRecycleAdapter;
    private List<AddressModel> addressModelList;
    //Location
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private String lat, lon;
    private boolean mPermissionDenied = false;
    private AddressResultReceiver mResultReceiver;
    private boolean mAddressRequested;
    protected String mAddressOutput;
    private DriverDashResponse driverDashResponse;
    Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_driver_route, container, false);
        mResultReceiver = new AddressResultReceiver(new Handler());
        driverDashResponse = getArguments().getParcelable("RouteInfo");
        init();
        enableLocation();
        return rootView;
    }

    private void init() {

        mContext = getContext();
        route_list = (RecyclerView) rootView.findViewById(R.id.route_list);
        /*route_list.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {


                BusModel busModel = driverDashResponse.getData().get(pos);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder.setTitle(mContext.getResources().getString(R.string.msg_alert_bus_select));
                alertDialogBuilder
                        .setMessage(busModel.getBusNo())
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                        .setNegativeButton("No", null);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        }));*/
        setAddressList();

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
        mLocationRequest.setInterval(1000); // Update location every second
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
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

        if (mGoogleApiClient != null) {
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
            PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
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

            //  displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                // progressBar.setVisibility(View.GONE);
                // textCurrentLocation.setText("" + mAddressOutput);
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


    private void setAddressList() {
        // prepareMovieData();
        addressRecycleAdapter = new ChidlLocationRecycleAdapter(driverDashResponse.getChildLocationsData());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        route_list.setLayoutManager(mLayoutManager);
        route_list.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        route_list.setItemAnimator(new DefaultItemAnimator());
        route_list.setAdapter(addressRecycleAdapter);
    }
}
