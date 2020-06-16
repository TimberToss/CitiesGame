package com.example.speechrecognition.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.speechrecognition.data.entity.City;

public class MainActivityViewModelFactory implements ViewModelProvider.Factory {

//    private ViewModelCitiesCallback<City> citiesCallback;
//    private ViewModelLettersCallback<String> lettersCallback;
//
//    public MainActivityViewModelFactory(ViewModelCitiesCallback<City> citiesCallback,
//                                        ViewModelLettersCallback<String> lettersCallback) {
//        this.citiesCallback = citiesCallback;
//        this.lettersCallback = lettersCallback;
//    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainActivityViewModel();
    }
}
