package com.readytoborad;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.firebase.iid.FirebaseInstanceId;
import com.readytoborad.di.component.DaggerAppComponent;
import com.readytoborad.util.FontsOverride;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

/**
 * Created by anchal.kumar on 10/11/2017.
 */

public class App extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> activityDispatchingAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        // setupGraph();
      //  FontsOverride.setDefaultFont(this, "DEFAULT", "gillsans.ttf");
       // FontsOverride.setDefaultFont(this, "MONOSPACE", "gillsans.ttf");
      //  FontsOverride.setDefaultFont(this, "SERIF", "gillsans.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "gillsans.ttf");

        DaggerAppComponent.builder().application(this)
                .build().inject(this);
        FirebaseInstanceId.getInstance().getToken();
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityDispatchingAndroidInjector;
    }
}
