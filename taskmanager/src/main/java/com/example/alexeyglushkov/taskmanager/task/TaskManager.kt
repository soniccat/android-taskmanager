package com.example.alexeyglushkov.taskmanager.task

import android.util.SparseArray

import androidx.collection.SparseArrayCompat
import kotlinx.coroutines.CoroutineScope

import java.util.ArrayList

/**
 * Created by alexeyglushkov on 20.09.14.
 */

interface TaskManager : TaskPool, TaskPool.Listener {
    // Task Limits
    //var maxLoadingTasks: Int
    //val limits: SparseArrayCompat<Float>
    //val usedSpace: SparseArrayCompat<Int> //type -> task count from loadingTasks
    val loadingTaskCount: Int
    val taskProviders: List<TaskProvider> //always sorted by priority

    // TODO: extract limits to a Rule class: TaskManagerRule/TaskManagerConstraint
    fun setLimit(taskType: Int, availableQueuePart: Float)

    // Task Running

    // Put a task, the same task can't be putted twice to the TaskManager
    // TODO: handle this situation well
    fun startImmediately(task: Task)

    fun cancel(task: Task, info: Any?)

    // Task Providers
    fun addTaskProvider(provider: TaskProvider)

    fun removeTaskProvider(provider: TaskProvider)

    fun getTaskProvider(id: String): TaskProvider?
    // TODO: rename to addTaskProvider
    fun setTaskProviderPriority(provider: TaskProvider, priority: Int)



    // Listeners
    fun removeListener(listener: Listener)
    fun addListener(listener: Listener)

    interface Listener {
        fun onLimitsChanged(taskManager: TaskManager, taskType: Int, availableQueuePart: Float)

        // add additional flag showing that a task added to a loading or waiting queue
        fun onTaskAdded(pool: TaskPool, task: Task, isLoadingQueue: Boolean)

        fun onTaskRemoved(pool: TaskPool, task: Task, isLoadingQueue: Boolean)
    }
}
