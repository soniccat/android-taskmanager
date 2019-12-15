package com.example.alexeyglushkov.cachemanager.disk.serializer

import com.example.alexeyglushkov.cachemanager.disk.DiskStorageMetadata
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReader
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater
import com.example.alexeyglushkov.tools.ExceptionTools
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream

/**
 * Created by alexeyglushkov on 11.09.16.
 */
class DiskMetadataReader : InputStreamDataReader<DiskStorageMetadata?> {
    private var stream: InputStream? = null
    override fun beginRead(stream: InputStream) {
        ExceptionTools.throwIfNull(stream, "DiskMetadataReader.beginRead: stream is null")
        this.stream = BufferedInputStream(stream)
    }

    @Throws(Exception::class)
    override fun closeRead() {
        ExceptionTools.throwIfNull(stream, "DiskMetadataReader.close: stream is null")
        stream!!.close()
    }

    @Throws(IOException::class)
    override fun read(): DiskStorageMetadata? {
        ExceptionTools.throwIfNull(stream, "DiskMetadataReader.read: stream is null")
        var result: DiskStorageMetadata? = null
        val md = SimpleModule("DiskMetadataModule", Version(1, 0, 0, null, null, null))
        md.addDeserializer(DiskStorageMetadata::class.java, DiskMetadataDeserializer(DiskStorageMetadata::class.java))
        val mapper = ObjectMapper()
        mapper.registerModule(md)
        result = mapper.readValue(stream, DiskStorageMetadata::class.java)
        return result
    }

    override fun setProgressUpdater(progressUpdater: ProgressUpdater) {}
}