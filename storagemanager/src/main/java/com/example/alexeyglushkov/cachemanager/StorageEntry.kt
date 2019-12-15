package com.example.alexeyglushkov.cachemanager

/**
 * Created by alexeyglushkov on 04.10.15.
 */
interface StorageEntry {
    fun getObject(): Any?

    @Throws(Exception::class)
    fun delete()

    val metadata: StorageMetadata?
}