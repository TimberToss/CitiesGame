package com.example.speechrecognition.data.repository;

import android.app.Application;
import android.util.Log;

import com.example.speechrecognition.data.entity.BeginLetters;
import com.example.speechrecognition.data.entity.City;
import com.example.speechrecognition.data.state.Resource;
import com.example.speechrecognition.viewmodel.ServerCitiesCallback;
import com.example.speechrecognition.viewmodel.ServerLettersCallback;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.speechrecognition.constants.Constants.MY_TAG;

public class Repository {

    private static final String COLLECTION_CITIES = "cities";
    private static final String COLLECTION_BEGIN_LETTERS = "possible_begin_letters";
    private static final String LETTERS_ARRAY = "letters";
    //    private CityDao cityDao;
    private ServerCitiesCallback<List<City>> citiesCallback;
    private ServerLettersCallback<BeginLetters> lettersCallback;

    public Repository() {
    }


    public Repository(ServerCitiesCallback<List<City>> citiesCallback,
                      ServerLettersCallback<BeginLetters> lettersCallback) {
        this.citiesCallback = citiesCallback;
        this.lettersCallback = lettersCallback;
    }

//    public List<City> getAllCities(String concreteLanguage) throws ExecutionException, InterruptedException {
//        Callable<List<City>> callable = () -> cityDao.getAllCities();
//        return CityDatabase.databaseExecutor.submit(callable).get();
//    }

    public void downloadData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(COLLECTION_CITIES)
                .get(Source.CACHE)
                .addOnCompleteListener(task -> {
                    if (task.getResult() != null && !task.getResult().isEmpty()) {
                        if (task.isSuccessful()) {
                            Log.d(MY_TAG, "cities cache task is successful!!!!");
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


//        CollectionReference reference = db.collection(COLLECTION_CITIES);
//
////        QuerySnapshot querySnapshot = Tasks.await(db.collection(COLLECTION_CITIES).get(Source.CACHE));
//        QuerySnapshot querySnapshot = getQuerySnapshot(reference, Source.CACHE);

//            querySnapshot = getQuerySnapshot(reference, Source.SERVER);
//            querySnapshot = Tasks.await(db.collection(COLLECTION_CITIES).get(Source.SERVER));

//        for (DocumentSnapshot document : querySnapshot) {
//            cities.add(document.toObject(City.class));
//        }

//        return cities;
    }

    public void downloadPossibleLetters(String language) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(COLLECTION_BEGIN_LETTERS)
                .document(language)
                .get(Source.CACHE)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(MY_TAG, "begin letters cache task is successful!!!!");
                        lettersCallback.downloadLetters(
                                new Resource.Success<>(task.getResult().toObject(BeginLetters.class))
                        );
                    } else {
                        runLettersServerQuery(db, language);
                    }
//                    if (task.getException() == null) {
//                        if (task.isSuccessful()) {
//                            Log.d(MY_TAG, "begin letters cache task is successful!!!!");
//                            List<String> letters = task.getResult().toObject(BeginLetters.class).getLettersArray();
//                            for (int i = 0; i < letters.size(); i++) {
//                                possibleBeginLetters.add(letters.get(i).charAt(0));
//                            }
//                            lettersCallback.downloadLetters(
//                                    new Resource.Success<>(possibleBeginLetters)
//                            );
//                        } else {
//                            Log.d(MY_TAG, "begin letters cache task is empty");
//                            lettersCallback.downloadLetters(
//                                    new Resource.Error<>(Objects.requireNonNull(
//                                            task.getException()).getMessage()
//                                    ));
//                        }
//                    } else {
//                        runLettersServerQuery(db, language);
//                    }
                });
    }

    public boolean isCityExistInDatabase(String userCityName, String language) {
        String field = language + "Name";
        Log.d(MY_TAG, field + " " + userCityName);
        return FirebaseFirestore.getInstance()
                .collection(COLLECTION_CITIES)
                .whereEqualTo(field, userCityName)
                .get(Source.CACHE)
                .isSuccessful();
    }

    public void getPossibleBeginLetters(String language) {
        char[] possibleBeginLetters;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

//        DocumentReference reference
//                = db.collection(COLLECTION_BEGIN_LETTERS).document(language);
//
////        DocumentSnapshot documentSnapshot = Tasks.await(reference.get(Source.CACHE));
//        DocumentSnapshot documentSnapshot = getDocSnapshot(reference, Source.CACHE);
//
//        if (documentSnapshot == null) {
//            documentSnapshot = getDocSnapshot(reference, Source.SERVER);
////            documentSnapshot = Tasks.await(reference.get(Source.SERVER));
//        }
//
//        String[] strings = documentSnapshot.toObject(BeginLetters.class).getLettersArray();
//        possibleBeginLetters = new char[strings.length];
//
//        for (int i = 0; i < possibleBeginLetters.length; i++) {
//            possibleBeginLetters[i] = strings[i].charAt(0);
//        }

//        return possibleBeginLetters;
    }

    private void runCitiesServerQuery(FirebaseFirestore db) {
        db.collection(COLLECTION_CITIES)
                .get(Source.SERVER)
                .addOnSuccessListener(result -> {
                    Log.d(MY_TAG, "cities server task is successful!!!!");
                    citiesCallback.downloadCities(new Resource.Success<>(result.toObjects(City.class)));
                })
                .addOnFailureListener(error -> {
                    Log.d(MY_TAG, "cities server task is error!!!!");
                    citiesCallback.downloadCities(new Resource.Error<>(error.getMessage()));
                });
    }

    private void runLettersServerQuery(FirebaseFirestore db, String language) {
        db.collection(COLLECTION_BEGIN_LETTERS)
                .document(language)
                .get(Source.SERVER)
                .addOnSuccessListener(result -> {
                    Log.d(MY_TAG, "begin letters server task is successful!!!!");
                    lettersCallback.downloadLetters(
                            new Resource.Success<>(result.toObject(BeginLetters.class))
                    );
                })
                .addOnFailureListener(error -> {
                    Log.d(MY_TAG, "begin letters server task is error!!!!");
                    lettersCallback.downloadLetters(new Resource.Error<>(error.getMessage()));
                });
    }

//    private DocumentSnapshot getDocSnapshot(DocumentReference reference, Source source) {
//        return reference.get(source).getResult();
//    }
//
//    private QuerySnapshot getQuerySnapshot(CollectionReference reference, Source source) {
//        return reference.get(source).getResult();
//    }
}
