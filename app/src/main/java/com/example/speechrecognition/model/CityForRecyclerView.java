package com.example.speechrecognition.model;

public class CityForRecyclerView {

    private String name;
    private CityType type;

    public CityForRecyclerView(String name, CityType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public CityType getType() {
        return type;
    }

    public enum CityType {
        USER_CITY,
        APP_CITY
    }
}
