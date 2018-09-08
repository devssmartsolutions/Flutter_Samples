package com.readytoborad.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.readytoborad.model.NotificationModel;
import com.readytoborad.util.AppUtils;
import com.readytoborad.util.MyInfoWindowAdapter;
import com.readytoborad.util.PermissionUtils;

import java.util.ArrayList;

/**
 * Created by Vicky Garg on 1/17/2018.
 */

public class ParentSetPickUpPointsFragment extends Fragment implements AdapterView.OnItemClickListener,CompoundButton.OnCheckedChangeListener, View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {


    Context mContext;
    Resources mResources;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private  final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
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
    Button btn_save;
    SupportMapFragment mapFragment;
    TextView tvChildName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_save_pickup_points, container, false);

        init();
        prepareMovieData();

        autoCompleteTextView = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTextView);
        tvChildName = (TextView) rootView.findViewById(R.id.tv_child_name);

        autoCompleteTextView.setThreshold(1);

        btn_save = (Button) rootView.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);


        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.save_parent_bus_loc);
        mapFragment.getMapAsync(ParentSetPickUpPointsFragment.this);


        autoCompleteTextView.setAdapter(new GooglePlacesAutocompleteAdapter(mContext));
        autoCompleteTextView.setOnItemClickListener(this);


       /* PlaceAutocompleteFragment places= (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                Toast.makeText(mContext,place.getName(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Status status) {

                Toast.makeText(mContext,status.toString(),Toast.LENGTH_SHORT).show();

            }
        });*/
        setData();


        return rootView;
    }

    public void init() {
        mContext = getActivity();
        mResources = getResources();
        notificationModelList = new ArrayList<NotificationModel>();
        arrayList = new ArrayList<>();


    }
    public void setData()
    {
        if (getArguments() != null) {
            Bundle intent = getArguments();
            String child_name  = intent.getString("child_name");

                tvChildName.setText(  mResources.getString(R.string.set_location_txt) + child_name);

        }
    }
    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isChecked) {
            AppUtils.showToast(mContext, "Hello true");
        } else {
            AppUtils.showToast(mContext, "Hello false");
        }
    }

    private void setNotificationAdapter() {


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
        googleMap.getUiSettings().setZoomGesturesEnabled(false);

        MarkerOptions markerOptions = new MarkerOptions();
        LatLng chdLatLng = new LatLng(30.7362900, 76.7884000);
        markerOptions.position(chdLatLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker));

        markerOptions.title("My Location");
        if (mContext != null)
            googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter((Activity) mContext));

        googleMap.addMarker(markerOptions);
        googleMap.setOnMyLocationButtonClickListener(this);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(chdLatLng).zoom(5f).tilt(3).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        enableMyLocation();

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }
}




