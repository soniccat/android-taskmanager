package com.example.alexeyglushkov.cachemanager;

import java.io.Serializable;

/**
 * Created by alexeyglushkov on 04.10.15.
 */
public interface CacheEntry {
    Object getObject();
    Error delete();
    CacheMetadata getMetadata();
}
