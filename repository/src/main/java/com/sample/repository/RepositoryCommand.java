package com.sample.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public interface RepositoryCommand<T> {
    long getCommandId();
    void cancel();
    @NonNull LiveData<T> getLiveData();
}