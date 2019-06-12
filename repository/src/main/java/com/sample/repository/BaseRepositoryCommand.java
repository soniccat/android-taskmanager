package com.sample.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class BaseRepositoryCommand<T> implements RepositoryCommand<T> {
    private long id;
    @NonNull private LiveData<T> liveData;

    public BaseRepositoryCommand(long id, @NonNull LiveData<T> liveData) {
        this.id = id;
        this.liveData = liveData;
    }

    @Override
    public long getCommandId() {
        return id;
    }

    @Override
    public void cancel() {
    }

    @Override
    @NonNull
    public LiveData<T> getLiveData() {
        return liveData;
    }
}
