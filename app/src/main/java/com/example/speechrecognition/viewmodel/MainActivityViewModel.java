package com.example.speechrecognition.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.speechrecognition.data.entity.City;
import com.example.speechrecognition.data.repository.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivityViewModel extends AndroidViewModel {

    private Repository repository;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
    }

    public List<City> getAllCities(String concreteLanguage) throws ExecutionException, InterruptedException {
        return repository.getAllCities(concreteLanguage);
    }
}
