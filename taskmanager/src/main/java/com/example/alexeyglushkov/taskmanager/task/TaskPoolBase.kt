package com.example.alexeyglushkov.taskmanager.task

import android.util.Log
import androidx.annotation.WorkerThread

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Created by alexeyglushkov on 30.12.14.
 */
// TODO: think about abstractTaskPool without tasks list to be able to inherit code in PriorityTaskProvider
abstract class TaskPoolBase(scope: CoroutineScope) : TaskPool {
    //TODO: think about weakref
    protected var listeners = mutableListOf<TaskPool.Listener>() // TODO: set one listener

    private var _scope: CoroutineScope? = null
    override var scope: CoroutineScope
        get() = _scope!!
        set(value) {
            _scope = value
        }

    override var userData: Any? = null

    init {
        _scope = scope
    }

    //// Events

    override fun onTaskStatusChanged(task: Task, oldStatus: Task.Status, newStatus: Task.Status) {
        if (Tasks.isTaskCompleted(task)) {
            Log.d(tag(), "going to remove task")
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

        scope.launch {
            addTaskOnThread(task)
        }
    }

    @WorkerThread
    private fun addTaskOnThread(task: Task) {
        if (task !is TaskBase) { assert(false); return }
        checkHandlerThread()

        task.addTaskStatusListener(this)

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
        scope.launch {
            Log.d(tag(), "will remove task")
            removeTaskOnThread(task)
        }
    }

    @WorkerThread
    private fun removeTaskOnThread(task: Task) {
        if (removeTaskInternal(task)) {
            triggerOnTaskRemoved(task)
        }
    }

    protected fun triggerOnTaskRemoved(task: Task) {
        checkHandlerThread()

        for (listener in listeners) {
            listener.onTaskRemoved(this@TaskPoolBase, task)
        }
    }

    @WorkerThread
    abstract protected fun removeTaskInternal(task: Task): Boolean

    override fun cancelTask(task: Task, info: Any?) {
        scope.launch {
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
