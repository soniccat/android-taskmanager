package com.example.alexeyglushkov.cachemanager.disk.serializer

import com.example.alexeyglushkov.cachemanager.disk.DiskStorageMetadata
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.OutputStreamDataWriter
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater
import com.example.alexeyglushkov.tools.ExceptionTools
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import java.io.IOException
import java.io.OutputStream

/**
 * Created by alexeyglushkov on 11.09.16.
 */
class DiskMetadataWriter : OutputStreamDataWriter<DiskStorageMetadata?> {
    private var stream: OutputStream? = null
    override fun beginWrite(stream: OutputStream) {
        ExceptionTools.throwIfNull(stream, "DiskMetadataReader.beginWrite: stream is null")
        this.stream = stream
    }

    @Throws(IOException::class)
    override fun closeWrite() {
        ExceptionTools.throwIfNull(stream, "DiskMetadataReader.close: stream is null")
        stream!!.close()
    }

    @Throws(IOException::class)
    override fun write(metadata: DiskStorageMetadata) {
        ExceptionTools.throwIfNull(stream, "DiskMetadataReader.write: stream is null")
        val f = JsonFactory()
        var g: JsonGenerator? = null
        try {
            g = f.createGenerator(stream)
            g.writeStartObject()
            g.writeNumberField("contentSize", metadata.contentSize)
            g.writeNumberField("createTime", metadata.createTime)
            g.writeNumberField("expireTime", metadata.expireTime)
            val entryClass = metadata.entryClass
            if (entryClass != null) {
                g.writeStringField("entryClass", entryClass.name)
            }
        } finally {
            if (g != null) {
                try {
                    g.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun setProgressUpdater(progressUpdater: ProgressUpdater) {}
}