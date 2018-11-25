package com.example.alexeyglushkov.cachemanager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public interface RxStorage {
    Completable put(@NonNull String key, @NonNull Object value, @Nullable StorageMetadata metadata);

    Maybe<Object> getValue(@NonNull String key);

    Maybe<StorageMetadata> getMetadata(@NonNull String key);
    Completable remove(@NonNull String key);

    Maybe<StorageEntry> getEntry(@NonNull String key);
    Single<List<StorageEntry>> getEntries();
    Completable removeAll();
}
