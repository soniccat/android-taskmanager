package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.WorkerThread

import com.example.alexeyglushkov.tools.HandlerTools
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

import org.junit.Assert

import java.util.ArrayList

/**
 * Created by alexeyglushkov on 30.12.14.
 */
// TODO: think about abstractTaskPool without tasks list to be able to inherit code in PriorityTaskProvider
open class SimpleTaskPool(scope: CoroutineScope) : TaskPool {

    //TODO: think about weakref
    protected var listeners = mutableListOf<TaskPool.Listener>() // TODO: set one listener

    private var _scope: CoroutineScope? = null
    override var scope: CoroutineScope
        get() = _scope!!
        set(value) {
            _scope = value
        }

    //TODO: think about a map
    protected val _tasks = mutableListOf<Task>()
    override var userData: Any? = null

    init {
        _scope = scope
    }

    //// Events

    override fun onTaskStatusChanged(task: Task, oldStatus: Task.Status, newStatus: Task.Status) {
        if (Tasks.isTaskCompleted(task)) {
            removeTask(task)
        }
    }

    /// Actions

    override fun addTask(task: Task) {
        if (task !is TaskBase) { assert(false); return }

        if (!Tasks.isTaskReadyToStart(task)) {
            Log.d(TAG, "Can't put task " + task.javaClass.toString() + " because it has been added " + task.taskStatus.toString())
            return
        }

        // TaskPool must set Waiting status on the current thread
        task.private.taskStatus = Task.Status.Waiting

        scope.launch {
            Log.d(TAG, "addTaskOnThread")
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
            _tasks.add(task)
            triggerOnTaskAdded(task)
        }
    }

    @WorkerThread
    protected open fun triggerOnTaskAdded(task: Task) {
        for (listener in listeners) {
            listener.onTaskAdded(this, task)
        }
    }

    override fun removeTask(task: Task) {
        scope.launch {
            removeTaskOnThread(task)
        }
    }

    @WorkerThread
    private fun removeTaskOnThread(task: Task) {
        if (_tasks.remove(task)) {
            for (listener in listeners) {
                listener.onTaskRemoved(this@SimpleTaskPool, task)
            }
        }
    }

    override fun cancelTask(task: Task, info: Any?) {
        scope.launch {
            cancelTaskOnThread(task, info)
        }
    }

    @WorkerThread
    private fun cancelTaskOnThread(task: Task, info: Any?) {
        for (listener in listeners) {
            listener.onTaskCancelled(this@SimpleTaskPool, task, info)
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

    //// Getters

    @WorkerThread
    override fun getTaskCount(): Int {
        checkHandlerThread()
        return _tasks.size
    }

    @WorkerThread
    override fun getTasks(): List<Task> {
        checkHandlerThread()

        return _tasks
    }

    @WorkerThread
    override fun getTask(taskId: String): Task? {
        checkHandlerThread()

        for (task in _tasks) {
            if (task.taskId != null && task.taskId == taskId) {
                return task
            }
        }

        return null
    }

    companion object {
        internal val TAG = "SimpleTaskPool"
    }
}
