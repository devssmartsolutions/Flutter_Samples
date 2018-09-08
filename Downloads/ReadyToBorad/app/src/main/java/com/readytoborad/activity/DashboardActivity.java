package com.readytoborad.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.readytoborad.R;
import com.readytoborad.fragment.DriverMapFragment;
import com.readytoborad.fragment.MapViewFragment;
import com.readytoborad.fragment.RouteViewFragment;
import com.readytoborad.interfaces.ApiInterface;
import com.readytoborad.model.DashBoardParser;
import com.readytoborad.model.DriverDashResponse;
import com.readytoborad.model.ResponseModel;
import com.readytoborad.model.StudentModel;
import com.readytoborad.session.SessionManager;
import com.readytoborad.util.AppUtils;
import com.readytoborad.util.Constants;
import com.readytoborad.util.Util;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class DashboardActivity extends BaseActivity implements View.OnClickListener {
    private Context mContext;
    static final Integer LOCATION = 0x1;
    private RelativeLayout mToolbar;
    private TextView mTitle, mLogout;
    private TextView text_map;
    private TextView text_route, tv_alert_box;
    private LinearLayout view_layout, view_layout1;
    private Button routeView, mapView, btn_end_trip;
    private Spinner child_spinner;
    private View menuView;
    private SessionManager sessionManager;
    private String userType;
    private Resources resources;
    private static final int RequestPermissionCode = 1;
    private LinearLayout ll_main;
    private RelativeLayout rl_start_dailog;
    private ImageView start_trip, img_refresh;
    private SweetAlertDialog pDialog;
    private List<StudentModel> studentModelList;
    private List<String> studentNameList;
    private int routeFragment = 1;
    private DriverDashResponse driverDashResponse;
    Bundle bd;
    Fragment fragment;
    FragmentTransaction ftx;
    Resources mResources;
    @Inject
    ApiInterface apiInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Util.statuBarColor(this, R.color.light, true);
        setContentView(R.layout.activity_dashboard);
        AndroidInjection.inject(this);
        mContext = this;
        mResources = this.getResources();
        sessionManager = new SessionManager(this);
        mToolbar = (RelativeLayout) findViewById(R.id.toolbar);
        mToolbar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.login));
        mTitle = (TextView) findViewById(R.id.toolbar_title);
        mLogout = (TextView) findViewById(R.id.tv_logout);
        mLogout.setVisibility(View.GONE);

        img_refresh = (ImageView) findViewById(R.id.img_refresh);


        mTitle.setText(getResources().getString(R.string.app_name));
        mTitle.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.white)));


