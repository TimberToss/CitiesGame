package com.example.speechrecognition.data.database;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.speechrecognition.data.dao.CityDao;
import com.example.speechrecognition.data.entity.City;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {City.class}, version = 1, exportSchema = false)
public abstract class CityDatabase extends RoomDatabase {

    public abstract CityDao cityDao();

    private static volatile CityDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseExecutor
            = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static CityDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (CityDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            CityDatabase.class,
                            "city_database"
                    ).addCallback(populateDatabase).build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback populateDatabase = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            Log.d("onCreateCallback", "ough");
            super.onCreate(db);
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            List<City> cities = new ArrayList<>();

            databaseExecutor.execute(() -> {
                try {
                    QuerySnapshot querySnapshot = Tasks.await(
                            firestore.collection("cities").get()
                    );
                    if (querySnapshot != null) {
                        for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                            cities.add(snapshot.toObject(City.class));
                        }
                    }
                    if (!cities.isEmpty()) {
                        CityDao dao = INSTANCE.cityDao();
                        Log.d("Insert db", "Start inserting");

                        databaseExecutor.execute(() -> {
                            for (City city : cities) {
                                Log.d("Insert db", city.getEnglishName());
                                dao.insert(city);
                            }
                        });
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                firestore.collection("cities").get().addOnCompleteListener(task -> {
//
//                    if (task.getResult() != null) {
//                        for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
//                            cities.add(snapshot.toObject(City.class));
//                        }
//                    }
//                    if (!cities.isEmpty()) {
//                        CityDao dao = INSTANCE.cityDao();
//                        Log.d("Insert db", "Start inserting");
//
//                        databaseExecutor.execute(() -> {
//                            for (City city : cities) {
//                                Log.d("Insert db", city.getEnglishName());
//                                dao.insert(city);
//                            }
//                        });
//                    }
//                });
            });
        }
    };
}