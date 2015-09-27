package com.example.alexeyglushkov.cachemanager;

/**
 * Created by alexeyglushkov on 26.09.15.
 */
public interface CacheProvider {
    Error store(String key, CacheEntry value, CacheMetadata metadata);
    CacheEntry getEntry(String key);
    CacheMetadata getMetadata(String key);
    void remove(String key);
}
