package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler

import java.util.ArrayList
import java.util.HashMap

/**
 * Created by alexeyglushkov on 11.06.17.
 */

open class TaskProviderWrapper(val provider: TaskProvider) : TaskProvider {
    private val listenerMap = HashMap<TaskPool.TaskPoolListener, TaskPool.TaskPoolListener>()

    override var handler: Handler
        get() = provider.handler
        set(handler) {
            provider.handler = handler
        }

    override var taskProviderId: String
        get() = provider.taskProviderId
        set(id) {
            provider.taskProviderId = id
        }

    override val taskCount: Int
        get() = provider.taskCount

    override val tasks: List<Task>
        get() = provider.tasks

    override var userData: Any?
        get() = provider.userData
        set(data) {
            provider.userData = data
        }

    override var priority: Int
        get() = provider.priority
        set(priority) {
            provider.priority = priority
        }

    override fun addTask(task: Task) {
        provider.addTask(task)
    }

    override fun removeTask(task: Task) {
        provider.removeTask(task)
    }

    override fun getTask(taskId: String): Task? {
        return provider.getTask(taskId)
    }

    override fun addListener(listener: TaskPool.TaskPoolListener) {
        val wrapperListener = object : TaskPool.TaskPoolListener {
            override fun onTaskAdded(pool: TaskPool, task: Task) {
                listener.onTaskAdded(this@TaskProviderWrapper, task)
            }

            override fun onTaskRemoved(pool: TaskPool, task: Task) {
                listener.onTaskRemoved(this@TaskProviderWrapper, task)
            }
        }

        listenerMap[listener] = wrapperListener
        provider.addListener(wrapperListener)
    }

    override fun removeListener(listener: TaskPool.TaskPoolListener) {
        val wrappedListener = listenerMap[listener]
        if (wrappedListener != null) {
            provider.removeListener(wrappedListener)
            listenerMap.remove(listener)
        }
    }

    override fun getTopTask(typesToFilter: List<Int>): Task? {
        return provider.getTopTask(typesToFilter)
    }

    override fun takeTopTask(typesToFilter: List<Int>): Task? {
        return provider.takeTopTask(typesToFilter)
    }

    override fun onTaskStatusChanged(task: Task, oldStatus: Task.Status, newStatus: Task.Status) {
        provider.onTaskStatusChanged(task, oldStatus, newStatus)
    }
}
