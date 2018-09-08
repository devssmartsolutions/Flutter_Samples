package com.readytoborad.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.readytoborad.R;
import com.readytoborad.adapter.GooglePlacesAutocompleteAdapter;
import com.readytoborad.adapter.NotificationRecycleAdapter;
import com.readytoborad.interfaces.ApiInterface;
import com.readytoborad.model.ChildLocation;
import com.readytoborad.model.NotificationModel;
import com.readytoborad.util.AppUtils;
import com.readytoborad.util.MyInfoWindowAdapter;
import com.readytoborad.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.android.AndroidInjection;

/**
 * Created by Vicky Garg on 12/27/2017.
 */

public class ParentSavePickUpPointActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, AdapterView.OnItemClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    Context mContext;
    Resources mResources;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    RelativeLayout mToolbar;
    LinearLayout back_lyt;
    private GoogleMap googleMap;
    TextView tv_logout, toolbar_title;
    ArrayList<NotificationModel> notificationModelList;
    NotificationRecycleAdapter notificationRecycleAdapter;
    ImageView img_refresh;
    AutoCompleteTextView autoCompleteTextView;
    ArrayList<String> arrayList;
    Button btn_save, btn_left, btn_right;
    SupportMapFragment mapFragment;
    TextView tvChildName;
    int current_pos = -1, max_pos = 0;
    ArrayList<ChildLocation> locationArrayList;
    PlaceAutocompleteFragment places;
    GoogleApiClient mGoogleApiClient;
    private SweetAlertDialog pDialog;
    @Inject
    ApiInterface apiInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_save_pickup_points);
        AndroidInjection.inject(this);
        init();
        prepareMovieData();

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        tvChildName = (TextView) findViewById(R.id.tv_child_name);

        autoCompleteTextView.setThreshold(1);


        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);


        btn_left = (Button) findViewById(R.id.btn_left);
        btn_left.setOnClickListener(this);

        btn_right = (Button) findViewById(R.id.btn_right);
        btn_right.setOnClickListener(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.save_parent_bus_loc);
        mapFragment.getMapAsync(ParentSavePickUpPointActivity.this);


        autoCompleteTextView.setAdapter(new GooglePlacesAutocompleteAdapter(this));
        autoCompleteTextView.setOnItemClickListener(this);


       /* places = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        places.getView().findViewById(R.id.place_autocomplete_clear_button).setVisibility(View.GONE);
        places.getView().findViewById(R.id.place_autocomplete_search_input).setVisibility(View.GONE);

        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                ChildLocation childLocation = locationArrayList.get(current_pos);
                childLocation.setAddress(place.getAddress().toString());
                childLocation.setLatitude(String.valueOf(place.getLatLng().latitude));
                childLocation.setLongitude(String.valueOf(place.getLatLng().longitude));
                locationArrayList.set(current_pos, childLocation);
                setLocOnMap(place.getLatLng());
                autoCompleteTextView.setText(place.getAddress().toString());

            }

            @Override
            public void onError(Status status) {

                Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();

            }
        });*/
        setData();
    }

    public void init() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Uploading");
        pDialog.setCancelable(false);

        mContext = this;
        mResources = getResources();
        notificationModelList = new ArrayList<NotificationModel>();
        arrayList = new ArrayList<>();
        buildGoogleApiClient();

    }

    public void setData() {
        if (getIntent() != null) {
            Intent intent = getIntent();
            locationArrayList = intent.getParcelableArrayListExtra("childObj");
            if (locationArrayList != null && locationArrayList.size() > 0) {

                current_pos = 0;
                ChildLocation childLocation = locationArrayList.get(0);
                max_pos = locationArrayList.size();
                tvChildName.setText(mResources.getString(R.string.set_location_txt) + " " + childLocation.getChild_name());

            }

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isChecked) {
            AppUtils.showToast(mContext, "Hello true");
        } else {
            AppUtils.showToast(mContext, "Hello false");
        }
    }


    private void prepareMovieData() {
        for (int i = 0; i < 10; i++) {
            NotificationModel notificationModel = new NotificationModel();
            notificationModel.setMessage("Notification Message " + i);
            notificationModel.setDesc(" Mohali sector " + i);
            notificationModelList.add(notificationModel);
            arrayList.add("Notification Message 1");
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back_lyt:

                break;
            case R.id.btn_save:

              //  submitLocation();

              /*  Intent intent = new Intent(mContext, ParentDashboardActivity.class);
                finish();
                startActivity(intent);*/
                break;
            case R.id.btn_left:
                if (current_pos == 0) {

                    btn_left.setVisibility(View.GONE);
                    btn_save.setVisibility(View.GONE);
                    btn_right.setVisibility(View.VISIBLE);
                } else {

                    current_pos = current_pos - 1;
                    ChildLocation childLocation = locationArrayList.get(current_pos);

                    btn_left.setVisibility(View.VISIBLE);
                    btn_save.setVisibility(View.GONE);
                    btn_right.setVisibility(View.VISIBLE);


                    autoCompleteTextView.setText("" + childLocation.getAddress());
                    tvChildName.setText(mResources.getString(R.string.set_location_txt) + " " + childLocation.getChild_name());
                    googleMap.clear();

                    if (childLocation.getLatitude() != null && !childLocation.getLatitude().equalsIgnoreCase("") && childLocation.getLongitude() != null) {
                        LatLng latLng = new LatLng(Double.parseDouble(childLocation.getLatitude()), Double.parseDouble(childLocation.getLongitude()));
                        setLocOnMap(latLng, childLocation.getChild_name());
                    }
                    if (current_pos == 0) {
                        btn_left.setVisibility(View.GONE);
                        btn_save.setVisibility(View.GONE);
                        btn_right.setVisibility(View.VISIBLE);

                    }
                }
                break;
            case R.id.btn_right:
                if (current_pos >= max_pos - 1) {

                    btn_left.setVisibility(View.VISIBLE);
                    btn_save.setVisibility(View.VISIBLE);
                    btn_right.setVisibility(View.GONE);

                } else {

                    current_pos = current_pos + 1;
                    ChildLocation childLocation = locationArrayList.get(current_pos);

                    btn_left.setVisibility(View.VISIBLE);
                    btn_save.setVisibility(View.GONE);
                    btn_right.setVisibility(View.VISIBLE);

                    autoCompleteTextView.setText("" + childLocation.getAddress());

                    tvChildName.setText(mResources.getString(R.string.set_location_txt) + " " + childLocation.getChild_name());
                    googleMap.clear();
                    if (childLocation.getLatitude() != null && !childLocation.getLatitude().equalsIgnoreCase("") && childLocation.getLongitude() != null) {
                        LatLng latLng = new LatLng(Double.parseDouble(childLocation.getLatitude()), Double.parseDouble(childLocation.getLongitude()));
                        setLocOnMap(latLng, childLocation.getChild_name());
                    }

                    if (current_pos == max_pos - 1) {

                        btn_left.setVisibility(View.VISIBLE);
                        btn_save.setVisibility(View.VISIBLE);
                        btn_right.setVisibility(View.GONE);
                    }
                }

                break;
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission((AppCompatActivity) mContext, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, false);
        } else if (googleMap != null) {
            // Access to the location has been granted to the app.

            googleMap.setMyLocationEnabled(true);

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;
        LatLng chdLatLng = new LatLng(30.7362900, 76.7884000);
        setLocOnMap(chdLatLng, "My location");

    }


    public void setLocOnMap(LatLng latLng, String childName) {
        googleMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker));

        markerOptions.title(childName);
        if (mContext != null)
            googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter((Activity) mContext));

        googleMap.addMarker(markerOptions);
        googleMap.setOnMyLocationButtonClickListener(this);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(15f).tilt(3).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        enableMyLocation();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        String place_id = ((TextView) view.findViewById(R.id.tv_id)).getText().toString();
        String tvAddress = ((TextView) view.findViewById(R.id.tv_title)).getText().toString();

        autoCompleteTextView.setText(tvAddress);
        setLatLang(place_id);

        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(autoCompleteTextView, 0);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    public void setLatLang(String placeId) {
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place place = places.get(0);
                            LatLng latLng = place.getLatLng();
                            ChildLocation childLocation = locationArrayList.get(current_pos);
                            childLocation.setLongitude("" + latLng.longitude);
                            childLocation.setLatitude("" + latLng.latitude);
                            try {

                                childLocation.setCity(place.getAddress().toString());
                                childLocation.setAddress(place.getAddress().toString());
                                setLocOnMap(latLng, childLocation.getChild_name());

                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                List<Address> addresses;

                                // Attempt to Geocode from place lat & long
                                try {

                                    addresses = geocoder.getFromLocation(
                                            place.getLatLng().latitude,
                                            place.getLatLng().longitude,
                                            1);

                                    if (addresses.get(0).getPostalCode() != null) {
                                        childLocation.setPincode(addresses.get(0).getPostalCode());
                                    }

                                    if (addresses.get(0).getLocality() != null) {
                                        childLocation.setCity(addresses.get(0).getLocality());
                                    }

                                    if (addresses.get(0).getAdminArea() != null) {
                                        childLocation.setState(addresses.get(0).getAdminArea());
                                    }

                                    if (addresses.get(0).getCountryName() != null) {
                                        String country = addresses.get(0).getCountryName();
                                        Log.d("country", country);
                                    }

                                    locationArrayList.set(current_pos, childLocation);

                                } catch (Exception ex) {

                                    ex.printStackTrace();
                                    Log.e("MapException", ex.getMessage());
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Log.i("place", "Place found: " + place.getName());
                        } else {
                            Log.e("place", "Place not found");
                        }
                        places.release();
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

   /* public void submitLocation() {

        pDialog.show();
        final SessionManager sessionManager = new SessionManager(mContext);
        Log.i("Authoriztion", "" + sessionManager.getKeyToken());
        JsonObject object = new JsonObject();
        String jsonData = "";
        try {

            String string = new Gson().toJson(locationArrayList);
            JSONArray jsonObject = new JSONArray(string);
            JSONObject jsonObject1 = new JSONObject();
            // System.out.println(string);
            jsonObject1.put("childrenArr", jsonObject);

            jsonData = jsonObject1.toString().replace("\\", "");
            System.out.println(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Call<ResponseModel> loginResponse = apiInterface.setUppickupPoint(sessionManager.getKeyToken(), jsonData);
        loginResponse.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                pDialog.dismiss();

                try {
                    if (response.body().getSuccess() == 1) {

                        Intent intent = new Intent(mContext, ParentDashboardActivity.class);
                        intent.putParcelableArrayListExtra("childObj", locationArrayList);
                        finish();
                        startActivity(intent);

                    } else {
                        Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                pDialog.dismiss();
                t.printStackTrace();
            }
        });
    }*/
}



