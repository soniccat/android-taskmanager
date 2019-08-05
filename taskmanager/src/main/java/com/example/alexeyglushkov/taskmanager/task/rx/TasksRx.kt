package com.example.alexeyglushkov.taskmanager.task.rx

import com.example.alexeyglushkov.taskmanager.task.Task
import com.example.alexeyglushkov.taskmanager.task.TaskBase
import com.example.alexeyglushkov.taskmanager.task.TaskPool
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

object TasksRx {
    fun <T> fromSingle(single: Single<T>): TaskBase {
        return SingleTask(single)
    }

    fun <T> toSingle(task: Task, taskPool: TaskPool): Single<T> {
        return Single.create { emitter ->
            val callback = task.taskCallback
            emitter.setCancellable {
                taskPool.cancelTask(task, null)
            }

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

    fun <T> fromMaybe(maybe: Maybe<T>): TaskBase {
        return MaybeTask(maybe)
    }

    fun fromCompletable(completable: Completable): TaskBase {
        return CompletableTask(completable)
    }

    fun toCompletable(task: Task, taskPool: TaskPool): Completable {
        return Completable.create { emitter ->
            val callback = task.taskCallback
            emitter.setCancellable {
                taskPool.cancelTask(task, null)
            }

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
}