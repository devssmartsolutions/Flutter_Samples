package com.readytoborad.di.module;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;

import com.readytoborad.database.MySharedPreferences;
import com.readytoborad.database.RTBDatabase;
import com.readytoborad.database.dao.ChildDao;
import com.readytoborad.database.dao.LocationDao;
import com.readytoborad.database.respository.ChildDataRepository;
import com.readytoborad.database.respository.ChildDataSource;
import com.readytoborad.database.respository.LocationDataSource;
import com.readytoborad.database.respository.LocationRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Singleton
    @Provides
    Context provideContext(Application application) {
        return application.getApplicationContext();
    }

    @Singleton
    @Provides
    RTBDatabase providesRoomDatabase(Context context) {
        return Room.databaseBuilder(context, RTBDatabase.class, "rtb.db").build();
    }

    @Singleton
    @Provides
    LocationDao providesLocationDao(RTBDatabase demoDatabase) {
        return demoDatabase.locationDao();
    }

    @Singleton
    @Provides
    ChildDao providesChildDao(RTBDatabase demoDatabase) {
        return demoDatabase.childDao();
    }

    @Singleton
    @Provides
    LocationRepository locationRepository(LocationDao locationDao) {
        return new LocationDataSource(locationDao);
    }

    @Singleton
    @Provides
    ChildDataRepository childDataRepository(ChildDao childDao) {
        return new ChildDataSource(childDao);
    }
@Singleton
    @Provides
    SharedPreferences provideSharedPreferences(Context context) {
        return context.getSharedPreferences(MySharedPreferences.PREFS_FILE_APP_DATA,Context.MODE_PRIVATE);
    }

}
