package com.example.alexeyglushkov.cachemanager.clients

/**
 * Created by alexeyglushkov on 03.03.18.
 */
interface Cache {
    enum class CacheMode {
        CHECK_CACHE_IF_ERROR_THEN_LOAD, IGNORE_CACHE, ONLY_LOAD_FROM_CACHE, ONLY_STORE_TO_CACHE
    }

    @Throws(Exception::class) fun putValue(key: String, value: Any)
    @Throws(Exception::class) fun <T> getCachedValue(cacheKey: String): T?

    var cacheMode: CacheMode

    //// Inner classes
    class CacheEmptyError(val cacheKey: String, throwable: Throwable?) : Error("Cache is empty", throwable) {
        companion object {
            private const val serialVersionUID = -783104001989492147L
        }
    }
}