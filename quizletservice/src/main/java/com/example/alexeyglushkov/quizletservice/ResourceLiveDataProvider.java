package com.example.alexeyglushkov.quizletservice;

import com.example.alexeyglushkov.quizletservice.Resource;

import androidx.lifecycle.LiveData;

public interface ResourceLiveDataProvider<T> {
    LiveData<Resource<T>> getLiveData();
}
