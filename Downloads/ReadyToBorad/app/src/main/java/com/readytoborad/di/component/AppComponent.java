package com.readytoborad.di.component;

import android.app.Application;

import com.readytoborad.App;
import com.readytoborad.di.ApplicationScope;
import com.readytoborad.di.module.AppModule;
import com.readytoborad.di.module.RetrofitModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {ActivityBindingModule.class, AppModule.class, RetrofitModule.class, AndroidSupportInjectionModule.class})
@ApplicationScope
public interface AppComponent extends AndroidInjector<App> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        AppComponent.Builder application(Application application);

        AppComponent build();
    }

    @Override
    void inject(App app);


}
