package com.readytoborad.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.readytoborad.R;
import com.readytoborad.adapter.BusRecycleAdapter;
import com.readytoborad.interfaces.ApiInterface;
import com.readytoborad.model.BusModel;
import com.readytoborad.model.DriverDashResponse;
import com.readytoborad.model.ResponseModel;
import com.readytoborad.network.ApiClient;
import com.readytoborad.session.SessionManager;
import com.readytoborad.util.AppUtils;
import com.readytoborad.util.RecyclerItemClickListener;
import com.readytoborad.util.Util;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Vicky Garg on 7/16/2017.
 */

public class DriverBusActivity extends AppCompatActivity {
    Context mContext;
    Resources mResources;
    private SessionManager sessionManager;
    private ApiInterface apiService;
    private SweetAlertDialog pDialog;
    RecyclerView spinner;
    TextView tv_title, tv_msg, mTitle, tv_logout;
    ImageView img_refresh;
    LinearLayout layout;
    Button btn_yes, btn_no;
    CardView cardView;
    RelativeLayout mToolbar;
    BusRecycleAdapter adapter;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
//        Util.setTaskBarColored(this, R.color._transparent, false);
        setContentView(R.layout.activity_driver_bus);

        mContext = this;
        mResources = this.getResources();
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Loading");
        pDialog.setCancelable(false);

        mToolbar = (RelativeLayout) findViewById(R.id.toolbar);
        mToolbar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.login));
        mTitle = (TextView) findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.select_bus_title));

        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        img_refresh.setVisibility(View.GONE);

        tv_logout = (TextView) findViewById(R.id.tv_logout);


        mTitle.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.white)));
