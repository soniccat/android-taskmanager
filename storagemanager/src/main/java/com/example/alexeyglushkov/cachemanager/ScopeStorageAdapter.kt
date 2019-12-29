package com.example.alexeyglushkov.cachemanager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class ScopeStorageAdapter(private val storage: Storage, private val scope: CoroutineScope) : ScopeStorage {
    override suspend fun put(key: String, value: Any, metadata: StorageMetadata?) {
        withContext(scope.coroutineContext) {
            storage.put(key, value, metadata)
        }
    }

    override suspend fun getValue(key: String): Any? {
        return withContext(scope.coroutineContext) {
            storage.getValue(key)
        }
    }

    override suspend fun getMetadata(key: String): StorageMetadata? {
        return withContext(scope.coroutineContext) {
            storage.getMetadata(key)
        }
    }

    override suspend fun remove(key: String) {
        withContext(scope.coroutineContext) {
            storage.remove(key)
        }
    }

    override suspend fun getEntry(key: String): StorageEntry? {
        return withContext(scope.coroutineContext) {
            storage.getEntry(key)
        }
    }

    override suspend fun getEntries(): List<StorageEntry> {
        return withContext(scope.coroutineContext) {
            storage.getEntries()
        }
    }

    override suspend fun removeAll() {
        withContext(scope.coroutineContext) {
            storage.removeAll()
        }
    }
}