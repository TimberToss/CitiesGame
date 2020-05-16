package com.example.speechrecognition.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.speechrecognition.data.dao.CityDao;
import com.example.speechrecognition.model.City;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {City.class}, version = 1, exportSchema = false)
public abstract class CityDatabase extends RoomDatabase {

    public abstract CityDao cityDao();

    private static volatile CityDatabase INSTANCE;
//    private static final int NUMBER_OF_THREADS = 1;
    public static final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();

    public static CityDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (CityDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            CityDatabase.class,
                            "city_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}