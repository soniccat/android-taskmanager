package com.example.alexeyglushkov.taskmanager.task

import android.util.Log
import com.example.alexeyglushkov.tools.HandlerTools

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
                //Log.d("Bind--", "task $task from $oldStatus to $newStatus")

                if (isTaskFinished(task)) {
                    HandlerTools.runOnMainThread { listener.setTaskCompleted(task) }
                }
            }
        })
    }

    fun isTaskReadyToStart(task: Task): Boolean {
        val st = task.taskStatus
        return st == Task.Status.NotStarted ||
                st == Task.Status.Waiting
    }

    fun isTaskBlocked(task: Task): Boolean {
        return task.taskStatus == Task.Status.Blocked
    }

    // TODO: convert to extension
    fun isTaskFinished(task: Task): Boolean {
        val st = task.taskStatus
        return st == Task.Status.Completed || st == Task.Status.Cancelled
    }

    // TODO: convert to TaskPool extension
    suspend fun <T> run(task: Task, taskPool: TaskPool): T {
        return suspendCancellableCoroutine {
            it.invokeOnCancellation {
                taskPool.cancelTask(task, null)
            }

            val originalCallback = task.finishCallback
            task.finishCallback = object : Task.Callback {
                override fun onCompleted(cancelled: Boolean) {
                    //Thread.sleep(1000)
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

    fun logTask(tag: String, task: Task, prefix: String) {
        Log.d(tag, prefix + " [" + task.javaClass.toString() + "(" + task.taskStatus + ")" + " id= " + task.taskId + " priority= " + task.taskPriority + " type= " + task.taskType + " time " + task.taskDuration() + "] " + task)
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
