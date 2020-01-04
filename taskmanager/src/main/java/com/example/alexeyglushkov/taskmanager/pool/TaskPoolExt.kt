package com.example.alexeyglushkov.taskmanager.pool

import com.example.alexeyglushkov.taskmanager.task.Task
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> TaskPool.start(task: Task): T {
    return suspendCancellableCoroutine {
        it.invokeOnCancellation {
            cancelTask(task, null)
        }

        val originalCallback = task.finishCallback
        task.finishCallback = object : Task.Callback {
            override fun onCompleted(cancelled: Boolean) {
                originalCallback?.onCompleted(cancelled)

                val error = task.taskError
                if (error != null) {
                    it.resumeWithException(error)
                } else if (cancelled) {
                    it.resumeWithException(CancellationException())
                } else {
                    it.resume(task.taskResult as T)
                }
            }
        }
        addTask(task)
    }
}
