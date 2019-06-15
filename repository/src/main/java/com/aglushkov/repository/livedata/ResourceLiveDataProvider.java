package com.aglushkov.repository.livedata;

import androidx.lifecycle.LiveData;

public interface ResourceLiveDataProvider<T> {
    LiveData<Resource<T>> getLiveData();
}
