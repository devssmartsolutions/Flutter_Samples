package com.readytoborad.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.readytoborad.R;
import com.readytoborad.interfaces.ApiInterface;
import com.readytoborad.model.CommonResponse;
import com.readytoborad.session.SessionManager;
import com.readytoborad.util.EmailValidator;
import com.readytoborad.util.Util;

import javax.inject.Inject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ForgotActivity extends BaseActivity implements View.OnClickListener {
    private Context mContext;
    private TextView text_forgot, text_return;
    private EditText input_email;
    private Button buttonSend;
    private Toolbar mToolbar;
    private TextView mTitle;
    private Animation shakeAnimation;
    private String email;
    private SweetAlertDialog pDialog;
    @Inject
    ApiInterface apiInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTaskBarColored(this, R.color.light_transparent, false);
        setContentView(R.layout.activity_forgot);
        AndroidInjection.inject(this);
        mContext = this;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setBackgroundColor(ContextCompat.getColor(mContext, R.color._transparent));
        mTitle = (TextView) findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.forgot_pass));
        mTitle.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.white)));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        init();
    }

    private void init() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Loading");
        pDialog.setCancelable(false);
        shakeAnimation = AnimationUtils.loadAnimation(mContext, R.anim.shake);
        text_forgot = (TextView) findViewById(R.id.text_forgot);
        text_return = (TextView) findViewById(R.id.text_return);
        input_email = (EditText) findViewById(R.id.input_email);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        doColorSpanForSecondString("Return to ", "Login in", text_return);
        buttonSend.setOnClickListener(this);
        text_return.setOnClickListener(this);


    }

    private boolean validate() {
        email = input_email.getText().toString().trim();

        if (email.equals("")) {

            input_email.setError(getResources().getString(R.string.input_error));
            input_email.startAnimation(shakeAnimation);

            // utilAlert.showSnackBar(resources.getString(R.string.empty_field));
            //utilAlert.showError(et_username);
            return false;
        } else if (!new EmailValidator().validate(email)) {
            //utilAlert.showSnackBar(resources.getString(R.string.valid_email));

            input_email.setError(getResources().getString(R.string.valid_email));
            input_email.startAnimation(shakeAnimation);
            // utilAlert.showError(et_username);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSend:
                if (validate()) {
                    forgotPassword();
                }
                break;
            case R.id.text_return:
                onBackPressed();
                break;
        }
    }

    private void doColorSpanForSecondString(String firstString, String lastString, TextView txtSpan) {

        String changeString = (lastString != null ? lastString : "");
        String totalString = firstString + changeString;
        Spannable spanText = new SpannableString(totalString);
        spanText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.white)), 0, firstString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.login)), String.valueOf(firstString)
                .length(), totalString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtSpan.setText(spanText);
        txtSpan.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void forgotPassword() {
        pDialog.show();
        SessionManager sessionManager = new SessionManager(mContext);
        Log.i("Authoriztion", "" + sessionManager.getKeyToken());
        Call<CommonResponse> submitResponse = apiInterface.forgotPassword(sessionManager.getKeyToken(), email);
        submitResponse.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                pDialog.dismiss();
                try {
                    Log.i("TAG ", "" + response.body().getSuccess());
                    if (response.body().getSuccess() == 1) {
                        showDailogSuccess(response.body().getMessage());
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
                            Util.getUserLogout(ForgotActivity.this);
                        else
                            sDialog.dismiss();
                    }
                })
                .show();
    }


    private void showDailogSuccess(String message) {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Alert")
                .setContentText("" + message)
                .setConfirmText("OK")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        startActivity(new Intent(ForgotActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ForgotActivity.this, LoginActivity.class));
        finish();
    }
}
