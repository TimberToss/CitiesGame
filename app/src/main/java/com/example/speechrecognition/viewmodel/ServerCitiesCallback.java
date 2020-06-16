package com.example.speechrecognition.viewmodel;

import com.example.speechrecognition.data.state.Resource;

public interface ServerCitiesCallback<T> {
     void downloadCities(Resource<T> resource);
}
