package com.example.alexeyglushkov.wordteacher.listmodule;

import com.example.alexeyglushkov.quizletservice.NonNullLiveData;

import java.util.List;

import androidx.lifecycle.LiveData;

public interface ListLiveDataProvider<T> {
    LiveData<List<T>> getListLiveData();
}
