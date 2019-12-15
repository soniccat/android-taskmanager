package com.example.alexeyglushkov.cachemanager.disk

import com.example.alexeyglushkov.cachemanager.StorageEntry
import com.example.alexeyglushkov.streamlib.codecs.Codec
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReaders
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.OutputStreamDataWriters
import java.io.*

/**
 * Created by alexeyglushkov on 27.09.15.
 */
class DiskStorageEntry(private val file: File,
                       private var obj: Any?,
                       override val metadata:
                       DiskStorageMetadata?, private val codec: Codec<*>) : StorageEntry {
    val fileName: String
        get() = file.name

    @Throws(Exception::class)
    override fun getObject(): Any? {
        if (obj == null) {
            loadObject()
        }
        return obj
    }

    @Throws(Exception::class)
    private fun loadObject() {
        val stream: InputStream = FileInputStream(file)
        obj = InputStreamDataReaders.readOnce(codec, stream)
    }

    @Throws(Exception::class)
    fun write() {
        val os: OutputStream = FileOutputStream(file)
        val safeObj = obj
        if (safeObj != null) {
            // TODO: fix the warning
            OutputStreamDataWriters.writeOnce(codec as Codec<Any>, os, safeObj)
        }
    }

    @Throws(Exception::class)
    override fun delete() {
        if (!file.delete()) {
            throw Exception("DiskCacheEntry delete: can't delete file " + file.absolutePath)
        }
        if (metadata != null) {
            val file = metadata.file
            if (file != null && !metadata.file!!.delete()) {
                throw Exception("DiskCacheEntry delete: can't delete metadata " + metadata.file!!.absolutePath)
            }
        }
    }
}