package com.readytoborad.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
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
import com.readytoborad.util.Util;

import javax.inject.Inject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by harendrasinghbisht on 16/01/17.
 */

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {
    private Context mContext;
    // private TextView text_forgot, text_return;
    private EditText input_old_pass, input_new_pass, input_confirm_pass;//input_userid
    private Button buttonSave;
    private Toolbar mToolbar;
    private TextView mTitle;
    private Animation shakeAnimation;
    private String user_id, old_pass, newpass, confirm_pass;
    private SweetAlertDialog pDialog;
    private SessionManager sessionManager;
   @Inject
   ApiInterface apiInterface;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTaskBarColored(this, R.color.light_transparent, false);
        setContentView(R.layout.fragment_change_pass);
        AndroidInjection.inject(this);
        mContext = this;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setBackgroundColor(ContextCompat.getColor(mContext, R.color._transparent));
        mTitle = (TextView) findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.change_pass));
        mTitle.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.white)));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        init();
    }

    private void init() {
        shakeAnimation = AnimationUtils.loadAnimation(mContext, R.anim.shake);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Loading");
        pDialog.setCancelable(false);
        // input_userid = (EditText) findViewById(input_userid);
        input_old_pass = (EditText) findViewById(R.id.input_old_pass);
        input_new_pass = (EditText) findViewById(R.id.input_new_pass);
        input_confirm_pass = (EditText) findViewById(R.id.input_confirm_pass);
        buttonSave = (Button) findViewById(R.id.button_save);
        buttonSave.setOnClickListener(this);

    }

    private boolean validate() {
        sessionManager = new SessionManager(mContext);
        // user_id = input_userid.getText().toString().trim();
        old_pass = input_old_pass.getText().toString().trim();
        newpass = input_new_pass.getText().toString().trim();
        confirm_pass = input_confirm_pass.getText().toString().trim();
        /*if (user_id.isEmpty()) {
            input_userid.setError(getResources().getString(R.string.enter_userid));
            input_userid.startAnimation(shakeAnimation);
            return false;
        } else if (sessionManager.getKeyUserType().equalsIgnoreCase(getResources().getString(R.string.parent)) && !new EmailValidator().validate(user_id)) {
            input_userid.setError(getResources().getString(R.string.valid_email));
            input_userid.startAnimation(shakeAnimation);
            return false;

        } else */
        if (old_pass.isEmpty()) {
            input_old_pass.setError(getResources().getString(R.string.enter_old));
            input_old_pass.startAnimation(shakeAnimation);
            return false;

        } else if (newpass.isEmpty()) {
            input_new_pass.setError(getResources().getString(R.string.enter_new_pass));
            input_new_pass.startAnimation(shakeAnimation);
            return false;
        } else if (confirm_pass.isEmpty()) {
            input_confirm_pass.setError(getResources().getString(R.string.enter_confirm_pass));
            input_confirm_pass.startAnimation(shakeAnimation);
            return false;
        } else if (!newpass.equals(confirm_pass)) {
            input_confirm_pass.setError(getResources().getString(R.string.password_unmatch));
            input_confirm_pass.startAnimation(shakeAnimation);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_save:
                if (validate()) {
                    changePassword();
                }
                break;
        }
    }

    private void changePassword() {
        pDialog.show();
        SessionManager sessionManager = new SessionManager(mContext);
        Log.i("Authoriztion", "" + sessionManager.getKeyToken());
        Call<CommonResponse> submitResponse = apiInterface.changePassword(sessionManager.getKeyToken(), old_pass, confirm_pass);
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
                            Util.getUserLogout(ChangePasswordActivity.this);
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
                        Intent intent = new Intent();
                        setResult(0, intent);
                        finish();
                    }
                })
                .show();
    }
}
