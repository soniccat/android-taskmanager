package com.example.alexeyglushkov.wordteacher.listmodule;

import com.aglushkov.repository.livedata.Resource;
import com.aglushkov.repository.livedata.ResourceLiveDataProvider;

import java.util.List;

import androidx.lifecycle.LiveData;

public interface ResourceListLiveDataProvider<T> extends ResourceLiveDataProvider<List<T>> {
    LiveData<Resource<List<T>>> getListLiveData();
}
