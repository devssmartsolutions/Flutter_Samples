package com.readytoborad.database.respository;

import com.readytoborad.database.ChildData;
import com.readytoborad.database.dao.ChildDao;

import java.util.List;

import javax.inject.Inject;

public class ChildDataSource implements ChildDataRepository {


    ChildDao childDao;

    @Inject
    public ChildDataSource(ChildDao childDao) {
        this.childDao = childDao;
    }

    @Override
    public List<ChildData> getAll() {
        return childDao.getAll();
    }

    @Override
    public void insertChildData(ChildData childData) {
        childDao.insert(childData);

    }

    @Override
    public void insertAllChildData(List<ChildData> childDataList) {
        childDao.insertAll(childDataList);
    }

    @Override
    public void updateChildData(ChildData childData) {
        childDao.update(childData);

    }

    @Override
    public ChildData getChildData(String childId) {
        return childDao.getChildInfo(childId);
    }
}
