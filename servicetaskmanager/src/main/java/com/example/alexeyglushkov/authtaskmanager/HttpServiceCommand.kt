package com.example.alexeyglushkov.authtaskmanager

import com.aglushkov.taskmanager_http.loader.http.HTTPConnectionBytesReader
import com.aglushkov.taskmanager_http.loader.http.HttpCacheableTransport
import com.aglushkov.taskmanager_http.loader.http.HttpURLConnectionProvider
import com.aglushkov.taskmanager_http.loader.http.TransportTask
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder
import com.example.alexeyglushkov.cachemanager.clients.Cache
import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler
import com.example.alexeyglushkov.taskmanager.task.Task
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by alexeyglushkov on 04.11.15.
 */
open class HttpServiceCommand<T>(// Getters
        override final val connectionBuilder: HttpUrlConnectionBuilder,
        handler: ByteArrayHandler<T>) : BaseServiceTask<T>() {

    //// Setters / Getters
// Setters
    private fun setTaskConnectionBuilder(task: Task, connectionBuilder: HttpUrlConnectionBuilder) {
        task.taskId = connectionBuilder.stringUrl
        task.loadPolicy = Task.LoadPolicy.CancelPreviouslyAdded
    }

    fun setCacheClient(cacheClient: Cache) {
        val transport = transportTask.transport as Transport<*>
        transport.setCacheClient(cacheClient)
    }

    override val responseCode: Int
        get() {
            val transport = transportTask.transport as Transport<*>
            return transport.responseCode
        }

    private val transportTask: TransportTask
        private get() = task as TransportTask

    //// Classes
    private class Transport<T>(builder: HttpUrlConnectionBuilder, handler: ByteArrayHandler<T>) : HttpCacheableTransport<T>(createProvider(builder), createStreamReader(handler)) {
        companion object {
            private fun createProvider(builder: HttpUrlConnectionBuilder): HttpURLConnectionProvider {
                return object : HttpURLConnectionProvider {
                    override fun getUrlConnection(): HttpURLConnection {
                        return builder.build()!!
                    }

                    override fun getURL(): URL {
                        return builder.getUrl()!!
                    }
                }
            }

            private fun <T> createStreamReader(handler: ByteArrayHandler<T>): HTTPConnectionBytesReader<T> {
                return object : HTTPConnectionBytesReader<T> {
                    override fun convert(`object`: ByteArray): T {
                        return handler.convert(`object`)
                    }

                    override fun handleConnectionResponse(connection: HttpURLConnection) {}
                }
            }
        }
    }

    init {
        val task = TransportTask()
        setTaskConnectionBuilder(task, connectionBuilder)
        task.transport = Transport(connectionBuilder, handler)
        this.task = task
    }
}