package com.readytoborad.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.ResultReceiver;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.readytoborad.R;
import com.readytoborad.adapter.PlaceAutocompleteAdapter;
import com.readytoborad.database.ChildData;
import com.readytoborad.database.MySharedPreferences;
import com.readytoborad.database.respository.ChildDataRepository;
import com.readytoborad.interfaces.ApiInterface;
import com.readytoborad.interfaces.DialogCallBack;
import com.readytoborad.map.CustomMapFragment;
import com.readytoborad.map.MapWrapperLayout;
import com.readytoborad.model.CommonResponse;
import com.readytoborad.service.FetchAddressIntentService;
import com.readytoborad.util.AppUtils;
import com.readytoborad.util.Constants;
import com.readytoborad.util.PermissionUtils;
import com.readytoborad.util.Util;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.readytoborad.util.Constants.SET_LOCATION_REQUEST;


public class SetLocationActivity extends BaseActivity implements View.OnClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback, MapWrapperLayout.OnDragListener, DialogCallBack {
    private static final String TAG = SetLocationActivity.class.getSimpleName();
    private Context mContext;
    //location
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LatLng mLastLocation;
    private String lat, lon;
    private boolean mPermissionDenied = false;
    private boolean isLocation = false;
    private AddressResultReceiver mResultReceiver;
    private boolean mAddressRequested;
    protected String mAddressOutput;
    //map
    @BindView(R.id.marker_icon_parent)
    View mMarkerParentView;
    private Marker mCurrLocationMarker;
    @BindView(R.id.main_screen)
    RelativeLayout mainScreenRelativeLayout;
    @BindView(R.id.setmain)
    RelativeLayout setMainRelativeLayout;
    @BindView(R.id.buttonSubmit)
    Button buttonSubmit;

