package com.readytoborad.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.os.ResultReceiver;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.readytoborad.interfaces.ApiInterface;
import com.readytoborad.interfaces.IOnFocusListenable;
import com.readytoborad.map.CustomMapFragment;
import com.readytoborad.map.MapWrapperLayout;
import com.readytoborad.service.FetchAddressIntentService;
import com.readytoborad.util.Constants;
import com.readytoborad.util.PermissionUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.android.support.AndroidSupportInjection;

public class SetLocationFragment extends BaseFragment implements OnMapReadyCallback, MapWrapperLayout.OnDragListener, IOnFocusListenable {
    private static final String TAG = SetLocationFragment.class.getSimpleName();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private int imageParentWidth = -1;
    private int imageParentHeight = -1;
    private int imageHeight = -1;
    private int centerX = -1;
    private int centerY = -1;
    protected GeoDataClient mGeoDataClient;
    private boolean mAddressRequested;
    private PlaceAutocompleteAdapter mAdapter;
    @BindView(R.id.autocomplete_places)
    AutoCompleteTextView mAutocompleteView;
    private GoogleMap googleMap;
    private View mMarkerParentView;
    private Marker mCurrLocationMarker;
    private ImageView mMarkerImageView;
    private RelativeLayout main_screen, setmain;
    private ProgressBar progressBar;
    @Inject
    ApiInterface apiInterface;
    private CustomMapFragment mapFragment;
    private AddressResultReceiver mResultReceiver;
    private LatLng mLastLocation;
    private SweetAlertDialog pDialog;
    protected String mAddressOutput;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mGeoDataClient = Places.getGeoDataClient(this.getContext(), null);
        return inflater.inflate(R.layout.activity_setup_location, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, getView());
        pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Loading");
        pDialog.setCancelable(false);
        mMarkerParentView = getView().findViewById(R.id.marker_icon_parent);
        main_screen = (RelativeLayout) getView().findViewById(R.id.main_screen);
        setmain = (RelativeLayout) getView().findViewById(R.id.setmain);
        mapFragment = (CustomMapFragment) getChildFragmentManager().findFragmentById(R.id.map_picker);
        mapFragment.setOnDragListener(this);
        mapFragment.getMapAsync(this);
        mMarkerImageView = (ImageView) getView().findViewById(R.id.marker_icon_view);
        progressBar = (ProgressBar) getView().findViewById(R.id.progress);
        mAdapter = new PlaceAutocompleteAdapter(this.getContext(), mGeoDataClient, BOUNDS_GREATER_SYDNEY, null);
        mAutocompleteView.setAdapter(mAdapter);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);
        enableLocation();

    }

    @Override
    public void onDrag(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            Projection projection = (googleMap != null && googleMap
                    .getProjection() != null) ? googleMap.getProjection()
                    : null;
            if (projection != null) {
                LatLng centerLatLng = projection.fromScreenLocation(new Point(
                        centerX, centerY));

                mLastLocation = centerLatLng;
                startIntentService();
                Log.i("Dragged Location", "LAT=" + centerLatLng.latitude + "\nLng=" + centerLatLng.longitude);
                /// setCurrentLocation(mLastLocation);
            }
        }

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

            Toast.makeText(getActivity(), "Clicked: " + primaryText,
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
                startIntentService();

                // Format details of the place for display and show it in a TextView.
                //   mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(), place.getId(), place.getAddress(), place.getPhoneNumber(),place.getWebsiteUri()));

                // Display the third party attributions if set.
                final CharSequence thirdPartyAttribution = places.getAttributions();
                /*if (thirdPartyAttribution == null) {
                    mPlaceDetailsAttribution.setVisibility(View.GONE);
                } else {
                    mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                    mPlaceDetailsAttribution.setText(
                            Html.fromHtml(thirdPartyAttribution.toString()));
                }*/

                Log.i(TAG, "Place details received: " + place.getName());

                places.release();
            } catch (RuntimeRemoteException e) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete.", e);
                return;
            }
        }
    };

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

    protected void startIntentService() {
        progressBar.setVisibility(View.VISIBLE);
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this.getContext(), FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        this.getContext().startService(intent);
    }

    private void enableLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, false);
        } else {
            // Access to the location has been granted to the app.
            if (googleMap != null)
                googleMap.setMyLocationEnabled(true);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            //if (googleMap != null)
            // googleMap.setMyLocationEnabled(true);

        } else {
            // Display the missing permission error dialog when the fragments resume.
            //   mPermissionDenied = true;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        imageParentWidth = mMarkerParentView.getWidth();
        imageParentHeight = mMarkerParentView.getHeight();
        imageHeight = mMarkerImageView.getHeight();
        centerX = imageParentWidth / 2;
        centerY = (imageParentHeight / 2) + (imageHeight / 2);
        if (centerX == 0 || centerY == 0) {
            onWindowFocusChanged(true);
        }

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
                mAutocompleteView.setText(mAddressOutput);
                mAutocompleteView.dismissDropDown();
                mAutocompleteView.setSelection(mAutocompleteView.getText().length());
                setCurrentLocation(mLastLocation);
            }
            mAddressRequested = false;
        }
    }
}
