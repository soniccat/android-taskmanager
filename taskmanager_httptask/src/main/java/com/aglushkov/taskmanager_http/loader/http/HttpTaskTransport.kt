package com.aglushkov.taskmanager_http.loader.http

import android.util.Log
import com.aglushkov.taskmanager_http.loader.transport.TaskTransport
import com.aglushkov.taskmanager_http.loader.transport.TaskTransport.Listener
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReader
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReaders
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.StringReader
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection

/**
 * Created by alexeyglushkov on 28.01.18.
 */
open class HttpTaskTransport<T>(
        protected var provider: HttpURLConnectionProvider,
        protected var streamReader: HTTPConnectionStreamReader<T>
) : TaskTransport {
    var contentLength = 0

    // out
    var responseCode = 0
        protected set

    private var _isCancelled = false
    override val isCancelled: Boolean
        get() = _isCancelled

    private var _data: T? = null
    override val data: T?
        get() = _data

    protected var _error: Error? = null
    override val error: Error?
        get() = _error


    var _progressUpdater: ProgressUpdater? = null
    override val progressUpdater: ProgressUpdater?
        get() = _progressUpdater

    override var listener: Listener? = null
    override val id: String?
        get() {
            return provider.url.toString()
        }

    override suspend fun start() {
        val connection = provider.getUrlConnection()
        try {
            withContext(Dispatchers.IO) {
                connection.connect()
            }

            val length = connection.contentLength
            if (length != -1) {
                contentLength = length
            }

            responseCode = connection.responseCode
            Log.d("HttpLoadTask", "HttpLoadingContext: The response is: $responseCode\n")
            _progressUpdater = if (listener != null) listener?.getProgressUpdater(this, contentLength.toFloat()) else null

            progressUpdater?.let {
                streamReader.setProgressUpdater(it)
            }

            if (listener == null || listener?.needCancel(this) == true) {
                _isCancelled = true

            } else {
                streamReader.handleConnectionResponse(connection)
                val stream = connection.inputStream
                var data: T? = null

                // TODO: support suspended in stream reader
                withContext(Dispatchers.IO) {
                    data = handleStream(stream)
                }

                if (listener == null || listener?.needCancel(this) == true) {
                    _isCancelled = true
                } else {
                    onDataLoaded(data)
                }
            }
        } catch (e: Exception) {
            val errorString = getErrorString(connection)
            val error = Error(errorString)
            _error = error
            Log.d("HttpLoadTask", error.toString())
        } finally {
            connection.disconnect()
        }
    }

    open protected fun onDataLoaded(handledData: T?) {
        _data = handledData
    }

    @Throws(Exception::class)
    protected fun handleStream(stream: InputStream?): T? {
        return InputStreamDataReaders.readOnce(streamReader, stream)
    }

    override fun cancel() {
        _progressUpdater = null
    }

    override fun clear() {
        responseCode = 0
        _isCancelled = false
        _data = null
        _error = null

        progressUpdater?.clear()
    }

    // Getters

    private fun getErrorString(connection: HttpURLConnection): String {
        var result = "HttpLoadTask load error"
        try {
            val errorReader = StringReader<String>(null)
            var errorString: String? = ""
            val errorStream = connection.errorStream
            if (errorStream != null) {
                errorString = InputStreamDataReaders.readOnce(errorReader, errorStream)
            }
            result = "HttpLoadTask load error, url " + connection.url + " code: " + connection.responseCode + " message: " + connection.responseMessage + " response " + errorString
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}