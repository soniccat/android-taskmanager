package com.example.alexeyglushkov.cachemanager;

/**
 * Created by alexeyglushkov on 27.09.15.
 */
public interface CacheMetadata {
    long createTime();
    long expireTime();
    long size();
}
