package com.example.alexeyglushkov.cachemanager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by alexeyglushkov on 04.10.15.
 */
public interface StorageEntry {
    @NonNull
    Object getObject() throws Exception;

    void delete() throws Exception;

    @Nullable
    StorageMetadata getMetadata();
}
