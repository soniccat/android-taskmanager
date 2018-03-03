package com.example.alexeyglushkov.cachemanager;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

/**
 * Created by alexeyglushkov on 10.10.15.
 */
public interface StorageCleaner {
    void setProgressInfo(ProgressUpdater info);
    void clean(Storage provider);
}
