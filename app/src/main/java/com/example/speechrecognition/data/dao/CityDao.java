package com.example.speechrecognition.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CityDao {

    @Query("SELECT :concreteName")
    List<String> getCities(String concreteName);
}
