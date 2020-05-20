package com.example.speechrecognition.data.repository;

import android.app.Application;
import android.util.Log;

import com.example.speechrecognition.data.dao.CityDao;
import com.example.speechrecognition.data.database.CityDatabase;
import com.example.speechrecognition.data.entity.BeginLetters;
import com.example.speechrecognition.data.entity.City;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class Repository {

    private static final String COLLECTION_CITIES = "cities";
    private static final String COLLECTION_BEGIN_LETTERS = "possible_begin_letters";
    private static final String LETTERS_ARRAY = "letters";
//    private CityDao cityDao;

    public Repository() {
    }

    public Repository(Application application) {
//        CityDatabase db = CityDatabase.getInstance(application);
//        cityDao = db.cityDao();
    }

//    public List<City> getAllCities(String concreteLanguage) throws ExecutionException, InterruptedException {
//        Callable<List<City>> callable = () -> cityDao.getAllCities();
//        return CityDatabase.databaseExecutor.submit(callable).get();
//    }

    public List<City> getData() throws ExecutionException, InterruptedException {
        List<City> cities = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        QuerySnapshot querySnapshot = Tasks.await(db.collection(COLLECTION_CITIES).get(Source.CACHE));
        if (querySnapshot == null) {
            querySnapshot = Tasks.await(db.collection(COLLECTION_CITIES).get(Source.SERVER));
        }

        for (DocumentSnapshot document : querySnapshot) {
            cities.add(document.toObject(City.class));
        }

        return cities;
    }

    public char[] getPossibleBeginLetters(String language) throws ExecutionException, InterruptedException {
        char[] possibleBeginLetters;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference reference
                = db.collection(COLLECTION_BEGIN_LETTERS).document(language);

        DocumentSnapshot documentSnapshot = Tasks.await(reference.get(Source.CACHE));

        if (documentSnapshot == null) {
            documentSnapshot = Tasks.await(reference.get(Source.SERVER));
        }

        String[] strings = documentSnapshot.toObject(BeginLetters.class).getLettersArray();
        possibleBeginLetters = new char[strings.length];

        for (int i = 0; i < possibleBeginLetters.length; i++) {
            possibleBeginLetters[i] = strings[i].charAt(0);
        }

        return possibleBeginLetters;
    }
}
