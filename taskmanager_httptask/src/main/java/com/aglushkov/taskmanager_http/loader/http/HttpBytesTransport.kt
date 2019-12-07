package com.aglushkov.taskmanager_http.loader.http

import com.example.alexeyglushkov.streamlib.data_readers_and_writers.ByteArrayReader
import java.net.HttpURLConnection

/**
 * Created by alexeyglushkov on 26.11.15.
 */
open class HttpBytesTransport<T>(provider: HttpURLConnectionProvider,
                                 handler: HTTPConnectionBytesReader<T>) : HttpTaskTransport<T>(provider, NullHTTPConnectionStreamReader<T>()) {
    protected var byteArrayReader: ByteArrayReader<T>

    init {
        byteArrayReader = ByteArrayReader(handler, true)
        streamReader = object : HTTPConnectionStreamReaderAdaptor<T>(byteArrayReader) {
            override fun handleConnectionResponse(connection: HttpURLConnection) {
                handler.handleConnectionResponse(connection)
            }
        }
    }
}