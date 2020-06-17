package com.example.speechrecognition.data.repository;

import android.util.Log;

import com.example.speechrecognition.data.entity.BeginLetters;
import com.example.speechrecognition.data.entity.City;
import com.example.speechrecognition.data.state.Resource;
import com.example.speechrecognition.viewmodel.ServerCitiesCallback;
import com.example.speechrecognition.viewmodel.ServerLettersCallback;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.List;
import java.util.Objects;

import static com.example.speechrecognition.constants.Constants.MY_TAG;

public class Repository {

    private static final String COLLECTION_CITIES = "cities";
    private static final String COLLECTION_BEGIN_LETTERS = "possible_begin_letters";
    private static final String DOCUMENT_BEGIN_LETTERS_LANGUAGE = "russian";
    private static final String CITY_NAME_FIELD = "russianName";

    private ServerCitiesCallback<List<City>> citiesCallback;
    private ServerLettersCallback<BeginLetters> lettersCallback;

    public Repository(ServerCitiesCallback<List<City>> citiesCallback,
                      ServerLettersCallback<BeginLetters> lettersCallback) {
        this.citiesCallback = citiesCallback;
        this.lettersCallback = lettersCallback;
    }

    public void downloadData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(COLLECTION_CITIES)
                .get(Source.CACHE)
                .addOnCompleteListener(task -> {
                    if (task.getResult() != null && !task.getResult().isEmpty()) {
                        if (task.isSuccessful()) {
                            Log.d(MY_TAG, "cities cache task is successful!!!");
                            citiesCallback.downloadCities(
                                    new Resource.Success<>(
                                            task.getResult().toObjects(City.class)
                                    ));
                        } else {
                            Log.d(MY_TAG, "cities cache task is empty");
                            citiesCallback.downloadCities(
                                    new Resource.Error<>(Objects.requireNonNull(
                                            task.getException()).getMessage()
                                    ));
                        }
                    } else {
                        runCitiesServerQuery(db);
                    }
                });
    }

    public void downloadPossibleLetters() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(COLLECTION_BEGIN_LETTERS)
                .document(DOCUMENT_BEGIN_LETTERS_LANGUAGE)
                .get(Source.CACHE)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(MY_TAG, "begin letters cache task is successful!!!");
                        lettersCallback.downloadLetters(
                                new Resource.Success<>(Objects.requireNonNull(task.getResult())
                                        .toObject(BeginLetters.class))
                        );
                    } else {
                        runLettersServerQuery(db);
                    }
                });
    }

    public boolean isCityExistInDatabase(String userCityName) {
        Log.d(MY_TAG, CITY_NAME_FIELD + " " + userCityName);
        Task<QuerySnapshot> task = FirebaseFirestore.getInstance()
                .collection(COLLECTION_CITIES)
                .limit(1)
                .whereEqualTo(CITY_NAME_FIELD, userCityName)
                .get(Source.CACHE);
        while (!task.isComplete()) {
        }
        return !Objects.requireNonNull(task.getResult()).isEmpty();
    }

    private void runCitiesServerQuery(FirebaseFirestore db) {
        db.collection(COLLECTION_CITIES)
                .get(Source.SERVER)
                .addOnSuccessListener(result -> {
                    Log.d(MY_TAG, "cities server task is successful!!!");
                    citiesCallback.downloadCities(new Resource.Success<>(result.toObjects(City.class)));
                })
                .addOnFailureListener(error -> {
                    Log.d(MY_TAG, "cities server task is error!!!");
                    citiesCallback.downloadCities(new Resource.Error<>(error.getMessage()));
                });
    }

    private void runLettersServerQuery(FirebaseFirestore db) {
        db.collection(COLLECTION_BEGIN_LETTERS)
                .document(DOCUMENT_BEGIN_LETTERS_LANGUAGE)
                .get(Source.SERVER)
                .addOnSuccessListener(result -> {
                    Log.d(MY_TAG, "begin letters server task is successful!!!");
                    lettersCallback.downloadLetters(
                            new Resource.Success<>(result.toObject(BeginLetters.class))
                    );
                })
                .addOnFailureListener(error -> {
                    Log.d(MY_TAG, "begin letters server task is error!!!");
                    lettersCallback.downloadLetters(new Resource.Error<>(error.getMessage()));
                });
    }
}
