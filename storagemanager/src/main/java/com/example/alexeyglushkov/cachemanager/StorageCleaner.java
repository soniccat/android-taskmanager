package com.example.alexeyglushkov.cachemanager;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import io.reactivex.Completable;

/**
 * Created by alexeyglushkov on 10.10.15.
 */
public interface StorageCleaner {
    void setProgressInfo(ProgressUpdater info);
    Completable clean(Storage provider);
}
