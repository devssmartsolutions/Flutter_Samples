package com.readytoborad.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.readytoborad.R;
import com.readytoborad.database.MySharedPreferences;
import com.readytoborad.interfaces.ApiInterface;
import com.readytoborad.interfaces.DialogCallBack;
import com.readytoborad.model.CommonResponse;
import com.readytoborad.util.AppUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.android.support.AndroidSupportInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordFragment extends BaseFragment implements DialogCallBack {
    @BindView(R.id.input_old_pass)
    EditText oldPasswordEditText;
    @BindView(R.id.input_new_pass)
    EditText newPasswordEditText;
    @BindView(R.id.input_confirm_pass)
    EditText confirmPasswordEditText;
    @BindView(R.id.button_save)
    Button savePasswordButton;
    @Inject
    ApiInterface apiInterface;
    @Inject
    MySharedPreferences mySharedPreferences;
    private SweetAlertDialog sweetAlertDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_change_pass, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, getView());
        sweetAlertDialog = AppUtils.getDialog(getActivity());
    }

    @OnClick(R.id.button_save)
    public void savePassword(View view) {
        if (validateData()) {
            changePassword();
        }

    }

    private boolean validateData() {

        if (oldPasswordEditText.getText().toString().trim().isEmpty()) {
            oldPasswordEditText.setError(getResources().getString(R.string.enter_old));
            //input_old_pass.startAnimation(shakeAnimation);
            return false;

        } else if (newPasswordEditText.getText().toString().trim().isEmpty()) {
            newPasswordEditText.setError(getResources().getString(R.string.enter_new_pass));
            // input_new_pass.startAnimation(shakeAnimation);
            return false;
        } else if (confirmPasswordEditText.getText().toString().trim().isEmpty()) {
            confirmPasswordEditText.setError(getResources().getString(R.string.enter_confirm_pass));
            // input_confirm_pass.startAnimation(shakeAnimation);
            return false;
        } else if (!newPasswordEditText.getText().toString().trim().equals(confirmPasswordEditText.getText().toString().trim())) {
            confirmPasswordEditText.setError(getResources().getString(R.string.password_unmatch));
            //input_confirm_pass.startAnimation(shakeAnimation);
            return false;
        }
        return true;
    }

    private void changePassword() {
        sweetAlertDialog.show();

        Call<CommonResponse> loginResponse = apiInterface.changePassword(oldPasswordEditText.getText().toString().trim(), newPasswordEditText.getText().toString().trim(), mySharedPreferences.getStringData(MySharedPreferences.PREFS_SESSION_TOKEN));
        loginResponse.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {

                sweetAlertDialog.dismiss();
                try {
                    if (response.body().getSuccess() == 1) {
                        AppUtils.showSuccess(getContext(), response.body().getMessage(), ChangePasswordFragment.this);
                    } else {
                        AppUtils.showErrorDialog(getContext(), response.body().getMessage());
                    }

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                sweetAlertDialog.dismiss();
                AppUtils.showErrorDialog(getContext(), t.getMessage());

            }
        });
    }


    @Override
    public void onClickAction() {
        //
    }
}
