package com.readytoborad.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.readytoborad.R;
import com.readytoborad.adapter.BusRecycleAdapter;
import com.readytoborad.database.MySharedPreferences;
import com.readytoborad.database.respository.ChildDataRepository;
import com.readytoborad.interfaces.ApiInterface;
import com.readytoborad.model.BusModel;
import com.readytoborad.model.DriverDashResponse;
import com.readytoborad.model.ResponseModel;
import com.readytoborad.util.AppUtils;
import com.readytoborad.util.EmailValidator;
import com.readytoborad.util.RecyclerItemClickListener;
import com.readytoborad.util.Util;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.readytoborad.database.MySharedPreferences.PREFS_IS_LOGIN;
import static com.readytoborad.database.MySharedPreferences.PREFS_KEY_USER_ID;
import static com.readytoborad.database.MySharedPreferences.PREFS_SESSION_TOKEN;
import static com.readytoborad.database.MySharedPreferences.PREFS_USER_TYPE;


public class LoginActivity extends BaseActivity {
    private Context context;
    @BindView(R.id.parentLogin)
    Button parentLogin;
    @BindView(R.id.driverLogin)
    Button driverLogin;
    @BindView(R.id.input_username)
    EditText input_username;
    @BindView(R.id.input_phone)
    EditText input_phone;
    @BindView(R.id.input_password)
    EditText input_password;
    @BindView(R.id.buttonLogin)
    Button loginButton;
    @BindView(R.id.textForgotPassword)
    TextView forgotPassword;
    @BindView(R.id.text_driver)
    TextView text_driver;
    @BindView(R.id.text_parent)
    TextView text_parent;
    @BindView(R.id.tv_logout)
    TextView tv_logout;
    AlertDialog alertDialog;
    Context mContext;
    @BindView(R.id.login_layout)
    LinearLayout login_layout;
    @BindView(R.id.login_layout1)
    LinearLayout login_layout1;
    @BindView(R.id.main_layout)
    LinearLayout main_layout;
    @BindView(R.id.img_refresh)
    ImageView img_refresh;

    private String user_name, password;
    private boolean isDriver = false;
    private Resources resources;
    private Animation shakeAnimation;
    private SweetAlertDialog pDialog;
    private String usertype;
    Rect displayRectangle;

