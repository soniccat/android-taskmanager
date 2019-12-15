package com.example.alexeyglushkov.cachemanager

/**
 * Created by alexeyglushkov on 02.05.16.
 */
interface StorageMetadata {
    var createTime: Long
    var expireTime: Long
    var contentSize: Long
    var entryClass: Class<*>?

    fun isExpired(): Boolean
}