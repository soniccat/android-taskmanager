package com.example.alexeyglushkov.cachemanager.clients

import com.example.alexeyglushkov.cachemanager.clients.Cache.CacheMode

interface ScopeCache {
    suspend fun putValue(key: String, value: Any)
    suspend fun <T> getCachedValue(cacheKey: String): T?

    suspend fun getCacheMode(): CacheMode
    suspend fun setCacheMode(mode: CacheMode)
}