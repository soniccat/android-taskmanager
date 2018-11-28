package com.example.alexeyglushkov.wordteacher.listmodule;

import com.example.alexeyglushkov.quizletservice.NonNullLiveData;

import androidx.lifecycle.LiveData;

public interface ListLiveDataProvider<T> {
    LiveData<T> getListLiveData();
}
