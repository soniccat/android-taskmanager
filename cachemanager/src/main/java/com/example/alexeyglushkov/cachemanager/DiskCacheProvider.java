package com.example.alexeyglushkov.cachemanager;

import com.noveogroup.android.cache.disk.DiskCache;
import com.noveogroup.android.cache.io.Serializer;

import java.io.File;

/**
 * Created by alexeyglushkov on 26.09.15.
 */
public class DiskCacheProvider<K, V> implements CacheProvider<K, V> {

    DiskCache<K> diskCache;
    Serializer<V> valueSerializer;

    public DiskCacheProvider(File directory, Serializer<K> keySerializer, Serializer<V> valueSerializer) {
        this.diskCache = DiskCache.create(directory, keySerializer);
        this.valueSerializer = valueSerializer;
    }

    @Override
    public Error storeData(K key, V value) {
        diskCache.put(key, value, valueSerializer);
        return null;
    }

    @Override
    public V readData(K key) {
        return diskCache.get(key, valueSerializer);
    }
}
