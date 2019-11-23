package com.aglushkov.taskmanager_http.loader.http

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater
import com.aglushkov.taskmanager_http.loader.transport.TaskTransport
import com.example.alexeyglushkov.taskmanager.task.SimpleTask

class TransportTask : SimpleTask, TaskTransport.Listener {
    lateinit private var transport: TaskTransport

    constructor() : super()

    constructor(transport: TaskTransport) : super() {
        setTransport(transport)
    }

    fun setTransport(transport: TaskTransport) {
        val oldTransport = getTransport()
        if (oldTransport != null && oldTransport.listener === this) {
            oldTransport.listener = null
        }

        this.transport = transport
        transport.listener = this

        val transportId = transport.id
        if (transportId != null) {
            taskId = transportId
        }
    }

    fun getTransport(): TaskTransport? {
        return transport
    }

    override suspend fun startTask() {
        transport.start()

        if (needCancelTask) {
            setIsCancelled()

        } else {
            val e = transport.error
            val d = transport.data

            if (e != null) {
                setError(e)
            } else {
                taskResult = d
            }
        }

        private.handleTaskCompletion()
    }

    fun setError(error: Error) {
        private.taskError = error
    }

    override fun canBeCancelledImmediately(): Boolean {
        return true
    }

    override fun clear() {
        super.clear()
        transport.clear()
    }

    override fun cancelTask(info: Any?) {
        val progressUpdater = transport.progressUpdater

        if (progressUpdater != null) {
            transport.cancel()
            progressUpdater.cancel(info) // cancel will call this method again
        } else {
            super.cancelTask(info)
        }
    }

    // TaskTransport.Listener

    override fun getProgressUpdater(transport: TaskTransport, size: Float): ProgressUpdater {
        return createProgressUpdater(size)
    }

    override fun needCancel(transport: TaskTransport): Boolean {
        return needCancelTask
    }
}
