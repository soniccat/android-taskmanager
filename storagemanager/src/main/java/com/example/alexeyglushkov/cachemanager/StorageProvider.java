package com.example.alexeyglushkov.cachemanager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by alexeyglushkov on 26.09.15.
 */
public interface StorageProvider {
    void put(@NonNull String key, @NonNull Object value, @Nullable StorageMetadata metadata) throws Exception;

    @Nullable
    Object getValue(@NonNull String key);

    StorageMetadata createMetadata();
    StorageMetadata getMetadata(@NonNull String key);
    Error remove(@NonNull String key);
    Error getError();

    StorageEntry getEntry(@NonNull String key);
    List<StorageEntry> getEntries();
    Error removeAll();
}
