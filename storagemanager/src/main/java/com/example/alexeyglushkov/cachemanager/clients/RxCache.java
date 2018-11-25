package com.example.alexeyglushkov.cachemanager.clients;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Completable;
import io.reactivex.Maybe;

public interface RxCache {
    Completable putValue(@NonNull String key, @NonNull Object value);
    <T> Maybe<T> getCachedValue(@NonNull String cacheKey);

    Cache.CacheMode getCacheMode();
    void setCacheMode(Cache.CacheMode mode);
}
