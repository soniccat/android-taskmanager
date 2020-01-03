package com.example.alexeyglushkov.taskmanager.task

import android.util.Log
import androidx.annotation.WorkerThread

/**
 * Created by alexeyglushkov on 30.12.14.
 */
abstract class TaskPoolBase(threadRunner: ThreadRunner) : TaskPool {
    //TODO: think about weakref
    protected var listeners = mutableListOf<TaskPool.Listener>() // TODO: set one listener

    private var _threadRunner: ThreadRunner? = null
    override var threadRunner: ThreadRunner
        get() = _threadRunner!!
        set(value) {
            _threadRunner = value
        }

    override var userData: Any? = null

    init {
        _threadRunner = threadRunner
    }

    //// Events

    override fun onTaskStatusChanged(task: Task, oldStatus: Task.Status, newStatus: Task.Status) {
        if (Tasks.isTaskFinished(task)) {
            removeTask(task)
        }
    }

    /// Actions

    override fun addTask(task: Task) {
        if (task !is TaskBase) { assert(false); return }
        if (!Tasks.isTaskReadyToStart(task)) {
            Log.d(tag(), "Can't put task " + task.javaClass.toString() + " because it has been added " + task.taskStatus.toString())
            return
        }

        // TaskPool must set Waiting status on the current thread
        task.private.taskStatus = Task.Status.Waiting

        threadRunner.launch {
            addTaskOnThread(task)
        }
    }

    @WorkerThread
    private fun addTaskOnThread(task: Task) {
        if (task !is TaskBase) { assert(false); return }
        checkHandlerThread()

        var resultTask = task
        val taskId = task.taskId
        if (taskId != null) {
            val oldTask = getTask(taskId)
            val listener = listeners.firstOrNull()
            if (oldTask != null && listener != null) {
                resultTask = listener.onTaskConflict(this, task, oldTask)
            }
        }

        if (resultTask == task) {
            task.addTaskStatusListener(this)
            addTaskInternal(task)
            triggerOnTaskAdded(task)
        }
    }

    @WorkerThread
    abstract protected fun addTaskInternal(task: Task)

    @WorkerThread
    protected open fun triggerOnTaskAdded(task: Task) {
        for (listener in listeners) {
            listener.onTaskAdded(this, task)
        }
    }

    override fun removeTask(task: Task) {
        threadRunner.launch {
            removeTaskOnThread(task)
        }
    }

    @WorkerThread
    private fun removeTaskOnThread(task: Task) {
        if (removeTaskInternal(task)) {
            onTaskRemoved(task)
        }
    }

    protected fun onTaskRemoved(task: Task) {
        task.removeTaskStatusListener(this)
        triggerOnTaskRemoved(task)
    }

    private fun triggerOnTaskRemoved(task: Task) {
        checkHandlerThread()

        for (listener in listeners) {
            listener.onTaskRemoved(this@TaskPoolBase, task)
        }
    }

    @WorkerThread
    abstract protected fun removeTaskInternal(task: Task): Boolean

    override fun cancelTask(task: Task, info: Any?) {
        threadRunner.launch {
            cancelTaskOnThread(task, info)
        }
    }

    @WorkerThread
    private fun cancelTaskOnThread(task: Task, info: Any?) {
        for (listener in listeners) {
            listener.onTaskCancelled(this@TaskPoolBase, task, info)
        }
    }

    override fun addListener(listener: TaskPool.Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: TaskPool.Listener) {
        listeners.remove(listener)
    }

    protected fun checkHandlerThread() {
        //Assert.assertEquals(Looper.myLooper(), this.handler.looper)
    }

    open protected fun tag() = "SimpleTaskPool"
}
