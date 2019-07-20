package com.example.alexeyglushkov.taskmanager.task

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Created by alexeyglushkov on 08.02.15.
 */
open class SimpleTaskExecutor : TaskExecutor {
    private val viewModelJob = Job()
    private val taskScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    override fun executeTask(task: Task) {
        taskScope.launch {
            start(task)
        }
    }

    suspend fun start(task: Task): Any? {
        return suspendCoroutine {
            task.taskCallback = object : Task.Callback {
                override fun onCompleted(cancelled: Boolean) {
                    val error = task.taskError
                    if (error != null) {
                        it.resumeWithException(error)
                    } else {
                        it.resume(task.taskResult)
                    }
                }
            }
            task.startTask()
        }
    }

    companion object {
        private val sThreadFactory = object : ThreadFactory {
            private val mCount = AtomicInteger(1)

            override fun newThread(r: Runnable): Thread {
                return Thread(r, "AsyncTask #" + mCount.getAndIncrement())
            }
        }
    }
}
