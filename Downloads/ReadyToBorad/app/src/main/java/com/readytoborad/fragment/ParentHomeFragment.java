package com.readytoborad.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.readytoborad.R;
import com.readytoborad.activity.ParentDashboardActivity;
import com.readytoborad.customviews.SeekBarHint;
import com.readytoborad.database.ChildData;
import com.readytoborad.database.MySharedPreferences;
import com.readytoborad.database.respository.ChildDataRepository;
import com.readytoborad.interfaces.ApiInterface;
import com.readytoborad.model.DriverLocationByChild;
import com.readytoborad.util.AppUtils;
import com.readytoborad.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.android.support.AndroidSupportInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParentHomeFragment extends BaseFragment implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_logout)
    TextView tv_logout;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    @BindView(R.id.img_refresh)
    ImageView img_refresh;
    @BindView(R.id.iv_map)
    ImageView img_map;
    @BindView(R.id.btn_left)
    Button btn_left;
    @BindView(R.id.ll_lyt)
    LinearLayout btnLL;
    @BindView(R.id.transit_layout)
    RelativeLayout transitRelativeLayout;
    Context mContext;
    Resources mResources;
    @BindView(R.id.bus)
    ImageView iv;
    @BindView(R.id.seek_bar)
    SeekBarHint seekBarHint;
    @BindView(R.id.child_location_textview)
    TextView childLocationText;
    SupportMapFragment mapFragment;
    GoogleMap googleMap;
    private boolean mPermissionDenied = false;
    int which_frag = -1, parent_route = 1, parent_map = 2;
    List<ChildData> childArrayList;
    @Inject
    ApiInterface apiInterface;
    @Inject
    ChildDataRepository childDataRepository;
    @Inject
    MySharedPreferences mySharedPreferences;
    ArrayList<DriverLocationByChild> driverLocationByChildList;
    ArrayList<String> childIdList;
    SweetAlertDialog pDialog;
    private boolean isFragmentLoaded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_parent_home, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
    }

    private class GetChildData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            childArrayList = childDataRepository.getAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void agentsCount) {
            addButton();
            setSelected(0);
            getDriverByChildID(String.valueOf(childArrayList.get(0).getChildId()));

        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isFragmentLoaded) {
            // Load your data here or do network operations here
            isFragmentLoaded = true;
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, getView());
        if (isFragmentLoaded)
            ((ParentDashboardActivity) getActivity()).setToolbarInfo(false, getResources().getString(R.string.app_name));
        init();
        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map_parent_bus_home);
        mapFragment.getMapAsync(ParentHomeFragment.this);
        mapFragment.getView().setVisibility(View.GONE);
        mToolbar.setVisibility(View.GONE);
        seekBarHint.getProgressDrawable().setColorFilter(Color.parseColor("#00ad80"), PorterDuff.Mode.SRC_IN);
        seekBarHint.getThumb().setColorFilter(Color.parseColor("#00ad80"), PorterDuff.Mode.SRC_IN);
        tv_logout.setVisibility(View.GONE);
        toolbar_title.setText(mResources.getString(R.string.app_name));
        mToolbar.setBackgroundColor(mResources.getColor(R.color.login));
        tv_logout.setTextColor(Color.WHITE);
        img_refresh.setVisibility(View.GONE);
        img_map.setOnClickListener(this);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dpi = metrics.densityDpi;

        System.out.println("dpi " + dpi);
        if (dpi < 230) {
            SeekBarHint.setTextYPositionIndent(1);
        } else if (dpi < 310) {
            SeekBarHint.setTextYPositionIndent(1);
        } else if (dpi < 470) {
            SeekBarHint.setTextYPositionIndent(1);
        }


        new GetChildData().execute();


    }

    public void init() {
        mContext = getActivity();
        mResources = getResources();

        driverLocationByChildList = new ArrayList<>();
        childIdList = new ArrayList<>();
        pDialog = AppUtils.getDialog(getActivity());

        which_frag = parent_route;
    }

    public void addButton() {
        if (childArrayList != null && childArrayList.size() > 0) {
            btnLL.setWeightSum(childArrayList.size());
            int count = 0;
            btnLL.removeAllViews();
            for (ChildData childData : childArrayList) {
                Button btn = new Button(mContext);
                btn.setId(count);
                btn.setText(childData.getChildName());
                btn.setBackgroundResource(R.drawable.rectangle);
                btn.setTextColor(mResources.getColor(R.color.white));
                btn.setMaxLines(2);
                btn.setEllipsize(TextUtils.TruncateAt.END);
                btn.setOnClickListener(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                lp.weight = 1;
                btnLL.addView(btn, lp);
                count++;
            }
        }
    }

    public void setSelected(int position) {
        for (int j = 0; j < btnLL.getChildCount(); j++) {

            if (j == position) {
                TextView button = (TextView) btnLL.getChildAt(j);
                button.setBackgroundResource(R.drawable.rectangle);
                button.setTextColor(mResources.getColor(R.color.white));

            } else {
                TextView button = (TextView) btnLL.getChildAt(j);
                button.setBackgroundColor(Color.TRANSPARENT);
                button.setTextColor(mResources.getColor(R.color.login));
            }
            if (null != childArrayList && null != childArrayList.get(position)) {
                childLocationText.setText(childArrayList.get(position).getAddress());
            }

        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_map:
                if (which_frag == parent_route) {
                    mapFragment.getView().setVisibility(View.VISIBLE);
                    which_frag = parent_map;
                } else {
                    mapFragment.getView().setVisibility(View.GONE);
                    which_frag = parent_route;
                }
                break;
            case 0:
            case 1:
            case 2:
                setSelected(view.getId());
                if (!checkDriverByChildSearched(String.valueOf(childArrayList.get(view.getId()).getChildId())))
                    getDriverByChildID(String.valueOf(childArrayList.get(view.getId()).getChildId()));
                break;
            default:
                break;

        }
    }

    public boolean checkDriverByChildSearched(String childId) {
        return childIdList.contains(childId);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
        //Initialize Google Play Services
    }

    /**
     * Enables the My Loocation layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission((Activity) mContext, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        } else if (googleMap != null) {
            // Access to the location has been granted to the app.
            // buildGoogleApiClient();
            MarkerOptions mp = new MarkerOptions();
            LatLng latLng = new LatLng(30.741482, 76.768066);
            mp.position(latLng);
            mp.title("my position");
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5.0f));
            googleMap.addMarker(mp);
            googleMap.setMyLocationEnabled(true);
            // Latitude‎: ‎30.741482 Longitude‎: ‎76.768066

            //setchilldLocation();

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
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

   /* public void setchilldLocation() {
        childArrayList = ((ParentDashboardActivity) getActivity()).getchildData();
        if (childArrayList != null && childArrayList.size() > 0) {

            for (ChildData childData : childArrayList) {
                try {
                    MarkerOptions mp = new MarkerOptions();
                    LatLng latLng = new LatLng(Double.parseDouble(childData.getLatitude()), Double.parseDouble(childData.getLongitude()));
                    mp.position(latLng);
                    mp.title(childData.getChildName());
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5.0f));
                    googleMap.addMarker(mp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }*/


    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    public void getDriverByChildID(final String child_id) {

        pDialog.show();
        pDialog.setTitleText("Loading driver data....");

        Call<DriverLocationByChild> loginResponse = apiInterface.driverLocationByChildId(mySharedPreferences.getStringData(MySharedPreferences.PREFS_SESSION_TOKEN), child_id);
        loginResponse.enqueue(new Callback<DriverLocationByChild>() {
            @Override
            public void onResponse(Call<DriverLocationByChild> call, Response<DriverLocationByChild> response) {
                pDialog.dismiss();

                try {
                    if (response.body().getSuccess() == 1) {
                        driverLocationByChildList.add(response.body());
                        childIdList.add(child_id);
                        transitRelativeLayout.setVisibility(View.GONE);

                    } else {
                        transitRelativeLayout.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<DriverLocationByChild> call, Throwable t) {
                pDialog.dismiss();
                t.printStackTrace();
            }
        });
    }
}