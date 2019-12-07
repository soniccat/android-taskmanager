package com.aglushkov.taskmanager_http.loader.http

import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReader
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater
import java.io.InputStream
import java.net.HttpURLConnection

/**
 * Created by alexeyglushkov on 31.10.15.
 */
open class HTTPConnectionStreamReaderAdaptor<T>(private val reader: InputStreamDataReader<T>) : HTTPConnectionStreamReader<T> {
    override fun handleConnectionResponse(connection: HttpURLConnection) {}

    @Throws(Exception::class)
    override fun beginRead(stream: InputStream) {
        reader.beginRead(stream)
    }

    @Throws(Exception::class)
    override fun closeRead() {
        reader.closeRead()
    }

    @Throws(Exception::class)
    override fun read(): T? {
        return reader.read()
    }

    override fun setProgressUpdater(progressUpdater: ProgressUpdater) {
        reader.setProgressUpdater(progressUpdater)
    }

}