    private GoogleMap googleMap;
    @BindView(R.id.marker_icon_view)
    ImageView mMarkerImageView;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.edit_icon)
    ImageView editAddress;
    private int imageParentWidth = -1;
    private int imageParentHeight = -1;
    private int imageHeight = -1;
    private int centerX = -1;
    private int centerY = -1;
    private String country, city, state, postalCode;
    private SweetAlertDialog pDialog;

    @BindView(R.id.autocomplete_places)
    AutoCompleteTextView autoCompleteTextView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.title_toolbar)
    TextView titleToolbar;
    @BindView(R.id.setlocationfor)
    TextView setLocationTextView;

    private PlaceAutocompleteAdapter mAdapter;
    protected GeoDataClient mGeoDataClient;
    private CustomMapFragment mapFragment;
    private ChildData childData;
    @Inject
    ChildDataRepository childDataRepository;
    @Inject
    MySharedPreferences mySharedPreferences;
    @Inject
    ApiInterface apiInterface;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeoDataClient = Places.getGeoDataClient(this, null);
        setContentView(R.layout.activity_setup_location);
        AndroidInjection.inject(this);
        ButterKnife.bind(this);
        setToolbar();
        // Register a listener that receives callbacks when a suggestion has been selected
        autoCompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mContext = this;
        // Set up the adapter that will retrieve suggestions from the Places Geo Data Client.
        mAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient, BOUNDS_GREATER_SYDNEY, null);
        autoCompleteTextView.setAdapter(mAdapter);
        mResultReceiver = new AddressResultReceiver(new Handler());
        childData = (ChildData) getIntent().getSerializableExtra(Constants.CHILD_DATA);
        init();

    }

    private void setToolbar() {
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.login));
        titleToolbar.setText(getResources().getString(R.string.set_pickup_point));
        titleToolbar.setTextColor(ContextCompat.getColor(this, R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.pickup_points));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

    }

    private void init() {
        pDialog = AppUtils.getDialog(this);
        mapFragment = (CustomMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_picker);
        mapFragment.setOnDragListener(SetLocationActivity.this);
        mapFragment.getMapAsync(this);
        setLocationTextView.setText(getResources().getString(R.string.setlocation) + " " + childData.getChildName());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        imageParentWidth = mMarkerParentView.getWidth();
        imageParentHeight = mMarkerParentView.getHeight();
        imageHeight = mMarkerImageView.getHeight();
        centerX = imageParentWidth / 2;
        centerY = (imageParentHeight / 2) + (imageHeight / 2);


    }


    /*
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableLocation() {
        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(SetLocationActivity.this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, false);
        } else {
            // Access to the location has been granted to the app.
            if (googleMap != null)
                googleMap.setMyLocationEnabled(true);
            //  buildGoogleApiClient();
            setChildLocation();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            setChildLocation();

        } else {
            mPermissionDenied = true;
        }
    }

    private void setChildLocation() {
        progressBar.setVisibility(View.GONE);
        if (null != childData.getLatitude() && !childData.getLatitude().equalsIgnoreCase("0")
                && null != childData.getLongitude() && !childData.getLongitude().equalsIgnoreCase("0")) {
            mLastLocation = new LatLng(Double.parseDouble(childData.getLatitude()), Double.parseDouble(childData.getLongitude()));
            setCurrentLocation(mLastLocation);
            setAutoCompleteTextView(childData.getAddress());
        }
    }


    protected void startIntentService() {
        progressBar.setVisibility(View.VISIBLE);
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(mContext, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        enableLocation();

        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 180);
        googleMap.setOnMyLocationButtonClickListener(this);
        enableLocation();

    }

    @OnClick({R.id.buttonSubmit, R.id.edit_icon})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSubmit:
                submitLocation();
                break;
            case R.id.edit_icon:
                autoCompleteTextView.setText("");
                break;
        }
    }

    @Override
    public void onDrag(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            Projection projection = (googleMap != null && googleMap
                    .getProjection() != null) ? googleMap.getProjection()
                    : null;
            if (projection != null) {
                if (centerX == 0 || centerY == 0) {
                    onWindowFocusChanged(true);
                } else {
                    LatLng centerLatLng = projection.fromScreenLocation(new Point(
                            centerX, centerY));
                    mLastLocation = centerLatLng;
                    startIntentService();
                    Log.i("Dragged Location", "LAT=" + centerLatLng.latitude + "\nLng=" + centerLatLng.longitude);
                }


            }
        }
    }

    @Override
    public void onClickAction() {

    }


    class AddressResultReceiver extends ResultReceiver {
        @SuppressLint("RestrictedApi")
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            // mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            // mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            List<Address> addressList = resultData.getParcelableArrayList(Constants.RESULT_DATA_KEY);


            if (addressList != null && addressList.size() > 0) {
                city = addressList.get(0).getLocality();
                state = addressList.get(0).getAdminArea();
                country = addressList.get(0).getCountryName();
                postalCode = addressList.get(0).getPostalCode();

                String addressIndex0 = (addressList.get(0).getAddressLine(0) != null) ? addressList
                        .get(0).getAddressLine(0) : null;
                String addressIndex1 = (addressList.get(0).getAddressLine(1) != null) ? addressList
                        .get(0).getAddressLine(1) : null;
                String addressIndex2 = (addressList.get(0).getAddressLine(2) != null) ? addressList
                        .get(0).getAddressLine(2) : null;
                String addressIndex3 = (addressList.get(0).getAddressLine(3) != null) ? addressList
                        .get(0).getAddressLine(3) : null;

                mAddressOutput = addressIndex0;

                if (addressIndex1 != null) {
                    mAddressOutput += "," + addressIndex1;
                }

                if (addressIndex2 != null) {
                    mAddressOutput += "," + addressIndex2;
                }
                if (addressIndex3 != null) {
                    mAddressOutput += "," + addressIndex3;
                }
                //  mLocationTextView.setText(completeAddress);
            }
            if (resultCode == Constants.SUCCESS_RESULT) {
                progressBar.setVisibility(View.GONE);
                setAutoCompleteTextView(mAddressOutput);
                // setCurrentLocation(mLastLocation);
            }
            mAddressRequested = false;
        }
    }

    private void setAutoCompleteTextView(String address) {
        autoCompleteTextView.setText(address);
        autoCompleteTextView.dismissDropDown();
        autoCompleteTextView.setSelection(autoCompleteTextView.getText().length());
    }

    private void setCurrentLocation(LatLng location) {
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        final LatLng latLng = new LatLng(location.latitude, location.longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(16f).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));


    }

    private void submitLocation() {
        if (city != null && !city.isEmpty()) {
            pDialog.show();
            final ChildData childData = new ChildData();
            childData.setChildName(this.childData.getChildName());
            childData.setAlarmTime(this.childData.getAlarmTime());
            childData.setBusId(this.childData.getBusId());
            childData.setChildId(this.childData.getChildId());
            childData.setLatitude(String.valueOf(mLastLocation.latitude));
            childData.setLongitude(String.valueOf(mLastLocation.longitude));
            childData.setAddress(mAddressOutput);
            childData.setState(state);
            childData.setPincode(postalCode);
            childData.setCity(city);
            Call<CommonResponse> submitResponse = apiInterface.updatePickupPoints(mySharedPreferences.getStringData(MySharedPreferences.PREFS_SESSION_TOKEN),
                    childData.getChildId(), childData.getLatitude(), childData.getLongitude(),
                    childData.getAddress(), childData.getCity(), childData.getState(), childData.getPincode());
            submitResponse.enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                    try {
                        Log.i("TAG ", "" + response.body().getSuccess());
                        if (response.body().getSuccess() == 1) {
                            //update the child info in local db
                            new UpdateChildData(childData).execute();
                        } else {
                            showDailog(response.body().getMessage(), response.body().getSuccess());

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
                            Util.getUserLogout(SetLocationActivity.this);
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
                mainScreenRelativeLayout.setVisibility(View.VISIBLE);
                setMainRelativeLayout.setVisibility(View.GONE);
                // setMainRelativeLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.light_transparent));
            }
        });
        dialog.show();

    }


    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data Client
     * to retrieve more details about the place.
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data Client to retrieve a Place object with
             additional details about the place.
              */
            Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(placeId);
            placeResult.addOnCompleteListener(mUpdatePlaceDetailsCallback);

            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data Client query that shows the first place result in
     * the details view on screen.
     */
    private OnCompleteListener<PlaceBufferResponse> mUpdatePlaceDetailsCallback
            = new OnCompleteListener<PlaceBufferResponse>() {
        @Override
        public void onComplete(Task<PlaceBufferResponse> task) {
            try {
                PlaceBufferResponse places = task.getResult();

                // Get the Place object from the buffer.
                final Place place = places.get(0);

                mLastLocation = place.getLatLng();
                setCurrentLocation(mLastLocation);
                AppUtils.hideKeyboardFrom(mContext, autoCompleteTextView);
                startIntentService();

                // Format details of the place for display and show it in a TextView.
                //   mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(), place.getId(), place.getAddress(), place.getPhoneNumber(),place.getWebsiteUri()));

                // Display the third party attributions if set.
                final CharSequence thirdPartyAttribution = places.getAttributions();

                Log.i(TAG, "Place details received: " + place.getName());

                places.release();
            } catch (RuntimeRemoteException e) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete.", e);
                return;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class UpdateChildData extends AsyncTask<Void, Void, Void> {


        ChildData childData;

        public UpdateChildData(ChildData childData) {
            this.childData = childData;
        }

        @Override
        protected Void doInBackground(Void... params) {
            childDataRepository.updateChildData(this.childData);
            return null;
        }

        @Override
        protected void onPostExecute(Void agentsCount) {
            pDialog.dismiss();
            setResult(SET_LOCATION_REQUEST);
            finish();

        }
    }
}
