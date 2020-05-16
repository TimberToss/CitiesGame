package com.example.speechrecognition.data.repository;

import android.app.Application;

import com.example.speechrecognition.data.dao.CityDao;
import com.example.speechrecognition.data.database.CityDatabase;
import com.example.speechrecognition.data.entity.City;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class Repository {
    private CityDao cityDao;

    public Repository(Application application) {
        CityDatabase db = CityDatabase.getInstance(application);
        cityDao = db.cityDao();
    }

    public List<City> getAllCities(String concreteLanguage) throws ExecutionException, InterruptedException {
        Callable<List<City>> callable = () -> cityDao.getAllCities();
        return CityDatabase.databaseExecutor.submit(callable).get();
    }
}
