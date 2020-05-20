package com.example.speechrecognition.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.example.speechrecognition.constants.Constants.ENGLISH;
import static com.example.speechrecognition.constants.Constants.RUSSIAN;

@Entity(tableName = "cities")
public class City {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String englishName;
    private String russianName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getRussianName() {
        return russianName;
    }

    public void setRussianName(String russianName) {
        this.russianName = russianName;
    }

    public String getName(String language) {
        if (ENGLISH.equals(language)) {
            return englishName;
        } else {
            return russianName;
        }
    }

    public boolean isNameOnThisLanguageExist(String language) {
        boolean result = false;
        switch (language) {
            case ENGLISH:
                if (englishName != null) {
                    result = true;
                }
                break;
            case RUSSIAN:
                if (russianName != null) {
                    result = true;
                }
                break;
        }
        return result;
    }
}
