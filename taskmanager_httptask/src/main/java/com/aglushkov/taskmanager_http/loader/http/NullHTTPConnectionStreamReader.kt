package com.aglushkov.taskmanager_http.loader.http

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater
import java.io.InputStream
import java.net.HttpURLConnection

class NullHTTPConnectionStreamReader<T>: HTTPConnectionStreamReader<T> {
    override fun beginRead(stream: InputStream) {
    }

    override fun read(): T? {
        return null
    }

    override fun closeRead() {
    }

    override fun setProgressUpdater(progressUpdater: ProgressUpdater) {
    }

    override fun handleConnectionResponse(connection: HttpURLConnection) {
    }
}