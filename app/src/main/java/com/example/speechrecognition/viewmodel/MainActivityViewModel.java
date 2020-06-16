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
import java.util.Random;

import static com.example.speechrecognition.constants.Constants.ENGLISH;
import static com.example.speechrecognition.constants.Constants.MY_TAG;
import static com.example.speechrecognition.constants.Constants.RUSSIAN;
import static com.example.speechrecognition.constants.Constants.RU_LOCALE;

public class MainActivityViewModel extends ViewModel
        implements ServerCitiesCallback<List<City>>, ServerLettersCallback<BeginLetters> {

    private Repository repository;
    private List<City> cities;
    private List<Character> possibleBeginLetters;
    private String language;
    private Map<Character, List<String>> citiesMap;

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
        if (resource.getStatus() == Resource.DataStatus.SUCCESS) {
            createPossibleLetters(resource);
            createCitiesMap();
        } else if (resource.getStatus() == Resource.DataStatus.ERROR) {
            Log.d(MY_TAG, resource.getMessage());
        }
    }

    private void createPossibleLetters(Resource<BeginLetters> resource) {
        possibleBeginLetters = new ArrayList<>();
        List<String> letters = resource.getData().getLetters();
        for (String str : letters) {
            possibleBeginLetters.add(str.charAt(0));
        }
        for (Character letter : possibleBeginLetters) {
            Log.d(MY_TAG, letter.toString());
        }
    }

    private void createCitiesMap() {
        citiesMap = new HashMap<>();
        for (Character character : possibleBeginLetters) {
            citiesMap.put(character, new ArrayList<>());
        }
        for (City city : cities) {
            if (city.isNameOnThisLanguageExist(language)) {
                String cityName = city.getName(language);

                List<String> citiesNames = citiesMap.get(cityName.toLowerCase().charAt(0));
                if (citiesNames == null) {
                    Log.d(MY_TAG, "Incorrect begin letter: " + cityName.charAt(0));
                    return;
                }
                citiesNames.add(cityName);
                Log.d("Check cities", cityName);
            }
        }
    }

    public String createAnswer(String userWord) {
//        Character ourBeginLetter = userWord.toLowerCase().charAt(userWord.length() - 1);
        Character ourBeginLetter = null;
        char[] lettersInUserCity = userWord.toCharArray();

        for (int i = lettersInUserCity.length - 1; i >= 0; i--) {
            if (possibleBeginLetters.contains(lettersInUserCity[i])) {
                ourBeginLetter = lettersInUserCity[i];
                break;
            }
        }
        // this check is redundant because in checkUserCity() throw exception if userCity begin from incorrect word
//        if (ourBeginLetter == null) {
//            Log.d(MY_TAG, "User word consists from forbidden letters");
//            // TODO please, enter another word
//        }
        List<String> citiesNames = citiesMap.get(ourBeginLetter);
        assert citiesNames != null;
//        if (citiesNames.isEmpty()) {
//            // TODO if cities beginning from any letter will end - close game
//        }

        int indexOurCity = new Random().nextInt(citiesNames.size());
        String ourCityName = citiesNames.get(indexOurCity);
        citiesNames.remove(indexOurCity);
        return ourCityName;
    }

    public boolean checkUserCity(String userCity) {
        Character userCityBeginLetter = userCity.toLowerCase().charAt(0);
        if (!possibleBeginLetters.contains(userCityBeginLetter)) {
            Log.d(MY_TAG, "user city has incorrect begin letter");
            // TODO
            return false;
        }
        List<String> citiesNames = citiesMap.get(userCityBeginLetter);
        assert citiesNames != null;
        if (citiesNames.isEmpty()) {
            Log.d(MY_TAG, "cities on this letter are ended: " + userCityBeginLetter);
            // TODO if cities beginning from any letter will end - close game
            return false;
        }
        boolean isUserCityExistInList = false;
        for (int i = 0; i < citiesNames.size(); i++) {
            Log.d(MY_TAG, userCity);
            Log.d(MY_TAG, citiesNames.get(i));
            if (userCity.toLowerCase().equals(citiesNames.get(i).toLowerCase())) {
                isUserCityExistInList = true;
                citiesNames.remove(i);
                break;
            }
        }
//        if (citiesNames.isEmpty()) {
//            Log.d(MY_TAG, "user enter last word beginning from this letter: " + userCityBeginLetter);
//            // TODO if cities beginning from any letter will end - close game
//            return false;
//        }
        if (!isUserCityExistInList) {
            Log.d(MY_TAG, "database doesn't know this city or it is already used: " + userCity);
            // TODO incorrect cityName (doesn't exist in database or already is used in game)
            // maybe for check existence in database get query to Firestore
            return false;
        }
        return true;
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
