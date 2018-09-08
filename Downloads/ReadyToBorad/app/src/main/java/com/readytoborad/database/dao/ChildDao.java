package com.readytoborad.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.readytoborad.database.ChildData;

import java.util.List;

@Dao
public interface ChildDao {
    @Query("SELECT * FROM childdata")
    List<ChildData> getAll();

    @Query("SELECT * FROM childdata WHERE childId=:childId")
    ChildData getChildInfo(String childId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ChildData> childDataList);

    @Insert
    long insert(ChildData childData);

    @Update
    void update(ChildData childData);

    @Delete
    int delete(ChildData childData);
}
