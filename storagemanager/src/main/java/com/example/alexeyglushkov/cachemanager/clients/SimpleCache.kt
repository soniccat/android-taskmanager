package com.example.alexeyglushkov.cachemanager.clients

import com.example.alexeyglushkov.cachemanager.Storage
import com.example.alexeyglushkov.cachemanager.clients.Cache.CacheMode
import com.example.alexeyglushkov.tools.TimeTools

/**
 * Created by alexeyglushkov on 03.03.18.
 */
// Supports isExpired
class SimpleCache @JvmOverloads constructor(internal val storage: Storage, private var defaultDuration: Long = 0) : Cache {
    override var cacheMode = CacheMode.CHECK_CACHE_IF_ERROR_THEN_LOAD
    var deleteIfExpired = true

    //// Actions
    @Throws(Exception::class)
    override fun putValue(key: String, value: Any) {
        putValue(key, value, defaultDuration)
    }

    @Throws(Exception::class)
    fun putValue(key: String?, value: Any?, duration: Long) {
        val metadata = storage.createMetadata()
        if (duration != 0L) {
            val expireTime = TimeTools.currentTimeSeconds() + duration
            metadata.expireTime = expireTime
        }
        storage.put(key!!, value!!, metadata)
    }

    @Throws(Exception::class)
    override fun <T> getCachedValue(cacheKey: String): T? {
        val metadata = storage.getMetadata(cacheKey)
        var result: T? = null
        if (metadata != null) {
            if (deleteIfExpired && metadata.isExpired()) {
                storage.remove(cacheKey)
            } else {
                result = storage.getValue(cacheKey) as T?
            }
        }
        return result
    }
}