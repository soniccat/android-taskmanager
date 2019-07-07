package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.WorkerThread

import com.example.alexeyglushkov.tools.HandlerTools

import org.junit.Assert

import java.util.ArrayList

/**
 * Created by alexeyglushkov on 30.12.14.
 */
// TODO: think about abstractTaskPool without tasks list to be able to inherit code in PriorityTaskProvider
open class SimpleTaskPool(handler: Handler) : TaskPool {

    //TODO: think about weakref
    protected var listeners = mutableListOf<TaskPool.Listener>()
    private var _handler: Handler = handler
    override var handler: Handler
        get() = _handler
        set(handler) {
            checkHandlerThread()
            _handler = handler
        }

    //TODO: think about a map
    protected val _tasks = mutableListOf<Task>()
    override var userData: Any? = null

    @WorkerThread
    override fun getTaskCount(): Int {
        checkHandlerThread()
        return _tasks.size
    }

    override fun addTask(task: Task) {
        if (task !is TaskBase) { assert(false); return }

        if (!Tasks.isTaskReadyToStart(task)) {
            Log.d(TAG, "Can't put task " + task.javaClass.toString() + " because it has been added " + task.taskStatus.toString())
            return
        }

        // TaskPool must set Waiting status on the current thread
        task.private.taskStatus = Task.Status.Waiting

        HandlerTools.runOnHandlerThread(this.handler) {
            Log.d(TAG, "addTaskOnThread")
            addTaskOnThread(task)
        }
    }

    @WorkerThread
    private fun addTaskOnThread(task: Task) {
        if (task !is TaskBase) { assert(false); return }
        checkHandlerThread()

        task.addTaskStatusListener(this)
        _tasks.add(task)

        triggerOnTaskAdded(task)
    }

    protected open fun triggerOnTaskAdded(task: Task) {
        for (listener in listeners) {
            listener.onTaskAdded(this, task)
        }
    }

    override fun onTaskStatusChanged(task: Task, oldStatus: Task.Status, newStatus: Task.Status) {
        if (Tasks.isTaskCompleted(task)) {
            removeTask(task)
        }
    }

    override fun removeTask(task: Task) {
        HandlerTools.runOnHandlerThread(handler) {
            if (_tasks.remove(task)) {
                for (listener in listeners) {
                    listener.onTaskRemoved(this@SimpleTaskPool, task)
                }
            }
        }
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

    @WorkerThread
    override fun getTasks(): List<Task> {
        checkHandlerThread()

        return _tasks
    }

    override fun addListener(listener: TaskPool.Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: TaskPool.Listener) {
        listeners.remove(listener)
    }

    protected fun checkHandlerThread() {
        Assert.assertEquals(Looper.myLooper(), this.handler.looper)
    }

    companion object {
        internal val TAG = "SimpleTaskPool"
    }
}
