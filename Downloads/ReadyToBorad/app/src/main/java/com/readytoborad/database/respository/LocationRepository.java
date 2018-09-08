package com.readytoborad.database.respository;

import com.readytoborad.database.LocationData;

public interface LocationRepository {
   // LiveData<Product> findById(int id);

   // LiveData<List<Product>> findAll();

    long insert(LocationData product);

    int delete(LocationData product);
}
