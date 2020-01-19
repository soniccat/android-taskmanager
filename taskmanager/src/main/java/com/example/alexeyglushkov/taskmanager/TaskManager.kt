package com.example.alexeyglushkov.taskmanager

import com.example.alexeyglushkov.taskmanager.coordinators.TaskManagerCoordinator
import com.example.alexeyglushkov.taskmanager.pool.TaskPool
import com.example.alexeyglushkov.taskmanager.providers.TaskProvider
import com.example.alexeyglushkov.taskmanager.task.Task

/**
 * Created by alexeyglushkov on 20.09.14.
 */

interface TaskManager : TaskPool, TaskPool.Listener {
    // TaskManagerCoordinator sets taskFilters to taskProviders
    var taskManagerCoordinator: TaskManagerCoordinator

    fun getLoadingTaskCount(): Int
    val taskProviders: List<TaskProvider> //always sorted by priority

    // tries to start a task ignoring TaskManagerCoordinator and without putting a task in any task provider
    // but task.loadPolicy will be taken into account anyway
    fun startImmediately(task: Task)
    fun cancel(task: Task, info: Any?)

    // Task Providers
    fun addTaskProvider(provider: TaskProvider)
    fun removeTaskProvider(provider: TaskProvider)
    fun getTaskProvider(id: String): TaskProvider?
    fun setTaskProviderPriority(provider: TaskProvider, priority: Int)

    // Listeners
    fun removeListener(listener: Listener)
    fun addListener(listener: Listener)

    interface Listener {
        fun onLimitsChanged(taskManager: TaskManager, taskType: Int, availableQueuePart: Float)

        // add additional flag showing that a task added to a loading or waiting queue
        fun onTaskAdded(pool: TaskPool, task: Task, isLoadingQueue: Boolean)
        fun onTaskRemoved(pool: TaskPool, task: Task, isLoadingQueue: Boolean)
        fun onTaskStatusChanged(task: Task, oldStatus: Task.Status)
    }
}
