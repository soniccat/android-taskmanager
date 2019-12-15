package com.example.alexeyglushkov.cachemanager.clients

import com.example.alexeyglushkov.cachemanager.clients.Cache.CacheMode

interface ScopeCache {
    var cacheMode: CacheMode
    suspend fun putValue(key: String, value: Any)
    suspend fun <T> getCachedValue(cacheKey: String): T?
}