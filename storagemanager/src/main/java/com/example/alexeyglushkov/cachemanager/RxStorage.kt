package com.example.alexeyglushkov.cachemanager

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

interface RxStorage {
    fun put(key: String, value: Any, metadata: StorageMetadata): Completable
    fun getValue(key: String): Maybe<Any>
    fun getMetadata(key: String): Maybe<StorageMetadata>
    fun remove(key: String): Completable
    fun getEntry(key: String): Maybe<StorageEntry>
    fun getEntries(): Single<List<StorageEntry>>
    fun removeAll(): Completable
}