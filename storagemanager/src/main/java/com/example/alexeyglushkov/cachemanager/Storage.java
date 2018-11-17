package com.example.alexeyglushkov.cachemanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * Created by alexeyglushkov on 26.09.15.
 */
// TODO: add exception throwing, create safe interface like SafeStorage with optGetValue/
// TODO: add iterator support
public interface Storage {
    void put(@NonNull String key, @NonNull Object value, @Nullable StorageMetadata metadata) throws Exception;

    @Nullable Object getValue(@NonNull String key);

    @NonNull StorageMetadata createMetadata();

    @Nullable StorageMetadata getMetadata(@NonNull String key);
    void remove(@NonNull String key) throws Exception;

    @Nullable StorageEntry getEntry(@NonNull String key);
    List<StorageEntry> getEntries();
    void removeAll() throws Exception;
}
