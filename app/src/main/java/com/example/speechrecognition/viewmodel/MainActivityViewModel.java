package com.example.speechrecognition.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.speechrecognition.data.entity.BeginLetters;
import com.example.speechrecognition.data.entity.City;
import com.example.speechrecognition.data.repository.Repository;
import com.example.speechrecognition.data.state.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.speechrecognition.constants.Constants.ENGLISH;
import static com.example.speechrecognition.constants.Constants.MY_TAG;
import static com.example.speechrecognition.constants.Constants.RUSSIAN;
import static com.example.speechrecognition.constants.Constants.RU_LOCALE;

public class MainActivityViewModel extends ViewModel
        implements ServerCitiesCallback<List<City>>, ServerLettersCallback<BeginLetters> {

    private Repository repository;
    private List<City> cities;
    private List<Character> possibleLetters;
    private String language;

    private MutableLiveData<Resource<List<City>>> citiesMutableLiveData
            = new MutableLiveData<>(new Resource.Loading<>());
    public LiveData<Resource<List<City>>> citiesLiveData = citiesMutableLiveData;

    private MutableLiveData<Resource<BeginLetters>> lettersMutableLiveData
            = new MutableLiveData<>(new Resource.Loading<>());
    public LiveData<Resource<BeginLetters>> lettersLiveData = lettersMutableLiveData;

    @Override
    public void downloadCities(Resource<List<City>> resource) {
        citiesMutableLiveData.setValue(resource);
        Log.d(MY_TAG, "Download cities");
        if (resource.getStatus() == Resource.DataStatus.SUCCESS) {
            cities = resource.getData();
            knowLanguage();
            repository.downloadPossibleLetters(language);
            for (City city : cities) {
                Log.d(MY_TAG, city.getEnglishName());
            }

        } else if (resource.getStatus() == Resource.DataStatus.ERROR) {
            Log.d(MY_TAG, resource.getMessage());
        }
    }

    @Override
    public void downloadLetters(Resource<BeginLetters> resource) {
        lettersMutableLiveData.setValue(resource);
        Log.d(MY_TAG, "Download letters");
        if (resource instanceof Resource.Success) {
            possibleLetters = new ArrayList<>();
            List<String> letters = resource.getData().getLetters();
            for (String str : letters) {
                possibleLetters.add(str.charAt(0));
            }
            for (Character letter : possibleLetters) {
                Log.d(MY_TAG, letter.toString());
            }
        } else if (resource instanceof Resource.Error) {
            Log.d(MY_TAG, resource.getMessage());
        }
    }

    public MainActivityViewModel() {
        super();
        repository = new Repository(this, this);

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

    public void downloadData() {
//        try {
//            cities = repository.getData();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        cities = repository.getData();
        repository.downloadData();
//        chooseLanguage();
    }

//    public List<City> getAllCities(String concreteLanguage) throws ExecutionException, InterruptedException {
//        return repository.getAllCities(concreteLanguage);
//    }

    private void createMap(String language) {

        char[] possibleBeginLetters;
//        try {
//            possibleBeginLetters = repository.getPossibleBeginLetters(language);
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        possibleBeginLetters = repository.getPossibleBeginLetters(language);
        repository.getPossibleBeginLetters(language);

        Map<Character, List<String>> cityMap = new HashMap<>();

//        if (possibleBeginLetters == null) {
//            Log.d(MY_TAG, "possibleBeginLetters is Null");
//            return;
//        }
//        for (Character character : possibleBeginLetters) {
//            cityMap.put(character, new ArrayList<>());
//        }
//
//        for (City city : cities) {
//            if (city.isNameOnThisLanguageExist(language)) {
//                String cityName = city.getName(language);
//
//                List<String> list = cityMap.get(cityName.charAt(0));
//                if (list == null) {
//                    Log.d(MY_TAG, "Incorrect begin letter");
//                    return;
//                }
//                list.add(cityName);
//                Log.d("Check cities", cityName);
//            }
//        }
    }

    private void knowLanguage() {

        if (RU_LOCALE.equals(Locale.getDefault().getLanguage())) {
            language = RUSSIAN;
//            createMap(RUSSIAN);
        } else {
            language = ENGLISH;
//            createMap(ENGLISH);
        }
    }

//    @Override
//    public void getQuery(Resource<T> resource) {
//        if (resource instanceof Resource.Success) {
//
//        } else if (resource instanceof Resource.Error) {
//
//        }
//    }
}
