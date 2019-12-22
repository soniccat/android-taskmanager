package com.example.alexeyglushkov.cachemanager.disk

import com.example.alexeyglushkov.cachemanager.Storage
import com.example.alexeyglushkov.cachemanager.StorageEntry
import com.example.alexeyglushkov.cachemanager.StorageMetadata
import com.example.alexeyglushkov.streamlib.codecs.Codec
import com.example.alexeyglushkov.streamlib.codecs.ObjectCodec
import com.example.alexeyglushkov.tools.TimeTools
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

/**
 * Created by alexeyglushkov on 26.09.15.
 */
// TODO: check synchronization
open class DiskStorage(val directory: File) : Storage {
    private var defaultCodec: Codec<*> = ObjectCodec<Any>()
    private val keySerializerMap: MutableMap<String, Codec<*>> = HashMap()
    private val classSerializerMap: MutableMap<Class<*>, Codec<*>> = HashMap()

    // TODO: need to clear this lockMap somewhere
    private val lockMap: MutableMap<String, Any> = HashMap()

    @Throws(Exception::class)
    override fun put(key: String, entry: Any, metadata: StorageMetadata?) {
        prepareDirectory()
        write(key, entry, metadata as DiskStorageMetadata?)
    }

    @Throws(Exception::class)
    private fun prepareDirectory() {
        if (!directory.exists()) {
            if (!directory.mkdir()) {
                throw Exception("DiskStorage.prepareDirectory.mkdir: can't create directory " + directory.absolutePath)
            }
        }
    }

    fun getKeyFile(key: String): File {
        return File(directory.path + File.separator + key)
    }

    private fun getKeyMetadataFile(key: String): File {
        val fileName = key + METADATA_PREFIX
        return File(directory.path + File.separator + fileName)
    }

    private fun isMetadataFile(file: File): Boolean {
        return file.name.endsWith(METADATA_PREFIX)
    }

    @Throws(Exception::class)
    private fun write(fileName: String, `object`: Any, metadata: DiskStorageMetadata?) {
        val key = getKeyName(fileName)
        val lockObject = getLockObject(key)
        synchronized(lockObject!!) { writeByKey(key, `object`, metadata) }
    }

    private fun getKeyName(fileName: String): String {
        val builder = StringBuilder(fileName)
        for (i in 0 until builder.length) {
            val c = builder[i]
            if (ReservedCharsSet.contains(c)) {
                builder.replace(i, i + 1, "_")
            }
        }
        return builder.toString()
    }

    @Synchronized
    private fun getLockObject(key: String): Any {
        var lockObject = lockMap[key]
        if (lockObject == null) {
            lockObject = createLockObject(key)
        }
        return lockObject
    }

    private fun createLockObject(key: String): Any {
        val lockObject = Any()
        lockMap[key] = lockObject
        return lockObject
    }

    // TODO: need to refactor
    @Throws(Exception::class)
    private fun writeByKey(key: String, `object`: Any, metadata: DiskStorageMetadata?) {
        val file = getKeyFile(key)
        if (!file.exists()) {
            createFile(file)
        }
        try {
            val codec = getSerializer(key, `object`.javaClass)
                    //?: throw Exception("Can't find a serializer for " + `object`.javaClass)
            val entry = DiskStorageEntry(file, `object`, metadata, codec)
            entry.write()
            metadata?.let { writeMetadata(it, `object`, key, file) }
        } catch (ex: Exception) {
            file.delete()
            if (metadata != null && metadata.file != null) {
                metadata.file!!.delete()
            }
            throw ex
        }
    }

    @Throws(IOException::class)
    private fun createFile(file: File) {
        val isFileCreated = file.createNewFile()
        if (!isFileCreated) {
            throw IOException("DiskStorage.write() createNewFile create error")
        }
    }

    private fun getSerializer(key: String?, cl: Class<*>?): Codec<*> {
        var codec = if (key != null) keySerializerMap[key] else null
        if (codec == null) {
            codec = if (cl != null) classSerializerMap[cl] else null
        }
        return codec ?: defaultCodec
    }

