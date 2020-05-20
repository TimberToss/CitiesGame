package com.example.speechrecognition.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.example.speechrecognition.data.entity.City;
import com.example.speechrecognition.data.repository.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.example.speechrecognition.constants.Constants.ENGLISH;
import static com.example.speechrecognition.constants.Constants.MY_TAG;
import static com.example.speechrecognition.constants.Constants.RUSSIAN;

public class MainActivityViewModel extends ViewModel {

    private static final String RU_LOCALE = "ru";

    private Repository repository;
    private List<City> cities;

    public MainActivityViewModel() {
        super();
        repository = new Repository();

//        try {
//            cities = repository.getData();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        chooseLanguage();
    }

    public void getData() {
        try {
            cities = repository.getData();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        chooseLanguage();
    }

//    public List<City> getAllCities(String concreteLanguage) throws ExecutionException, InterruptedException {
//        return repository.getAllCities(concreteLanguage);
//    }

    private void createMap(String language)  {

        char[] possibleBeginLetters = null;
        try {
            possibleBeginLetters = repository.getPossibleBeginLetters(language);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map<Character, List<String>> cityMap = new HashMap<>();

        if (possibleBeginLetters == null) {
            Log.d(MY_TAG, "possibleBeginLetters is Null");
            return;
        }
        for (Character character : possibleBeginLetters) {
            cityMap.put(character, new ArrayList<>());
        }

        for (City city : cities) {
            if (city.isNameOnThisLanguageExist(language)) {
                String cityName = city.getName(language);

                List<String> list = cityMap.get(cityName.charAt(0));
                if (list == null) {
                    Log.d(MY_TAG, "Incorrect begin letter");
                    return;
                }
                list.add(cityName);
                Log.d("Check cities", cityName);
            }
        }
    }

    private void chooseLanguage() {

        if (RU_LOCALE.equals(Locale.getDefault().getLanguage())) {
            createMap(RUSSIAN);
        } else {
            createMap(ENGLISH);
        }
    }
}
