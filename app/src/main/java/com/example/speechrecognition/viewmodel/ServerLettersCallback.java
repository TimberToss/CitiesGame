package com.example.speechrecognition.viewmodel;

import com.example.speechrecognition.data.state.Resource;

public interface ServerLettersCallback<T> {
    void downloadLetters(Resource<T> resource);
}
