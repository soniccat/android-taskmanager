package com.example.alexeyglushkov.cachemanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by alexeyglushkov on 26.09.15.
 */
public interface CacheProvider {
    Error put(String key, Object value, CacheMetadata metadata);
    Object getValue(String key);

    CacheMetadata createMetadata();
    CacheMetadata getMetadata(String key);
    Error remove(String key);
    Error getError();

    CacheEntry getEntry(String key);
    List<CacheEntry> getEntries();
    Error removeAll();
}
