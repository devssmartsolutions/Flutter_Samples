package com.readytoborad.job;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.RetryConstraint;

import retrofit2.Response;

public class LoginJob extends BaseJob {

    public LoginJob(Context context, int priority, String group, JobCallback jobCallback) {
        super(context, priority, group, jobCallback);
    }

    @Override
    public void onSuccess(Response<?> response) {
        //  jobCallback.onSuccess();

    }

    @Override
    public void onFailure(Response<?> response) {

    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        // Call<ResponseModel>call=apiInterface.getuserlogin();
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
