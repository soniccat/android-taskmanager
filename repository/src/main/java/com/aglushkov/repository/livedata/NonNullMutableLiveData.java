package com.aglushkov.repository.livedata;

import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
