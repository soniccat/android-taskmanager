package com.example.alexeyglushkov.wordteacher.listmodule;

import com.example.alexeyglushkov.quizletservice.NonNullLiveData;
import com.example.alexeyglushkov.quizletservice.Resource;
import com.example.alexeyglushkov.quizletservice.ResourceLiveDataProvider;

import java.util.List;

import androidx.lifecycle.LiveData;

public interface ResourceListLiveDataProvider<T> extends ResourceLiveDataProvider {
    LiveData<Resource<List<T>>> getListLiveData();
}
