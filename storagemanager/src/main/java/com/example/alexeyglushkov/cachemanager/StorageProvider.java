package com.example.alexeyglushkov.cachemanager;

import java.util.List;

/**
 * Created by alexeyglushkov on 26.09.15.
 */
public interface StorageProvider {
    Error put(String key, Object value, StorageMetadata metadata);
    Object getValue(String key);

    StorageMetadata createMetadata();
    StorageMetadata getMetadata(String key);
    Error remove(String key);
    Error getError();

    StorageEntry getEntry(String key);
    List<StorageEntry> getEntries();
    Error removeAll();
}
