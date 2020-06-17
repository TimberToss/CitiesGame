package com.example.speechrecognition.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.speechrecognition.data.entity.BeginLetters;
import com.example.speechrecognition.data.entity.City;
import com.example.speechrecognition.data.repository.Repository;
import com.example.speechrecognition.data.state.CheckCityStatus;
import com.example.speechrecognition.data.state.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.example.speechrecognition.constants.Constants.MY_TAG;

public class MainActivityViewModel extends ViewModel
        implements ServerCitiesCallback<List<City>>, ServerLettersCallback<BeginLetters> {

    private Repository repository;
    private List<City> cities;
    private List<Character> possibleBeginLetters;
    private Map<Character, List<String>> citiesMap;

    private MutableLiveData<Resource<BeginLetters>> lettersMutableLiveData
            = new MutableLiveData<>(new Resource.Loading<>());
    public LiveData<Resource<BeginLetters>> lettersLiveData = lettersMutableLiveData;

    public MainActivityViewModel() {
        super();
        repository = new Repository(this, this);
    }

    public void downloadData() {
        repository.downloadData();
    }

    @Override
    public void downloadCities(Resource<List<City>> resource) {
        if (resource.getStatus() == Resource.DataStatus.SUCCESS) {
            cities = resource.getData();
            repository.downloadPossibleLetters();
        } else if (resource.getStatus() == Resource.DataStatus.ERROR) {
            Log.d(MY_TAG, resource.getMessage());
        }
    }

    @Override
    public void downloadLetters(Resource<BeginLetters> resource) {
        lettersMutableLiveData.setValue(resource);
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
    }

    private void createCitiesMap() {
        citiesMap = new HashMap<>();
        for (Character character : possibleBeginLetters) {
            citiesMap.put(character, new ArrayList<>());
        }
        for (City city : cities) {
            String cityName = city.getRussianName();

            List<String> citiesNames = citiesMap.get(cityName.toLowerCase().charAt(0));
            if (citiesNames == null) {
                return;
            }
            citiesNames.add(cityName);
        }
    }

    public String createAnswer(String userWord) {
        Character ourBeginLetter = null;
        char[] lettersInUserCity = userWord.toCharArray();

        for (int i = lettersInUserCity.length - 1; i >= 0; i--) {
            if (possibleBeginLetters.contains(lettersInUserCity[i])) {
                ourBeginLetter = lettersInUserCity[i];
                break;
            }
        }
        List<String> citiesNames = citiesMap.get(ourBeginLetter);
        assert citiesNames != null;
        int indexOurCity = new Random().nextInt(citiesNames.size());
        String ourCityName = citiesNames.get(indexOurCity);
        citiesNames.remove(indexOurCity);
        return ourCityName;
    }

    public CheckCityStatus checkUserCity(String userCity, String lastAppCity) {
        Character userCityBeginLetter = userCity.toLowerCase().charAt(0);
        if (!possibleBeginLetters.contains(userCityBeginLetter)) {
            return CheckCityStatus.INCORRECT_BEGIN_LETTER;
        }

        if (lastAppCity != null) {
            lastAppCity = lastAppCity.toLowerCase();
            Character appEndLetter = null;
            for (int i = lastAppCity.length() - 1; i >= 0; i--) {
                if (possibleBeginLetters.contains(lastAppCity.charAt(i))) {
                    appEndLetter = lastAppCity.charAt(i);
                    break;
                }
            }

            if (appEndLetter == null || !userCityBeginLetter.toString().equals(appEndLetter.toString())) {
                return CheckCityStatus.CHOOSE_INCORRECT_BEGIN_LETTER;
            }
        }

        List<String> citiesNames = citiesMap.get(userCityBeginLetter);
        assert citiesNames != null;
        if (citiesNames.isEmpty()) {
            return CheckCityStatus.CITIES_FROM_LETTER_ENDED;
        }

        boolean isUserCityExistInList = false;
        for (int i = 0; i < citiesNames.size(); i++) {
            if (userCity.toLowerCase().equals(citiesNames.get(i).toLowerCase())) {
                isUserCityExistInList = true;
                citiesNames.remove(i);
                break;
            }
        }

        if (citiesNames.isEmpty()) {
            return CheckCityStatus.USER_ENTER_LAST_CITY;
        }

        if (!isUserCityExistInList) {
            if (repository.isCityExistInDatabase(userCity)) {
                return CheckCityStatus.ALREADY_USED_CITY;
            } else {
                return CheckCityStatus.UNKNOWN_CITY;
            }
        }
        return CheckCityStatus.SUCCESS;
    }
}
