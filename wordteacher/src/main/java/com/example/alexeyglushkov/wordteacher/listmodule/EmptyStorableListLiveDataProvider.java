package com.example.alexeyglushkov.wordteacher.listmodule;

import android.os.Bundle;

import com.example.alexeyglushkov.quizletservice.Resource;

import java.util.Collections;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EmptyStorableListLiveDataProvider<T> implements StorableResourceListLiveDataProvider<T> {
    private Resource<List<T>> EMPTY_RES = new Resource<>();

    @Override
    public void store(Bundle bundle) {
    }

    @Override
    public void restore(Bundle bundle) {
    }

    @Override
    public LiveData<Resource<List<T>>> getListLiveData() {
        MutableLiveData<Resource<List<T>>> data = new MutableLiveData<>();
        data.setValue(EMPTY_RES);
        return data;
    }

    @Override
    public LiveData<Resource<List<T>>> getLiveData() {
        return getListLiveData();
    }
}
