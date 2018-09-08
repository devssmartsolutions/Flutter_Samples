package com.readytoborad.util;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.readytoborad.activity.DashboardActivity;
import com.readytoborad.R;
import com.readytoborad.interfaces.ApiInterface;
import com.readytoborad.map.CustomMap_Fragment;
import com.readytoborad.map.EventBus_Poster;
import com.readytoborad.map.EventBus_Singleton;
import com.readytoborad.model.CommonResponse;
import com.readytoborad.network.ApiClient;
import com.readytoborad.service.FetchAddressIntentService;
import com.readytoborad.session.SessionManager;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by harendrasinghbisht on 28/02/17.
 */

public class LocationSetupActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
        , GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener {
    private GoogleMap googleMap;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String TAG = "MAP LOCATION";
    Context mContext;
    private String country, city, state, pincode;
    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;
    /**
     * The formatted location address.
     */
    protected String mAddressOutput;
    protected String mAreaOutput;
    private TextView mTitle, locationMarkertext;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Toolbar mToolbar;
    private Location mLastLocation;
    private String lat, lon;
    private boolean isLocation = false;
    private ApiInterface apiService;
    private SweetAlertDialog pDialog;
    private Button buttonSubmit;
    private RelativeLayout main_screen, setmain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   Util.statuBarColor(this, R.color.light, true);
        setContentView(R.layout.location_setup);
        mContext = this;
       /*mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) findViewById(R.id.toolbar_title);
        mTitle.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_search, 0, 0, 0);
        setTextViewDrawableColor(mTitle);
        mTitle.setText(getResources().getString(R.string.setlocation));
        mTitle.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.common_google_signin_btn_text_light_default)));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        */


        mResultReceiver = new AddressResultReceiver(new Handler());
       mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAutocompleteActivity();

            }
        });

        init();
        showDialog();
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitLocation();
            }
        });

    }

    private void setTextViewDrawableColor(TextView textView) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mContext, R.color.gray_btn_bg_color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    private void init() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Loading");
        pDialog.setCancelable(false);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        // final CustomMapFragment mapFragment = (CustomMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_picker);
        // mapFragment.setOnDragListener(LocationSetupActivity.this);
        // mapFragment.getMapAsync(this);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        main_screen = (RelativeLayout) findViewById(R.id.main_screen);
        setmain = (RelativeLayout) findViewById(R.id.setmain);
        locationMarkertext = (TextView) findViewById(R.id.locationMarkertext);
        final CustomMap_Fragment mMapFragment = (CustomMap_Fragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // init();
        EventBus_Singleton.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus_Singleton.getInstance().unregister(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnCameraIdleListener(this);
        this.googleMap.setOnCameraMoveStartedListener(this);
        this.googleMap.setOnCameraMoveListener(this);
        this.googleMap.setOnCameraMoveCanceledListener(this);
        this.googleMap.getUiSettings().setZoomGesturesEnabled(false);
       // this.googleMap.setOnMapLoadedCallback(this);
        enableLocation();

    }

    @Subscribe
    public void eventBus_ListenerMethod(EventBus_Poster event) {
        if (event.dtap != null)
            this.googleMap.getUiSettings().setZoomGesturesEnabled(false);

        //Construct a CameraUpdate object that will zoom into the exact middle of the map, with a zoom of currentCameraZoom + 1 unit
        CameraUpdate zoomInUpdate = CameraUpdateFactory.zoomIn();
        //Run that with a speed of 400 ms.
        if (googleMap != null)
            googleMap.animateCamera(zoomInUpdate, 400, null);//Toast.makeText(mContext,"zooming",Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(LocationSetupActivity.this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                lat = String.valueOf(mLastLocation.getLatitude());
                lon = String.valueOf(mLastLocation.getLongitude());
                Log.i("TAG", "Latitude=" + lat + "\nLongitude=" + lon);
                startIntentService(mLastLocation);

            }

        }

    }

    private void setCurrentLocation(Location location) {
        /*if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }*/
        final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(15f).tilt(50).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));


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
        if (mLastLocation != null) {
            if (!isLocation) {
                isLocation = true;
                startIntentService(mLastLocation);
                setCurrentLocation(mLastLocation);
                enableLocation();
            }
        }

    }

    /*
 * Enables the My Location layer if the fine location permission has been granted.
 */
    private void enableLocation() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(LocationSetupActivity.this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        } else {
            // Access to the location has been granted to the app.
            if (googleMap != null)
                googleMap.setMyLocationEnabled(true);
            buildGoogleApiClient();

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
            //if (googleMap != null)
            // googleMap.setMyLocationEnabled(true);
            buildGoogleApiClient();

        } else {
            // Display the missing permission error dialog when the fragments resume.
            // mPermissionDenied = true;
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

    @Override
    public void onCameraIdle() {
        /*this.googleMap.getUiSettings().setZoomGesturesEnabled(true);
        LatLng center = googleMap.getCameraPosition().target;
        mLastLocation.setLatitude(center.latitude);
        mLastLocation.setLongitude(center.longitude);
        CameraUpdate zoomInUpdate = CameraUpdateFactory.zoomIn();
        //Run that with a speed of 400 ms.
        if (googleMap != null)
            googleMap.animateCamera(zoomInUpdate, 400, null);
        startIntentService(mLastLocation);*/

    }

    @Override
    public void onCameraMoveCanceled() {


    }

    @Override
    public void onCameraMove() {
       /* LatLng center = googleMap.getCameraPosition().target;
        mLastLocation.setLatitude(center.latitude);
        mLastLocation.setLongitude(center.longitude);*/
        // startIntentService(mLastLocation);
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            this.googleMap.getUiSettings().setZoomGesturesEnabled(true);
            LatLng center = googleMap.getCameraPosition().target;

            mLastLocation.setLatitude(center.latitude);
            mLastLocation.setLongitude(center.longitude);
            CameraUpdate zoomInUpdate = CameraUpdateFactory.zoomIn();
            //Run that with a speed of 400 ms.
            if (googleMap != null)
                googleMap.animateCamera(zoomInUpdate, 400, null);
            startIntentService(mLastLocation);

        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_API_ANIMATION) {
            LatLng center = googleMap.getCameraPosition().target;
            mLastLocation.setLatitude(center.latitude);
            mLastLocation.setLongitude(center.longitude);
            CameraUpdate zoomInUpdate = CameraUpdateFactory.zoomIn();
            //Run that with a speed of 400 ms.
            if (googleMap != null)
                googleMap.animateCamera(zoomInUpdate, 400, null);
            startIntentService(mLastLocation);
             // Toast.makeText(this, "The user tapped something on the map.", Toast.LENGTH_SHORT).show();
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_DEVELOPER_ANIMATION) {
             // Toast.makeText(this, "The app moved the camera.", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
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
                country = address.getCountryName();
                state = address.getAdminArea();
                city = address.getLocality();
                if (city == null)
                    city = address.getSubAdminArea();
                pincode = address.getPostalCode();
                lat = "" + address.getLatitude();
                lon = "" + address.getLongitude();
                mAddressOutput = "";
                // doColorSpanString(Util.checkNull(addressFragments.get(0))+" "+Util.checkNull(addressFragments.get(1)),Util.checkNull(addressFragments.get(2)),mTitle);
                mAddressOutput = TextUtils.join(System.getProperty("line.separator"), addressFragments);
                buttonSubmit.setEnabled(true);
            }
            Log.i("City", "" + city);
            Log.i("state", "" + state);
            Log.i("pincode", "" + pincode);
            Log.i("country", "" + country);
            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //    showToast(getString(R.string.address_found));
                // progressBar.setVisibility(View.GONE);
                // text_loginsuccess.setVisibility(View.VISIBLE);
//                mTitle.setText(mAddressOutput);
//                locationMarkertext.setText(mAddressOutput);

            }


        }

    }

    /**
     * Updates the address in the UI.
     */
    protected void displayAddressOutput() {
        //  mLocationAddressTextView.setText(mAddressOutput);
        try {
            if (mAreaOutput != null)
                // mLocationText.setText(mAreaOutput+ "");

                mTitle.setText(mAddressOutput);
            //mLocationText.setText(mAreaOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService(Location mLocation) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }


    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(mContext, data);

                // TODO call location based filter
                LatLng latLong;
                latLong = place.getLatLng();
                mLastLocation.setLatitude(latLong.latitude);
                mLastLocation.setLongitude(latLong.longitude);
                startIntentService(mLastLocation);

                //  mTitle.setText(place.getAddress() + "");

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLong).zoom(15f).tilt(70).build();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
            }

        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(mContext, data);
        } else if (resultCode == RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

        }
        return super.onOptionsItemSelected(item);
    }

    private void submitLocation() {
        if (city != null && !city.isEmpty()) {
            pDialog.show();
            final SessionManager sessionManager = new SessionManager(mContext);
            Log.i("Authoriztion", "" + sessionManager.getKeyToken());
            Call<CommonResponse> submitResponse = apiService.submitLocation(sessionManager.getKeyToken(), mAddressOutput, lat, lon, city, state, pincode);
            submitResponse.enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                    pDialog.dismiss();
                    try {
                        Log.i("TAG ", "" + response.body().getSuccess());
                        if (response.body().getSuccess() == 1) {
                            sessionManager.setKeyFirstLogin("1");
                            sessionManager.setKeyLatitude(lat);
                            sessionManager.setKeyLongitude(lon);
                            startActivity(new Intent(LocationSetupActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            showDailog(response.body().getMessage(), response.body().getSuccess());

                            // finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<CommonResponse> call, Throwable t) {
                    pDialog.dismiss();
                    Log.e("TAG ERRRor ", "" + t.toString());

                }
            });
        }
    }

    private void showDailog(String message, final int type) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Alert")
                .setContentText("" + message)
                .setConfirmText("OK")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        if (type == 400)
                            Util.getUserLogout(LocationSetupActivity.this);
                        else
                            sDialog.dismiss();
                    }
                })
                .show();
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.location_display);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        DisplayMetrics metrics = new DisplayMetrics(); //get metrics of screen
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        wlp.height = (int) (metrics.widthPixels * 0.8);
        wlp.width = (int) (metrics.widthPixels * 0.8);
        wlp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(wlp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button continueBtn = (Button) dialog.findViewById(R.id.continueBtn);
        TextView textView = (TextView) dialog.findViewById(R.id.textView);
        TextView textView1 = (TextView) dialog.findViewById(R.id.textView1);
        textView.setText(getResources().getString(R.string.ls_sucess));
        textView1.setText(getResources().getString(R.string.login_success));
        // doColorSpanString(getResources().getString(R.string.ls_sucess), getResources().getString(R.string.login_success), text_login);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                main_screen.setVisibility(View.VISIBLE);
                setmain.setVisibility(View.GONE);
                // setmain.setBackgroundColor(ContextCompat.getColor(mContext, R.color.light_transparent));
            }
        });
        dialog.show();

    }

    private void doColorSpanString(String firstString,
                                   String lastString, TextView txtSpan) {

        String totalString = firstString + "\n" + lastString;
        Spannable spanText = new SpannableString(totalString);
        spanText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.black)), 0
                , firstString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spanText.setSpan(new RelativeSizeSpan(1.2f), 0, firstString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spanText.setSpan(new StyleSpan(Typeface.NORMAL), firstString.length(),
                totalString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spanText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.grey)), firstString.length(),
                totalString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spanText.setSpan(new RelativeSizeSpan(1f), firstString.length(),
                totalString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        txtSpan.setText(spanText);
    }
}