    @Inject
    ApiInterface apiInterface;
    @Inject
    ChildDataRepository childDataRepository;
    @Inject
    MySharedPreferences mySharedPreferences;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTaskBarColored(this, R.color._transparent, false);
        setContentView(R.layout.activity_login);
        AndroidInjection.inject(this);
        ButterKnife.bind(this);
        context = this;
        resources = getResources();
        pDialog = AppUtils.getDialog(this);
        init();
        mySharedPreferences.putData(MySharedPreferences.PREFS_FIREBASE_TOKEN,FirebaseInstanceId.getInstance().getToken());

    }


    private void init() {
        shakeAnimation = AnimationUtils.loadAnimation(context, R.anim.shake);
        img_refresh.setVisibility(View.GONE);
        tv_logout.setVisibility(View.GONE);
        usertype = getResources().getString(R.string.parent);
        text_parent.setPaintFlags(text_parent.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        text_driver.setPaintFlags(text_driver.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        parentLogin.setPaintFlags(parentLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        driverLogin.setPaintFlags(driverLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        forgotPassword.setPaintFlags(text_parent.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        loginButton.setPaintFlags(loginButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        mContext = this;
        Intent in = getIntent();
        if (in != null) {
            String user_type = in.getStringExtra("user_type");
            if (user_type != null && user_type.equalsIgnoreCase("driver")) {
                setDriverLoginLayout();
            } else
                setParentLoginLayout();
        }

    }

    public void setParentLoginLayout() {
        login_layout.setVisibility(View.VISIBLE);
        login_layout1.setVisibility(View.GONE);
        input_phone.setVisibility(View.GONE);
        input_username.setVisibility(View.VISIBLE);
        forgotPassword.setVisibility(View.VISIBLE);
        isDriver = false;
        usertype = getResources().getString(R.string.parent);
    }

    public void setDriverLoginLayout() {
        login_layout1.setVisibility(View.VISIBLE);
        login_layout.setVisibility(View.GONE);
        input_phone.setVisibility(View.VISIBLE);
        input_username.setVisibility(View.GONE);
        forgotPassword.setVisibility(View.INVISIBLE);
        isDriver = true;
        usertype = getResources().getString(R.string.driver);
    }

    @OnClick({R.id.text_parent, R.id.text_driver, R.id.buttonLogin, R.id.textForgotPassword})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_parent:
                setParentLoginLayout();
                break;
            case R.id.text_driver:
                setDriverLoginLayout();
                break;
            case R.id.buttonLogin:
                if (validate()) {
                    callLogin();
                    /* */
                }
                break;
            case R.id.textForgotPassword:
                startActivity(new Intent(LoginActivity.this, ForgotActivity.class));
                finish();
                break;
        }
    }

    private boolean validate() {
        user_name = input_username.getText().toString().trim();
        if (isDriver)
            user_name = input_phone.getText().toString().trim();
        password = input_password.getText().toString().trim();

        if (user_name.equals("")) {
            if (!isDriver) {
                input_username.setError(resources.getString(R.string.input_error));
                input_username.startAnimation(shakeAnimation);
            } else {
                input_phone.setError(resources.getString(R.string.input_phone));
                input_phone.startAnimation(shakeAnimation);
            }

            // utilAlert.showSnackBar(resources.getString(R.string.empty_field));
            //utilAlert.showError(et_username);
            return false;
        } else if (!new EmailValidator().validate(user_name) && !isDriver) {
            //utilAlert.showSnackBar(resources.getString(R.string.valid_email));

            input_username.setError(resources.getString(R.string.valid_email));
            input_username.startAnimation(shakeAnimation);
            // utilAlert.showError(et_username);
            return false;
        } else if (password.equals("")) {
            //  utilAlert.showSnackBar(resources.getString(R.string.empty_password));
            input_password.setError(resources.getString(R.string.input_password));
            input_password.startAnimation(shakeAnimation);
            // utilAlert.showError(et_pwd);

            return false;
        }
        return true;
    }



    private void callLogin() {
        pDialog.show();

        Call<ResponseModel> loginResponse = apiInterface.getuserlogin(user_name, password, mySharedPreferences.getStringData(MySharedPreferences.PREFS_FIREBASE_TOKEN), "Android", usertype);
        loginResponse.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {


                try {
                    if (response.body().getSuccess() == 1) {
                        mySharedPreferences.putData(PREFS_SESSION_TOKEN, response.body().getToken());
                        mySharedPreferences.putData(PREFS_IS_LOGIN, true);
                        mySharedPreferences.putData(PREFS_KEY_USER_ID, response.body().getData().getParentId());
                        mySharedPreferences.putData(PREFS_USER_TYPE, response.body().getUserType());
                       // sessionManager.createLoginSession(response.body().getData(), response.body().getUserType(), response.body().getToken());

                        if (response.body().getUserType().equalsIgnoreCase(getResources().getString(R.string.parent))) {


                            ResponseModel model = response.body();
                            if (null != model) {
                                new InsertChildData(model).execute();
                            }


                        } else {


                            //  showSuccessDailog(response.body().getMessage());
                            ResponseModel model = response.body();
                          //  sessionManager.setKeyUserType("driver");
                            if (model.getData() != null && model.getData().getPopupFlag() == 0) {
                                main_layout.setVisibility(View.GONE);
                                //   sessionManager.setKeyBusId(model.getData().get());
                                Intent intent = new Intent(mContext, DashboardActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                main_layout.setVisibility(View.GONE);
                                Intent intent = new Intent(mContext, DriverBusActivity.class);
                                startActivity(intent);
                                finish();
                            }


//                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
//                            finish();
                        }
                    } else {
                        showDailogFail(response.body().getMessage());
                        //Toast.makeText(context, , Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                pDialog.dismiss();
                showDailogFail(t.getMessage());

            }
        });
    }

    private class InsertChildData extends AsyncTask<Void, Void, Void> {

        //Prevent leak
        //  private WeakReference<Activity> weakActivity;
        private String email;
        private String phone;
        private String license;
        ResponseModel model;

        public InsertChildData(ResponseModel model) {
            //weakActivity = new WeakReference<>(activity);
            this.model = model;
        }

        @Override
        protected Void doInBackground(Void... params) {
            childDataRepository.insertAllChildData(model.getData().getChildrenArr());
            return null;
        }

        @Override
        protected void onPostExecute(Void agentsCount) {
            pDialog.dismiss();
            if (model.getData() != null && model.getData().getPopupFlag() == 1) {
                Intent intent = new Intent(LoginActivity.this, ParentSavePickUpPointActivity.class);
                // intent.putParcelableArrayListExtra("childObj", model.getData().getChildrenArr());
                startActivity(intent);
                finish();
            } else {

                Intent intent = new Intent(LoginActivity.this, ParentDashboardActivity.class);
                // intent.putParcelableArrayListExtra("childObj", model.getData().getChildrenArr());
                startActivity(intent);

                finish();
            }
        }
    }


    private void getDashboardDriver() {
        pDialog.setTitleText("Loading buses");
        pDialog.show();
        Call<DriverDashResponse> dashBoardParserCall = apiInterface.getallbuses(mySharedPreferences.getStringData(MySharedPreferences.PREFS_SESSION_TOKEN));
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
                    showDailog(response.body().getMessage(), response.body().getSuccess());
                }

            }

            @Override
            public void onFailure(Call<DriverDashResponse> call, Throwable t) {

                pDialog.dismiss();
            }
        });
    }

    private void showDialogForBuses(final ArrayList<BusModel> busList) {
//        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this,R.style.CustomDialog);
        final Dialog dialogBuilder = new Dialog(this, R.style.CustomDialog);
// ...Irrelevant code for customizing the buttons and title
//        dialogBuilder.setTitle("Bus List");
//        dialogBuilder.setMessage("Please select the bus for trip");
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_driver_bus, null);
        dialogBuilder.setContentView(dialogView);

       /* alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();*/
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
        dialogBuilder.setCanceledOnTouchOutside(false);
        dialogBuilder.show();
    }

    private void showDailog(String message, final int type) {
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Alert")
                .setContentText("" + message)
                .setConfirmText("OK")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        /*if (type == 400) {
                            //Util.getUserLogout(LoginActivity.this);
                        } else*/
                            sDialog.dismiss();
                    }
                })
                .show();
    }

    private void showSuccessDailog(String message) {
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("SUCCESS")
                .setContentText("" + message)
                .setConfirmText("OK")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        main_layout.setVisibility(View.GONE);
                        sDialog.dismiss();
                        Intent intent = new Intent(mContext, DriverBusActivity.class);
                        startActivity(intent);
                        finish();

                    }
                })
                .show();
    }

    private void showDailogFail(String message) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
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

    private void showDailogSuccess(String message) {
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Confirmation")
                .setContentText("Are you want to start trip with " + message + " bus ?")
                .setConfirmText(resources.getString(R.string.yes))
                .showCancelButton(true).setCancelText("No,cancel plx!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        alertDialog.dismiss();
                        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                        finish();
                    }
                })
                .show();
    }
}