    @Throws(Exception::class)
    private fun writeMetadata(metadata: DiskStorageMetadata, `object`: Any, key: String, file: File) {
        metadata.file = getKeyMetadataFile(key)
        metadata.createTime = TimeTools.currentTimeSeconds()
        metadata.calculateSize(file)
        metadata.entryClass = `object`.javaClass
        metadata.write()
    }

    @Throws(Exception::class)
    override fun getValue(key: String): Any? {
        var result: Any? = null
        val entry = getEntry(key) as DiskStorageEntry?
        if (entry != null) {
            result = entry.getObject()
        }
        return result
    }

    override fun createMetadata(): DiskStorageMetadata {
        return DiskStorageMetadata()
    }

    @Throws(Exception::class)
    override fun getMetadata(key: String): StorageMetadata? {
        var result: StorageMetadata? = null
        val entry = getEntry(key)
        if (entry != null) {
            result = entry.metadata
        }
        return result
    }

    @Throws(Exception::class)
    override fun getEntry(fileName: String): StorageEntry? {
        var entry: StorageEntry? = null
        val key = getKeyName(fileName)

        try {
            val lockObject = getLockObject(key)
            synchronized(lockObject) { entry = getEntryByKey(key) }
        } catch (ex: Exception) {
        }
        return entry
    }

    @Throws(Exception::class)
    private fun getEntryByKey(key: String): StorageEntry {
        var entry: DiskStorageEntry? = null
        val file = getKeyFile(key)
        if (!file.exists()) {
            throw FileNotFoundException("DiskStorage.getEntryByKey() exists(): file doesn't exist ${key}")
        }
        var metadata: DiskStorageMetadata? = null
        var codec: Codec<*>? = null
        val metadataFile = getKeyMetadataFile(key)
        if (metadataFile.exists()) {
            try {
                metadata = DiskStorageMetadata.load(metadataFile)
                codec = if (metadata != null) {
                    getSerializer(key, metadata.entryClass)
                } else {
                    defaultCodec
                }
            } catch (e: Exception) {
                codec = defaultCodec
            }
        } else {
            codec = defaultCodec
        }
        if (codec == null) {
            throw Exception("Serializer is null")
        }
        entry = DiskStorageEntry(file, null, metadata, codec)
        return entry
    }

    @Throws(Exception::class)
    override fun remove(key: String) {
        val lockObject = getLockObject(key)
        synchronized(lockObject!!) {
            val entry = getEntry(key)
            entry?.delete()
        }
    }

    @Throws(Exception::class)
    override fun getEntries(): List<StorageEntry> {
        val entries: MutableList<StorageEntry> = ArrayList()
        if (!directory.exists()) {
            return entries
        }
        val files = directory.listFiles()
        if (files != null) {
            for (file in files) {
                if (!isMetadataFile(file)) {
                    val key = file.name
                    val lockObject = getLockObject(key)
                    synchronized(lockObject!!) {
                        val entry = getEntryByKey(key)
                        entries.add(entry)
                    }
                }
            }
        }
        return entries
    }

    val entryCount: Int
        get() {
            val files = directory.listFiles()
            var entryCount = 0
            for (file in files) {
                if (!isMetadataFile(file)) {
                    ++entryCount
                }
            }
            return entryCount
        }

    @Throws(Exception::class)
    override fun removeAll() {
        val entries = getEntries()
        for (file in entries) {
            val diskCacheEntry = file as DiskStorageEntry
            val key = diskCacheEntry.fileName
            val lockObject = getLockObject(key)
            synchronized(lockObject!!) { file.delete() }
        }
        if (!directory.delete()) {
            throw Exception("DiskStorage.removeAll() delete(): remove directory error")
        }
    }

    //// Setter
    fun setSerializer(codec: Codec<*>, cl: Class<*>) {
        classSerializerMap[cl] = codec
    }

    fun setSerializer(codec: Codec<*>, key: String) {
        keySerializerMap[key] = codec
    }

    fun setDefaultCodec(defaultCodec: Codec<*>) {
        this.defaultCodec = defaultCodec
    }

    companion object {
        private const val METADATA_PREFIX = "_metadata"
        private val ReservedCharsSet: Set<Char> = HashSet(Arrays.asList(
                '/', '|', '"', '?', '*', '<', '>', '\\', ':', '+', '[', ']'
        ))
    }

}