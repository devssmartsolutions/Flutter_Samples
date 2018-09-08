package com.readytoborad.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.readytoborad.R;
import com.readytoborad.adapter.BusRecycleAdapter;
import com.readytoborad.database.MySharedPreferences;
import com.readytoborad.interfaces.ApiInterface;
import com.readytoborad.model.BusModel;
import com.readytoborad.model.DriverDashResponse;
import com.readytoborad.util.AppUtils;
import com.readytoborad.util.RecyclerItemClickListener;
import com.readytoborad.util.Util;

import java.util.ArrayList;

import javax.inject.Inject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashActivity extends BaseActivity {
    private static final long SPLASH_TIME = 3 * 1000;
    private SweetAlertDialog pDialog;
    private Context mContext;
    AlertDialog alertDialog;
    @Inject
    ApiInterface apiInterface;
    @Inject
    MySharedPreferences mySharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTaskBarColored(this, R.color._transparent, false);
        setContentView(R.layout.activity_splash);
        AndroidInjection.inject(this);
        mContext = this;
        pDialog = AppUtils.getDialog(this);
        mySharedPreferences.putData(MySharedPreferences.PREFS_FIREBASE_TOKEN, FirebaseInstanceId.getInstance().getToken());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Class aClass = null;
                if (mySharedPreferences.getBooleanData(MySharedPreferences.PREFS_IS_LOGIN)) {
                    if (mySharedPreferences.getStringData(MySharedPreferences.PREFS_USER_TYPE).equalsIgnoreCase(getResources().getString(R.string.parent))) {
                        if (mySharedPreferences.getIntData(MySharedPreferences.PREFS_POPUP_FLAG) == 1) {
                            aClass = ParentSavePickUpPointActivity.class;
                        } else {

                            aClass = ParentDashboardActivity.class;
                        }
                    } else {

                            if (mySharedPreferences.getIntData(MySharedPreferences.PREFS_POPUP_FLAG) == 1) {
                                aClass = DriverBusActivity.class;
                            } else {

                                aClass = DashboardActivity.class;
                            }
                    }


                } else {
                    aClass = LoginActivity.class;
                }
                if (aClass != null) {
                    startActivity(new Intent(SplashActivity.this, aClass));
                    finish();
                }
            }
        }, SPLASH_TIME);
    }

    private void getDashboardDriver() {
        pDialog.show();
        Call<DriverDashResponse> dashBoardParserCall = apiInterface.getallbuses("");
        dashBoardParserCall.enqueue(new Callback<DriverDashResponse>() {
            @Override
            public void onResponse(Call<DriverDashResponse> call, Response<DriverDashResponse> response) {
                pDialog.dismiss();
                if (response.body().getSuccess() == 1) {
                    DriverDashResponse driverDashResponse = response.body();
                    ArrayList<BusModel> busModelList = new ArrayList<BusModel>();
                    busModelList.addAll(response.body().getData());
                    showDialogForBuses(busModelList);

                } else {
                    showDailog(response.body().getMessage());
                }

            }

            @Override
            public void onFailure(Call<DriverDashResponse> call, Throwable t) {
                showDailog(t.getMessage());
                pDialog.dismiss();
            }
        });
    }

    private void showDailog(String message) {
        new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Alert")
                .setContentText("" + message)
                .setConfirmText("OK")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {


                        sDialog.dismiss();
                    }
                })
                .show();
    }

    private void showSuccessDailog(String message) {
        new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Success")
                .setContentText("" + message)
                .setConfirmText("OK")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {


                        sDialog.dismiss();
                    }
                })
                .show();
    }

    private void showDialogForBuses(final ArrayList<BusModel> busList) {
        //  final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this,R.style.CustomDialog);
        final Dialog dialogBuilder = new Dialog(this, R.style.CustomDialog);
// ...Irrelevant code for customizing the buttons and title
//        dialogBuilder.setTitle("Bus List");
//        dialogBuilder.setMessage("Please select the bus for trip");
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_driver_bus, null);
        dialogBuilder.setContentView(dialogView);

        final RecyclerView spinner = (RecyclerView) dialogView.findViewById(R.id.spinner_buses);
        final TextView tv_title = (TextView) dialogView.findViewById(R.id.tv_bus_title);
        final TextView tv_msg = (TextView) dialogView.findViewById(R.id.tv_bus_msg);
        final LinearLayout layout = (LinearLayout) dialogView.findViewById(R.id.btn_lyt);
        Button btn_yes = (Button) dialogView.findViewById(R.id.btn_yes);
        Button btn_no = (Button) dialogView.findViewById(R.id.btn_no);
        BusRecycleAdapter adapter = new BusRecycleAdapter(busList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        spinner.setLayoutManager(mLayoutManager);
        // notification_list.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        spinner.setItemAnimator(new DefaultItemAnimator());
        spinner.setAdapter(adapter);
        spinner.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Spannable spanned = new SpannableString(busList.get(position).getBusNo());

                spanned.setSpan(new ForegroundColorSpan(Color.RED), 2, busList.get(position).getBusNo().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                tv_title.setText("Confirmation");
                tv_msg.setText(Html.fromHtml("Are you want to start trip with <font color='green'> <b>" + spanned + "</b></font> bus ?"));

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
                tv_msg.setVisibility(View.VISIBLE);
                layout.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);
            }
        });
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogBuilder.dismiss();
                BusModel busModel = (BusModel) tv_msg.getTag();
                Intent intent = new Intent(mContext, DashboardActivity.class);
                intent.putExtra("bus_model", busModel);
               // sessionManager.setKeyBusId("" + busModel.getBusId());
                startActivity(intent);
                finish();
            }
        });

//        alertDialog = dialogBuilder.create();
//        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBuilder.setCanceledOnTouchOutside(false);
        dialogBuilder.show();
    }

}
