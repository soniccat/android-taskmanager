package com.example.alexeyglushkov.wordteacher.listmodule;

import android.os.Bundle;

import com.example.alexeyglushkov.quizletservice.NonNullMutableLiveData;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EmptyStorableListLiveDataProvider<T> implements StorableListLiveDataProvider<T> {
    @Override
    public void store(Bundle bundle) {
    }

    @Override
    public void restore(Bundle bundle, Object context) {
    }

    @Override
    public LiveData<List<T>> getListLiveData() {
        MutableLiveData<List<T>> data = new MutableLiveData<>();
        data.setValue(Collections.<T>emptyList());
        return data;
    }
}