//        setSupportActionBar(mToolbar);
       /* getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//
        pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Loading");
        pDialog.setCancelable(false);

        init();
       /* child_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (routeFragment == 1) {
                    RouteViewFragment.routeInterface.changeChild(studentModelList.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/


        //   getDashboard();

    }

    public void showFragment() {
        if (userType.equalsIgnoreCase(resources.getString(R.string.driver)))
            callFragment(1);
        else
            callFragment(0);
    }

    private void init() {
        userType = sessionManager.getKeyUserType();
        resources = getResources();
        view_layout = (LinearLayout) findViewById(R.id.view_layout);
        view_layout1 = (LinearLayout) findViewById(R.id.view_layout1);
        rl_start_dailog = (RelativeLayout) findViewById(R.id.rl_start_dailog);
        start_trip = (ImageView) findViewById(R.id.start_trip);
        routeView = (Button) findViewById(R.id.routeView);
        mapView = (Button) findViewById(R.id.mapView);
        btn_end_trip = (Button) findViewById(R.id.btn_end_trip);
        text_map = (TextView) findViewById(R.id.text_map);
        text_route = (TextView) findViewById(R.id.text_route);
        child_spinner = (Spinner) findViewById(R.id.spinner_child);
        ll_main = (LinearLayout) findViewById(R.id.ll_main);
        tv_alert_box = (TextView) findViewById(R.id.tv_alert_box);

        text_map.setOnClickListener(this);
        routeView.setOnClickListener(this);
        mapView.setOnClickListener(this);
        text_route.setOnClickListener(this);
        start_trip.setOnClickListener(this);
        if (userType.equalsIgnoreCase(resources.getString(R.string.driver)))
            rl_start_dailog.setVisibility(View.VISIBLE);
        rl_start_dailog.setOnClickListener(this);
        btn_end_trip.setOnClickListener(this);


        img_refresh.setOnClickListener(this);

    }

    public boolean checkPermission() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                ACCESS_FINE_LOCATION}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean LocationPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (LocationPermission) {

                        //   showFragment();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();

                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_route:
                callFragment(0);
                break;
            case R.id.text_map:
                callFragment(1);
                break;
            case R.id.routeView:
                callFragment(0);
                break;
            case R.id.mapView:
                callFragment(1);
                break;
            case R.id.start_trip:
                rl_start_dailog.setVisibility(View.GONE);
                break;
            case R.id.rl_start_dailog:
                break;
            case R.id.img_refresh:
                //  pDialog.show();
                ((DriverMapFragment) fragment).updateDriverLocation();
//                pDialog.dismiss();
                break;
            case R.id.btn_end_trip:
                if (AppUtils.isNetworkAvailable(mContext)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(mResources.getString(R.string.confirmation));
                    builder.setMessage(mResources.getString(R.string.end_trip_alert_msg));
                    builder.setPositiveButton(mResources.getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            endTrip();
                        }
                    });
                    builder.setNegativeButton(mResources.getString(R.string.no), null);
                    builder.show();

                } else
                    Util.showNetworkAlertDialog(mContext);

                break;

        }
    }

    public void onResume() {
        super.onResume();

        if (!AppUtils.isLocationEnabled(mContext)) {
            AppUtils.showLocationAlertDialog(mContext);
        } else {
            if (!checkPermission()) {
                requestPermission();
            } else {
                if (userType.equalsIgnoreCase(resources.getString(R.string.driver)))
                    callFragment(1);
                else
                    callFragment(0);
            }
        }

    }


  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
//         menuView=findViewById(R.id.menuitem).getRootView();
//
//        menuView.setPadding(16,0,0,0);
        return false;
    }*/

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuitem:
                View view = findViewById(R.id.menuitem);
                PopupMenu popupMenu = new PopupMenu(DashboardActivity.this, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.profile:
                                // startActivity(new Intent(DashboardActivity.this, NotificationActivity.class));
                                break;
                            case R.id.notification:
                                startActivity(new Intent(DashboardActivity.this, NotificationActivity.class));
                                break;
                            case R.id.settings:
                                startActivity(new Intent(DashboardActivity.this, SettingsActivity.class));
                                break;
                            case R.id.logout:
                                if (AppUtils.isNetworkAvailable(mContext)) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    builder.setTitle(mResources.getString(R.string.confirmation));
                                    builder.setMessage(mResources.getString(R.string.logout_alert_msg));
                                    builder.setPositiveButton(mResources.getString(R.string.yes), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            driverLogout();
                                        }
                                    });
                                    builder.setNegativeButton(mResources.getString(R.string.no), null);
                                    builder.show();

                                } else
                                    Util.showNetworkAlertDialog(mContext);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.getMenuInflater().inflate(R.menu.pop_up, popupMenu.getMenu());
                popupMenu.show();
                MenuPopupHelper menuHelper = new MenuPopupHelper(DashboardActivity.this, (MenuBuilder) popupMenu.getMenu(), view);
                menuHelper.setForceShowIcon(true);
                menuHelper.show();
                //  popup_layout.setVisibility(View.VISIBLE);
                break;
            case android.R.id.home:
                finish();
                break;
            case R.id.logout:
                if (AppUtils.isNetworkAvailable(mContext)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(mResources.getString(R.string.confirmation));
                    builder.setMessage(mResources.getString(R.string.logout_alert_msg));
                    builder.setPositiveButton(mResources.getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            driverLogout();
                        }
                    });
                    builder.setNegativeButton(mResources.getString(R.string.no), null);
                    builder.show();

                } else
                    Util.showNetworkAlertDialog(mContext);
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

    public void callFragment(int position) {
        //drawerLayout.closeDrawer(Gravity.LEFT);
        Log.i("TAG", "pos=" + position);
        clearBackStack();
        FragmentManager fragmentManager = getSupportFragmentManager();
        ftx = fragmentManager.beginTransaction();
        fragment = null;
        bd = new Bundle();

        switch (position) {
            case 0:
                routeFragment = 1;
                view_layout1.setVisibility(View.GONE);
                view_layout.setVisibility(View.VISIBLE);
                if (userType.equalsIgnoreCase(resources.getString(R.string.parent))) {
                    getDashboard();


                } else {
                    getDashboardDriver();

                }
                break;
            case 1:
                routeFragment = 0;
                view_layout.setVisibility(View.GONE);
                view_layout1.setVisibility(View.VISIBLE);
                if (userType.equalsIgnoreCase(resources.getString(R.string.parent))) {
                    fragment = new MapViewFragment();
                    if (fragment != null) {
                        replaceFragment(fragment);
                        ftx.commit();
                    }
                } else {
                    //fragment = new DriverMapFragment();

                    if (AppUtils.isNetworkAvailable(mContext))
                        getDashboardDriver();
                    else
                        Util.showNetworkAlertDialog(mContext);
                }


                break;

            default:
                break;
        }

    }

    private void clearBackStack() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        while (fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStackImmediate();
        }
    }

    private void replaceFragment(Fragment fragment) {
        if (fragment != null) {
            String backStateName = fragment.getClass().getName();
            FragmentManager manager = getSupportFragmentManager();
            boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
            if (!fragmentPopped) { //fragment not in back stack, create it.
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.viewContainer, fragment);
                ft.addToBackStack(backStateName);
                ft.commit();
            }
        }

    }

    /*@Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        // Log.i("TAG", "Count Frag=" + manager.getBackStackEntryCount());
        if (manager.getBackStackEntryCount() == 1) {
            Log.i("Last", "backstack=" + manager.getBackStackEntryAt(0).getName());
            // Log.i("Real", "class frag=" + FragmentHome.class.getName());
            if (manager.getBackStackEntryAt(0).getName().equalsIgnoreCase(RouteViewFragment.class.getName()) || manager.getBackStackEntryAt(0).getName().equalsIgnoreCase(DriverRouteFragment.class.getName())) {
                finish();

            } else {
                callFragment(0);
            }
        } else {

            super.onBackPressed();

        }
    }*/
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mResources.getString(R.string.confirmation));
        builder.setMessage(mResources.getString(R.string.exit_alert));
        builder.setPositiveButton(mResources.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                finish();
            }
        });
        builder.setNegativeButton(mResources.getString(R.string.no), null);
        builder.show();
    }

    private void getDashboard() {
        pDialog.show();
        Call<DashBoardParser> dashBoardParserCall = apiInterface.getDashboard(sessionManager.getKeyToken(), sessionManager.getKeyFatherName());
        dashBoardParserCall.enqueue(new Callback<DashBoardParser>() {
            @Override
            public void onResponse(Call<DashBoardParser> call, Response<DashBoardParser> response) {
                pDialog.dismiss();
                if (response.body().getSuccess() == 1) {
                    studentModelList = new ArrayList<StudentModel>();
                    studentModelList.addAll(response.body().getData());
                    setSpinnerAdapter();
                    fragment = new RouteViewFragment();
                    if (fragment != null) {
                        replaceFragment(fragment);
                        ftx.commit();
                    }
                } else {
                    showDailog(response.body().getMessage(), response.body().getSuccess());
                }

            }

            @Override
            public void onFailure(Call<DashBoardParser> call, Throwable t) {
                pDialog.dismiss();
            }
        });
    }

    private void endTrip() {
        pDialog.show();
        String token = sessionManager.getKeyToken();
        String busId = sessionManager.getKeyBusId();
        Call<ResponseModel> dashBoardParserCall = apiInterface.endbustrip(token, busId);
        dashBoardParserCall.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                pDialog.dismiss();
                System.out.println(response.body());
                if (response.body().getSuccess() == 1) {

                    showSuccessDailog(response.body().getMessage());
                    sessionManager.setKeyBusId(null);
                    // ll_main.setVisibility(View.GONE);
                    btn_end_trip.setVisibility(View.GONE);
                    tv_alert_box.setText(mResources.getString(R.string.trip_end_msg));

                } else {
                    showDailog(response.body().getMessage(), response.body().getSuccess());
                }

            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {

                pDialog.dismiss();
            }
        });
    }


    private void getDashboardDriver() {
        pDialog.show();
        String token = sessionManager.getKeyToken();
        String busId = sessionManager.getKeyBusId();
        Call<DriverDashResponse> dashBoardParserCall = apiInterface.fetchallbuspickuppoints(token, busId);
        dashBoardParserCall.enqueue(new Callback<DriverDashResponse>() {
            @Override
            public void onResponse(Call<DriverDashResponse> call, Response<DriverDashResponse> response) {
                pDialog.dismiss();
                System.out.println(response.body());
                if (response.body().getSuccess() == 1) {
                    driverDashResponse = response.body();

                    if (driverDashResponse.getChildLocationsData() != null && driverDashResponse.getChildLocationsData().size() == 0) {

                        ll_main.setVisibility(View.VISIBLE);
                        tv_alert_box.setVisibility(View.GONE);
                        tv_alert_box.setVisibility(View.GONE);

                        showDailog("Sorry..No Pickup point found", response.body().getSuccess());
                        bd.putParcelable("RouteInfo", driverDashResponse);
                        fragment = new DriverMapFragment();
                        fragment.setArguments(bd);
                        if (fragment != null) {
                            replaceFragment(fragment);
                            ftx.commit();
                        }

                    } else {
                        // studentModelList = new ArrayList<StudentModel>();
                        //studentModelList.addAll(response.body().getData());
                        //setSpinnerAdapter();
                        bd.putParcelable("RouteInfo", driverDashResponse);

                        fragment = new DriverMapFragment();
                        fragment.setArguments(bd);
                        if (fragment != null) {
                            replaceFragment(fragment);
                            ftx.commit();
                        }
                    }

                } else {
                    showDailog(response.body().getMessage(), response.body().getSuccess());
                }

            }

            @Override
            public void onFailure(Call<DriverDashResponse> call, Throwable t) {

                pDialog.dismiss();
            }
        });
    }

    private void setSpinnerAdapter() {
        studentNameList = new ArrayList<>();
        for (int i = 0; i < studentModelList.size(); i++) {
            studentNameList.add(studentModelList.get(i).getStudentName());

        }
        ArrayAdapter<String> a = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, studentNameList);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        child_spinner.setAdapter(a);
    }

    private void showSuccessDailog(String message) {
        new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("SUCCESS")
                .setContentText("" + message)
                .setConfirmText("OK")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {


                        sDialog.dismiss();
                        Intent intent = new Intent(mContext, DriverBusActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();


                    }
                })
                .show();
    }

    private void showDailog(String message, final int type) {
        new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Alert")
                .setContentText("" + message)
                .setConfirmText("OK")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        if (type == 400)
                            Util.getUserLogout(DashboardActivity.this);
                        else if (type == Constants.LOCATION_ALERT) {
                            sDialog.dismiss();
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            mContext.startActivity(myIntent);
                        } else {
                            sDialog.dismiss();

                        }
                    }
                })
                .show();
    }

}