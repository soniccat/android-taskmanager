package com.example.alexeyglushkov.cachemanager.disk

import com.example.alexeyglushkov.cachemanager.StorageMetadata
import com.example.alexeyglushkov.cachemanager.disk.serializer.DiskMetadataCodec
import com.example.alexeyglushkov.streamlib.codecs.Codec
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReaders
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.OutputStreamDataWriters
import com.example.alexeyglushkov.tools.TimeTools
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Created by alexeyglushkov on 04.10.15.
 */
class DiskStorageMetadata: StorageMetadata {
    override var contentSize: Long = 0
    override var createTime: Long = 0
    override var expireTime: Long = 0
    override var entryClass: Class<*>? = null

    var file: File? = null
    private val codec = createSerializer()

    //// Actions
    @Throws(Exception::class)
    fun write() {
        OutputStreamDataWriters.writeOnce(codec, FileOutputStream(file), this)
    }

    fun calculateSize(file: File) {
        contentSize = file.length()
    }

    //// Interfaces
    // StorageMetadata

    override fun isExpired(): Boolean {
        return hasExpireTime() && TimeTools.currentTimeSeconds() >= expireTime
    }

    fun hasExpireTime(): Boolean {
        return expireTime > 0
    }

    companion object {
        @Throws(Exception::class)
        fun load(file: File): DiskStorageMetadata? {
            var result: DiskStorageMetadata? = null
            val codec = createSerializer()

            result = InputStreamDataReaders.readOnce(codec, FileInputStream(file)) as DiskStorageMetadata?
            result?.file = file
            return result
        }

        //// Construction Methods
        private fun createSerializer(): Codec<Any> {
            return DiskMetadataCodec() as Codec<Any>
        }
    }
}