package com.example.alexeyglushkov.cachemanager;

/**
 * Created by alexeyglushkov on 26.09.15.
 */
public interface CacheProvider<K,V> {
    Error storeData(K key, V value);
    V readData(K key);
}
