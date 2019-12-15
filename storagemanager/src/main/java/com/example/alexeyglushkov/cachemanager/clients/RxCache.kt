package com.example.alexeyglushkov.cachemanager.clients

import io.reactivex.Completable
import io.reactivex.Maybe

interface RxCache {
    fun putValue(key: String, value: Any): Completable
    fun <T> getCachedValue(cacheKey: String): Maybe<T>
    var cacheMode: Cache.CacheMode
}