package com.example.alexeyglushkov.taskmanager.task

import android.util.Log

import com.example.alexeyglushkov.taskmanager.task.rx.CompletableTask
import com.example.alexeyglushkov.taskmanager.task.rx.MaybeTask
import com.example.alexeyglushkov.taskmanager.task.rx.SingleTask
import com.example.alexeyglushkov.tools.HandlerTools

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Created by alexeyglushkov on 28.12.14.
 */
object Tasks {
    // To automatically sync task state with your object state (isLoading for example)
    fun bindOnTaskCompletion(task: Task, listener: TaskListener) {
        task.addTaskStatusListener(object : Task.StatusListener {
            override fun onTaskStatusChanged(task: Task, oldStatus: Task.Status, newStatus: Task.Status) {
                Log.d("Bind--", "task $task from $oldStatus to $newStatus")

                if (newStatus == Task.Status.Finished || newStatus == Task.Status.Cancelled) {
                    HandlerTools.runOnMainThread { listener.setTaskCompleted(task) }
                }
            }
        })
    }

    fun isTaskReadyToStart(task: Task): Boolean {
        val st = task.taskStatus
        return st != Task.Status.Started && st != Task.Status.Finished && st != Task.Status.Cancelled
    }

    // TODO: convert to extension
    fun isTaskCompleted(task: Task): Boolean {
        val st = task.taskStatus
        return st == Task.Status.Finished || st == Task.Status.Cancelled
    }

    // TODO: convert to TaskPool extension
    suspend fun <T> run(task: Task, taskPool: TaskPool): T {
        return suspendCancellableCoroutine {
            it.invokeOnCancellation {
                taskPool.cancelTask(task, null)
            }

            val originalCallback = task.taskCallback
            task.taskCallback = object : Task.Callback {
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
            taskPool.addTask(task)
        }
    }

    // TODO: think about a better name
    // An implementer should store task and filter setTaskCompleted call with old task
    // This can happen due to task cancellation behavior. When a task was cancelled when completion block
    // was already added to the com.example.alexeyglushkov.wordteacher.main thread
    interface TaskListener {
        fun setTaskInProgress(task: Task)
        fun setTaskCompleted(task: Task)
    }
}
