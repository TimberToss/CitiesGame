package com.example.speechrecognition.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.speechrecognition.data.entity.City;

import java.util.List;

@Dao
public interface CityDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(City city);

    @Query("SELECT * FROM cities")
    List<City> getAllCities();
}
