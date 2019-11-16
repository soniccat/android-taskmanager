package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler
import androidx.annotation.WorkerThread
import kotlinx.coroutines.CoroutineScope

import java.util.HashMap

/**
 * Created by alexeyglushkov on 11.06.17.
 */

open class TaskProviderWrapper(val provider: TaskProvider) : TaskProvider {
    private val listenerMap = HashMap<TaskPool.Listener, TaskPool.Listener>()
    override var taskFilter: TaskProvider.TaskFilter?
        get() = provider.taskFilter
        set(value) {
            provider.taskFilter = value
        }

    override var threadRunner: ThreadRunner
        get() = provider.threadRunner
        set(handler) {
            provider.threadRunner = threadRunner
        }

    override var taskProviderId: String
        get() = provider.taskProviderId
        set(id) {
            provider.taskProviderId = id
        }

    override fun getTaskCount(): Int {
        return provider.getTaskCount()
    }

    override fun getTasks(): List<Task> {
        return provider.getTasks()
    }

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

    override fun cancelTask(task: Task, info: Any?) {
        provider.cancelTask(task, info)
    }

    override fun getTask(taskId: String): Task? {
        return provider.getTask(taskId)
    }

    override fun addListener(listener: TaskPool.Listener) {
        val wrapperListener = object : TaskPool.Listener {
            override fun onTaskAdded(pool: TaskPool, task: Task) {
                listener.onTaskAdded(this@TaskProviderWrapper, task)
            }

            override fun onTaskRemoved(pool: TaskPool, task: Task) {
                listener.onTaskRemoved(this@TaskProviderWrapper, task)
            }

            override fun onTaskCancelled(pool: TaskPool, task: Task, info: Any?) {
                listener.onTaskCancelled(this@TaskProviderWrapper, task, info)
            }

            override fun onTaskConflict(pool: TaskPool, newTask: Task, oldTask: Task): Task {
                return listener.onTaskConflict(this@TaskProviderWrapper, newTask, oldTask)
            }
        }

        listenerMap[listener] = wrapperListener
        provider.addListener(wrapperListener)
    }

    override fun removeListener(listener: TaskPool.Listener) {
        val wrappedListener = listenerMap[listener]
        if (wrappedListener != null) {
            provider.removeListener(wrappedListener)
            listenerMap.remove(listener)
        }
    }

    @WorkerThread
    override fun getTopTask(): Task? {
        return provider.getTopTask()
    }

    @WorkerThread
    override fun takeTopTask(): Task? {
        return provider.takeTopTask()
    }

    override fun onTaskStatusChanged(task: Task, oldStatus: Task.Status, newStatus: Task.Status) {
        provider.onTaskStatusChanged(task, oldStatus, newStatus)
    }
}
