package com.readytoborad.di.component;

import com.readytoborad.activity.BaseActivity;
import com.readytoborad.activity.LoginActivity;
import com.readytoborad.activity.ParentDashboardActivity;
import com.readytoborad.activity.SetLocationActivity;
import com.readytoborad.activity.SplashActivity;
import com.readytoborad.di.module.ActivityModule;
import com.readytoborad.fragment.AlarmSettingFragment;
import com.readytoborad.fragment.BaseFragment;
import com.readytoborad.fragment.ChangePasswordFragment;
import com.readytoborad.fragment.ParentDashBoardFragment;
import com.readytoborad.fragment.ParentHomeFragment;
import com.readytoborad.fragment.ParentNotifiyFragment;
import com.readytoborad.fragment.ParentSettingFragment;
import com.readytoborad.fragment.PickupPointFragment;
import com.readytoborad.fragment.SetLocationFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {
    @ContributesAndroidInjector(modules = ActivityModule.class)
    abstract BaseActivity baseActivity();

    @ContributesAndroidInjector
    abstract SplashActivity splashActivity();
    @ContributesAndroidInjector
    abstract LoginActivity loginActivity();

    @ContributesAndroidInjector
    abstract ParentDashboardActivity parentDashboardActivity();

    @ContributesAndroidInjector
    abstract SetLocationActivity parentSetLocationActivity();

    @ContributesAndroidInjector
    abstract BaseFragment provideBaseFragment();

    @ContributesAndroidInjector
    abstract ParentDashBoardFragment provideParentDashBoardFragment();

    @ContributesAndroidInjector
    abstract ParentHomeFragment provideParentHomeFragment();

    @ContributesAndroidInjector
    abstract ParentSettingFragment provideParentSettingFragment();

    @ContributesAndroidInjector
    abstract PickupPointFragment providePickupPointFragment();

    @ContributesAndroidInjector
    abstract AlarmSettingFragment provideAlarmSettingFragment();

    @ContributesAndroidInjector
    abstract ChangePasswordFragment provideChangePasswordFragment();

    @ContributesAndroidInjector
    abstract SetLocationFragment provideSetLocationFragment();

    @ContributesAndroidInjector
    abstract ParentNotifiyFragment provideParentNotifiyFragment();

}
