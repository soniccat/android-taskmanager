package com.example.alexeyglushkov.cachemanager.clients

import com.example.alexeyglushkov.cachemanager.clients.Cache.CacheMode
import io.reactivex.Completable
import io.reactivex.Maybe

class RxCacheAdapter(private val cache: Cache) : RxCache {
    override var cacheMode: CacheMode
        get() = cache.cacheMode
        set(value) {
            cache.cacheMode = value
        }

    override fun putValue(key: String, value: Any): Completable {
        return Completable.create { emitter ->
            try {
                cache.putValue(key, value)
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    override fun <T> getCachedValue(cacheKey: String): Maybe<T> {
        return Maybe.create { emitter ->
            try {
                val obj = cache.getCachedValue<T>(cacheKey)
                if (obj != null) {
                    emitter.onSuccess(obj)
                } else {
                    emitter.onComplete()
                }
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }
}