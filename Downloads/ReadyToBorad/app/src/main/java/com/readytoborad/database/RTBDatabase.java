package com.readytoborad.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.readytoborad.database.dao.ChildDao;
import com.readytoborad.database.dao.LocationDao;

@Database(entities = {LocationData.class,ChildData.class}, version = 2, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class RTBDatabase extends RoomDatabase {
    public abstract LocationDao locationDao();

    public abstract ChildDao childDao();
}
