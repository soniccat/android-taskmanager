package com.aglushkov.taskmanager_http.loader.transport

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater

/**
 * Created by alexeyglushkov on 28.01.18.
 */
// TODO: use generic type for getData
interface TaskTransport {
    //void setContext(TaskTransportContext context);
    var listener: Listener?

    val id: String?
    val progressUpdater: ProgressUpdater?
    val data: Any?
    val error: Error?
    val isCancelled: Boolean

    suspend fun start()
    fun cancel()
    fun clear()

    interface Listener {
        fun getProgressUpdater(transport: TaskTransport, size: Float): ProgressUpdater
        fun needCancel(transport: TaskTransport): Boolean
    }
}