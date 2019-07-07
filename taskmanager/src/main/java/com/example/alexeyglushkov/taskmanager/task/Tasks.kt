package com.example.alexeyglushkov.taskmanager.task

import android.util.Log

import com.example.alexeyglushkov.taskmanager.task.rx.CompletableTask
import com.example.alexeyglushkov.taskmanager.task.rx.MaybeTask
import com.example.alexeyglushkov.taskmanager.task.rx.SingleTask
import com.example.alexeyglushkov.tools.HandlerTools

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

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

    fun isTaskCompleted(task: Task): Boolean {
        val st = task.taskStatus
        return st == Task.Status.Finished || st == Task.Status.Cancelled
    }

    fun <T> fromSingle(single: Single<T>): Task {
        return SingleTask(single)
    }

    fun <T> toSingle(task: Task, taskPool: TaskPool): Single<T> {
        return Single.create { emitter ->
            val callback = task.taskCallback

            task.taskCallback = object : Task.Callback {
                override fun onCompleted(cancelled: Boolean) {
                    callback?.onCompleted(cancelled)

                    val error = task.taskError
                    if (error != null) {
                        emitter.onError(error)
                    } else {
                        try {
                            val result = task.taskResult as T
                            emitter.onSuccess(result)
                        } catch (e: Exception) {
                            emitter.onError(e)
                        }
                    }
                }
            }

            taskPool.addTask(task)
        }
    }

    fun <T> fromMaybe(maybe: Maybe<T>): Task {
        return MaybeTask(maybe)
    }

    fun fromCompletable(completable: Completable): Task {
        return CompletableTask(completable)
    }

    fun toCompletable(task: Task, taskPool: TaskPool): Completable {
        return Completable.create { emitter ->
            val callback = task.taskCallback

            task.taskCallback = object : Task.Callback {
                override fun onCompleted(cancelled: Boolean) {
                    callback?.onCompleted(cancelled)

                    val error = task.taskError
                    if (error != null) {
                        emitter.onError(error)
                    } else {
                        emitter.onComplete()
                    }
                }
            }

            taskPool.addTask(task)
        }
    }

    fun asPrivate(task: Task, block: (TaskPrivate) -> Unit) {
        block(task as TaskPrivate)
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
