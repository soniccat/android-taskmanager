package com.example.alexeyglushkov.quizletservice;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class NonNullMutableLiveData<T> extends MutableLiveData<T> {
    public NonNullMutableLiveData(T value) {
        super();
        setValue(value);
    }

    @NonNull
    @Override
    public T getValue() {
        return super.getValue();
    }
}
