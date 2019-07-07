package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler
import android.os.Looper
import android.util.Log

import com.example.alexeyglushkov.tools.HandlerTools

import org.junit.Assert

import java.util.ArrayList

/**
 * Created by alexeyglushkov on 30.12.14.
 */
// TODO: think about abstractTaskPool without tasks list to be able to inherid code in PriorityTaskProvider
open class SimpleTaskPool(handler: Handler) : TaskPool {

    //TODO: think about weakref
    protected var listeners: MutableList<TaskPool.TaskPoolListener>
    override var handler: Handler? = null
        set(handler) {
            if (this.handler != null) {
                checkHandlerThread()
            }

            field = handler
        }

    //TODO: think about a map
    private val tasks: MutableList<Task>

    override var userData: Any? = null

    override val taskCount: Int
        get() {
            checkHandlerThread()

            return tasks.size
        }

    init {
        tasks = ArrayList()
        listeners = ArrayList<TaskPoolListener>()
        handler = handler
    }

    override fun addTask(task: Task) {
        if (!Tasks.isTaskReadyToStart(task)) {
            Log.d(TAG, "Can't put task " + task.javaClass.toString() + " because it has been added " + task.taskStatus.toString())
            return
        }

        // TaskPool must set Waiting status on the current thread
        task.private.taskStatus = Task.Status.Waiting

        HandlerTools.runOnHandlerThread(this.handler!!) {
            Log.d(TAG, "addTaskOnThread")
            addTaskOnThread(task)
        }
    }

    private fun addTaskOnThread(task: Task) {
        checkHandlerThread()

        task.addTaskStatusListener(this)
        tasks.add(task)

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
        HandlerTools.runOnHandlerThread(this.handler!!) {
            if (tasks.remove(task)) {
                for (listener in listeners) {
                    listener.onTaskRemoved(this@SimpleTaskPool, task)
                }
            }
        }
    }

    override fun getTask(taskId: String): Task? {
        checkHandlerThread()

        for (task in tasks) {
            if (task.taskId != null && task.taskId == taskId) {
                return task
            }
        }

        return null
    }

    override fun getTasks(): List<Task> {
        checkHandlerThread()

        return tasks
    }

    override fun addListener(listener: TaskPool.TaskPoolListener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: TaskPool.TaskPoolListener) {
        listeners.remove(listener)
    }

    protected fun checkHandlerThread() {
        Assert.assertEquals(Looper.myLooper(), this.handler!!.looper)
    }

    companion object {
        internal val TAG = "SimpleTaskPool"
    }
}
