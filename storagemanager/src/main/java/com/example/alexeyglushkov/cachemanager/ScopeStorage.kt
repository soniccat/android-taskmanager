package com.example.alexeyglushkov.cachemanager


interface ScopeStorage {
    suspend fun put(key: String, value: Any, metadata: StorageMetadata?)
    suspend fun getValue(key: String): Any?
    suspend fun getMetadata(key: String): StorageMetadata?
    suspend fun remove(key: String)
    suspend fun getEntry(key: String): StorageEntry?
    suspend fun getEntries(): List<StorageEntry>
    suspend fun removeAll()
}