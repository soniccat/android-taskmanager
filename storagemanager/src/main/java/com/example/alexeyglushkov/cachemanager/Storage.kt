package com.example.alexeyglushkov.cachemanager

/**
 * Created by alexeyglushkov on 26.09.15.
 */
// TODO: add exception throwing, create safe interface like SafeStorage with optGetValue/
// TODO: add iterator support
interface Storage {
    @Throws(Exception::class) fun put(key: String, value: Any, metadata: StorageMetadata?)

    @Throws(Exception::class) fun getValue(key: String): Any?

    fun createMetadata(): StorageMetadata

    @Throws(Exception::class) fun getMetadata(key: String): StorageMetadata?
    @Throws(Exception::class) fun remove(key: String)

    @Throws(Exception::class) fun getEntry(key: String): StorageEntry?
    @Throws(Exception::class) fun getEntries(): List<StorageEntry>

    @Throws(Exception::class) fun removeAll()
}