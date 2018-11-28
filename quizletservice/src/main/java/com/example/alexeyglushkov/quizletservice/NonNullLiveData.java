package com.example.alexeyglushkov.quizletservice;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class NonNullLiveData<T> extends LiveData<T> {
    public NonNullLiveData(T value) {
        super();
        setValue(value);
    }

    @NonNull
    @Override
    public T getValue() {
        return super.getValue();
    }
}
