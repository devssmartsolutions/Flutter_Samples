package com.readytoborad.database.respository;

import com.readytoborad.database.ChildData;

import java.util.List;

public interface ChildDataRepository {
    List<ChildData> getAll();

    void insertChildData(ChildData childData);

    void insertAllChildData(List<ChildData> childDataList);

    void updateChildData(ChildData childData);

    ChildData getChildData(String childId);
}
