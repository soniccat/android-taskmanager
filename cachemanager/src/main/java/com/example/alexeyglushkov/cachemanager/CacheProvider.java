package com.example.alexeyglushkov.cachemanager;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by alexeyglushkov on 26.09.15.
 */
public interface CacheProvider {
    Error put(String key, Object value, Serializable metadata);
    Object getValue(String key);
    Serializable getMetadata(String key);
    void remove(String key);
    Error getError();

    CacheEntry getEntry(String key);
}
