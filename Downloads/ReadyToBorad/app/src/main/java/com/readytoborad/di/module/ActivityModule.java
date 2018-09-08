package com.readytoborad.di.module;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.readytoborad.activity.BaseActivity;
import com.readytoborad.activity.LoginActivity;
import com.readytoborad.activity.ParentDashboardActivity;
import com.readytoborad.activity.SetLocationActivity;
import com.readytoborad.activity.SplashActivity;
import com.readytoborad.di.component.ActivityContext;
import com.readytoborad.fragment.AlarmSettingFragment;
import com.readytoborad.fragment.BaseFragment;
import com.readytoborad.fragment.ChangePasswordFragment;
import com.readytoborad.fragment.ParentDashBoardFragment;
import com.readytoborad.fragment.ParentHomeFragment;
import com.readytoborad.fragment.ParentNotifiyFragment;
import com.readytoborad.fragment.ParentSettingFragment;
import com.readytoborad.fragment.PickupPointFragment;
import com.readytoborad.fragment.SetLocationFragment;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ActivityModule {

    @Binds
    @ActivityContext
    abstract Activity provideActivity(BaseActivity baseActivity);

    @Binds
    @ActivityContext
    abstract Activity provideSplashActivity(SplashActivity splashActivity);
    @Binds
    @ActivityContext
    abstract Activity provideLoginActivity(LoginActivity loginActivity);

    @Binds
    @ActivityContext
    abstract Activity provideParentDashboardActivity(ParentDashboardActivity parentDashboardActivity);

    @Binds
    @ActivityContext
    abstract Activity provideSetLocationActivity(SetLocationActivity setLocationActivity);

    @Binds
    abstract Fragment provideBaseFragment(BaseFragment baseFragment);

    @Binds
    abstract Fragment provideParentHomeFragment(ParentHomeFragment parentHomeFragment);

    @Binds
    abstract Fragment provideParentDashBoardFragment(ParentDashBoardFragment parentDashBoardFragment);

    @Binds
    abstract Fragment provideParentSettingFragment(ParentSettingFragment parentSettingFragment);

    @Binds
    abstract Fragment providePickupPointFragment(PickupPointFragment pickupPointFragment);

    @Binds
    abstract Fragment provideAlarmSettingFragment(AlarmSettingFragment alarmSettingFragment);

    @Binds
    abstract Fragment provideChangePasswordFragment(ChangePasswordFragment changePasswordFragment);

    @Binds
    abstract Fragment provideSetLocationFragment(SetLocationFragment setLocationFragment);

    @Binds
    abstract Fragment provideParentNotifiyFragment(ParentNotifiyFragment parentNotifiyFragment);


}