//        setSupportActionBar(mToolbar);
       /* getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        findViewById();
        if (!AppUtils.isLocationEnabled(mContext)) {
            AppUtils.showLocationAlertDialog(mContext);
        }
        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

            }
        });

    }

    public void onResume() {
        super.onResume();
        getALLBuses();
    }

    public void findViewById() {
        spinner = (RecyclerView) findViewById(R.id.spinner_buses);
        tv_title = (TextView) findViewById(R.id.tv_bus_title);
        tv_msg = (TextView) findViewById(R.id.tv_bus_msg);
        layout = (LinearLayout) findViewById(R.id.btn_lyt);
        btn_yes = (Button) findViewById(R.id.btn_yes);
        btn_no = (Button) findViewById(R.id.btn_no);
        cardView = (CardView) findViewById(R.id.cardView);


    }

    public void showBuses(final ArrayList<BusModel> busList) {
        adapter = new BusRecycleAdapter(busList);
        cardView.setVisibility(View.VISIBLE);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        spinner.setLayoutManager(mLayoutManager);
        spinner.setVerticalFadingEdgeEnabled(true);

//        spinner.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
//        spinner.setItemAnimator(null);
        spinner.setAdapter(adapter);
        spinner.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Spannable spanned = new SpannableString(busList.get(position).getBusNo());

                spanned.setSpan(new ForegroundColorSpan(Color.RED), 2, busList.get(position).getBusNo().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                tv_title.setText("Confirmation");
                tv_msg.setText(Html.fromHtml("Are you sure, you are driving this " + "<font color='#00ad80'> <b>" + "'" + spanned + "'" + "</b></font> bus ?"));

//                textView.setText(busList.get(position).getBusNo());
                tv_msg.setTag(busList.get(position));
                tv_msg.setVisibility(View.VISIBLE);
                layout.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.GONE);

            }
        }));
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tv_title.setText("Bus List");
                tv_msg.setText("Please select the bus for trip");
                tv_msg.setVisibility(View.GONE);
                layout.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);
            }
        });
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                BusModel busModel = (BusModel) tv_msg.getTag();
                Intent intent = new Intent(mContext, DashboardActivity.class);
                intent.putExtra("bus_model", busModel);
                sessionManager.setKeyBusId("" + busModel.getBusId());
                startActivity(intent);


                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv_msg.setVisibility(View.GONE);
                        layout.setVisibility(View.GONE);
                        spinner.setVisibility(View.VISIBLE);
                    }
                }, 500);


            }
        });

    }

    private void getALLBuses() {
        pDialog.show();
        String token = sessionManager.getKeyToken();
        Call<DriverDashResponse> dashBoardParserCall = apiService.getallbuses(token);
        dashBoardParserCall.enqueue(new Callback<DriverDashResponse>() {
            @Override
            public void onResponse(Call<DriverDashResponse> call, Response<DriverDashResponse> response) {
                pDialog.dismiss();
                if (response.body().getSuccess() == 1) {
                    DriverDashResponse driverDashResponse = response.body();
                    ArrayList<BusModel> busModelList = new ArrayList<BusModel>();
                    busModelList.addAll(response.body().getData());
                    showBuses(busModelList);

                } else {
                    showDailog(response.body().getMessage(), SweetAlertDialog.ERROR_TYPE);
                }

            }

            @Override
            public void onFailure(Call<DriverDashResponse> call, Throwable t) {
                showDailog(t.getMessage(), SweetAlertDialog.ERROR_TYPE);
                pDialog.dismiss();
            }
        });
    }

    private void showDailog(final String message, final int type) {
        new SweetAlertDialog(mContext, type)
                .setTitleText("Alert")
                .setContentText("" + message)
                .setConfirmText("OK")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        if(message.equalsIgnoreCase("Unauthenticated User"))
                            Util.getUserLogout((Activity) mContext);

                        sDialog.dismiss();
                    }
                })
                .show();
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.logout_menu, menu);
//        // menuView=findViewById(R.id.menuitem).getRootView();
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menuitem:
//                View view = findViewById(R.id.menuitem);
//                PopupMenu popupMenu = new PopupMenu(this, view);
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//
//                        switch (item.getItemId()) {
//                            case R.id.profile:
//                                // startActivity(new Intent(DashboardActivity.this, NotificationActivity.class));
//                                break;
//                            case R.id.notification:
//                                startActivity(new Intent(mContext, NotificationActivity.class));
//                                break;
//                            case R.id.settings:
//                                startActivity(new Intent(mContext, SettingsActivity.class));
//                                break;
//                            case R.id.logout:
//                                if (AppUtils.isNetworkAvailable(mContext)) {
//                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                                    builder.setTitle(mResources.getString(R.string.confirmation));
//                                    builder.setMessage(mResources.getString(R.string.logout_alert_msg));
//                                    builder.setPositiveButton(mResources.getString(R.string.yes), new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                                            driverLogout();
//                                        }
//                                    });
//                                    builder.setNegativeButton(mResources.getString(R.string.no), null);
//                                    builder.show();
//
//                                } else
//                                    Util.showNetworkAlertDialog(mContext);
//                                break;
//                        }
//                        return false;
//                    }
//                });
//                popupMenu.getMenuInflater().inflate(R.menu.pop_up, popupMenu.getMenu());
//                popupMenu.show();
//                MenuPopupHelper menuHelper = new MenuPopupHelper(this, (MenuBuilder) popupMenu.getMenu(), view);
//                menuHelper.setForceShowIcon(true);
//                menuHelper.show();
//                //  popup_layout.setVisibility(View.VISIBLE);
//                break;
//            case android.R.id.home:
//                finish();
//                break;
//            case R.id.logout:
//                if (AppUtils.isNetworkAvailable(mContext)) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                    builder.setTitle(mResources.getString(R.string.confirmation));
//                    builder.setMessage(mResources.getString(R.string.logout_alert_msg));
//                    builder.setPositiveButton(mResources.getString(R.string.yes), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                            driverLogout();
//                        }
//                    });
//                    builder.setNegativeButton(mResources.getString(R.string.no), null);
//                    builder.show();
//
//                } else
//                    Util.showNetworkAlertDialog(mContext);
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
    private void driverLogout() {
        pDialog.show();
        String token = sessionManager.getKeyToken();
        Call<ResponseModel> dashBoardParserCall = apiService.logout(token);
        dashBoardParserCall.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                pDialog.dismiss();
                System.out.println(response.body());
                if (response.body().getSuccess() == 1) {

                    AppUtils.showToast(mContext, response.body().getMessage());
                    Util.getUserLogout((Activity) mContext);

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

}
