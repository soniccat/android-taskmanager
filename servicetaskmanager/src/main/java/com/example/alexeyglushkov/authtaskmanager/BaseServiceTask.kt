package com.example.alexeyglushkov.authtaskmanager

import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder
import com.example.alexeyglushkov.streamlib.progress.ProgressListener
import com.example.alexeyglushkov.taskmanager.task.Task
import com.example.alexeyglushkov.taskmanager.task.TaskBase
import com.example.alexeyglushkov.taskmanager.task.rx.TasksRx
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import java.lang.Exception

open class BaseServiceTask<T> : ServiceTask<T> {
    private var _task: TaskBase? = null
    override final var task: TaskBase
        set(value) {
            _task = value
        }
        get() = _task!!

    //// Initialization
    constructor()

    protected constructor(task: TaskBase): this() {
        this.task = task
    }

    //// Interface methods
    override fun clear() {
        task.private.clear()
    }

    //// Setters / Getters
    // Setters

    override val response: T?
        get() {
            val result = task.taskResult
            return if (result != null) result as T else null
        }

    override val commandError: Exception?
        get() = task.taskError

    override val isCancelled: Boolean
        get() = task.taskStatus === Task.Status.Cancelled

    override val connectionBuilder: HttpUrlConnectionBuilder
        get() = throw NotImplementedError()

    override val responseCode: Int
        get() = 0

    private var _progressListener: ProgressListener? = null
    override var progressListener: ProgressListener?
        get() = _progressListener
        set(value) {
            _progressListener?.let {
                task.removeTaskProgressListener(it)
            }
            _progressListener = value
            if (value != null) {
                task.addTaskProgressListener(value)
            }
        }

    companion object {
        fun <T> fromSingle(single: Single<T>?): BaseServiceTask<T> {
            return BaseServiceTask(TasksRx.fromSingle(single!!))
        }

        fun <T> fromMaybe(maybe: Maybe<T>?): BaseServiceTask<T> {
            return BaseServiceTask(TasksRx.fromMaybe(maybe!!))
        }

        fun fromCompletable(completable: Completable?): BaseServiceTask<Any> {
            return BaseServiceTask(TasksRx.fromCompletable(completable!!))
        }
    }
}