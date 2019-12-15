package com.example.alexeyglushkov.cachemanager.clients

import com.example.alexeyglushkov.cachemanager.clients.Cache.CacheMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScopeCacheAdapter(private val cache: Cache, private val scope: CoroutineScope) : ScopeCache {
    override var cacheMode: CacheMode
        get() = cache.cacheMode
        set(value) {
            cache.cacheMode = value
        }

    override suspend fun putValue(key: String, value: Any) {
        scope.launch {
            cache.putValue(key, value)
        }
    }

    override suspend fun <T> getCachedValue(cacheKey: String): T? {
        return withContext(scope.coroutineContext) {
            cache.getCachedValue<T>(cacheKey)
        }
    }
}