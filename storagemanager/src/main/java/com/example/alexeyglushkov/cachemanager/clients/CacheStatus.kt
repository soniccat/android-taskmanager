package com.example.alexeyglushkov.cachemanager.clients

import com.example.alexeyglushkov.cachemanager.clients.Cache.CacheMode

/**
 * Created by alexeyglushkov on 03.03.18.
 */
object CacheStatus {
    fun canLoadFromCache(cache: Cache?): Boolean {
        if (cache == null) {
            return false
        }

        val cacheMode = cache.cacheMode
        return cacheMode != CacheMode.IGNORE_CACHE &&
                cacheMode != CacheMode.ONLY_STORE_TO_CACHE
    }

    fun canWriteToCache(client: Cache?): Boolean {
        if (client == null) {
            return false
        }

        val cacheMode = client.cacheMode
        return cacheMode != CacheMode.IGNORE_CACHE &&
                cacheMode != CacheMode.ONLY_LOAD_FROM_CACHE
    }
}