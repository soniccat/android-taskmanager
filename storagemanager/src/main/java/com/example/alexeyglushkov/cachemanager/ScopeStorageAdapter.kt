package com.example.alexeyglushkov.cachemanager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

class ScopeStorageAdapter(private val storage: Storage, private val scope: CoroutineScope) : ScopeStorage {
    override suspend fun put(key: String, value: Any, metadata: StorageMetadata?) {
        scope.async {
            storage.put(key, value, metadata)
        }.await()
    }

    override suspend fun getValue(key: String): Any? {
        return scope.async {
            storage.getValue(key)
        }.await()
    }

    override suspend fun getMetadata(key: String): StorageMetadata? {
        return scope.async {
            storage.getMetadata(key)
        }.await()
    }

    override suspend fun remove(key: String) {
        scope.async {
            storage.remove(key)
        }.await()
    }

    override suspend fun getEntry(key: String): StorageEntry? {
        return scope.async {
            storage.getEntry(key)
        }.await()
    }

    override suspend fun getEntries(): List<StorageEntry> {
        return scope.async {
            storage.getEntries()
        }.await()
    }

    override suspend fun removeAll() {
        scope.async {
            storage.removeAll()
        }.await()
    }

}