package com.readytoborad.database.respository;

import com.readytoborad.database.LocationData;
import com.readytoborad.database.dao.LocationDao;

import javax.inject.Inject;

public class LocationDataSource implements LocationRepository {

    private LocationDao locationDao;

    @Inject
    public LocationDataSource(LocationDao locationDao) {
        this.locationDao = locationDao;
    }

    @Override
    public long insert(LocationData locationData) {
        return locationDao.insert(locationData);
    }

    @Override
    public int delete(LocationData locationData) {
        return locationDao.delete(locationData);
    }
}