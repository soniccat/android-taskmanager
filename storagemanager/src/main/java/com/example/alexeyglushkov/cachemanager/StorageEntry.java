package com.example.alexeyglushkov.cachemanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by alexeyglushkov on 04.10.15.
 */
public interface StorageEntry {
    @Nullable Object getObject() throws Exception;

    void delete() throws Exception;

    @Nullable StorageMetadata getMetadata();
}
