package com.example.alexeyglushkov.cachemanager;

/**
 * Created by alexeyglushkov on 04.10.15.
 */
public interface StorageEntry {
    Object getObject() throws Exception;
    Error delete();
    StorageMetadata getMetadata();
}
