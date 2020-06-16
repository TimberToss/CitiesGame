package com.example.speechrecognition.data.state;

public enum CheckCityStatus {
    SUCCESS,
    INCORRECT_BEGIN_LETTER,
    CITIES_FROM_LETTER_ENDED,
    USER_ENTER_LAST_CITY,
    ALREADY_USED_CITY,
    UNKNOWN_CITY
}
