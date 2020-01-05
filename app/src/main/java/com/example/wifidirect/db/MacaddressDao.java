package com.example.wifidirect.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


@Dao
public interface MacaddressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long createMacaddress(Macaddress macaddress);

    @Query("SELECT id FROM macaddress WHERE partnermac = :partnermacAddress ")
    Integer getIdIfExists(String partnermacAddress);

}
