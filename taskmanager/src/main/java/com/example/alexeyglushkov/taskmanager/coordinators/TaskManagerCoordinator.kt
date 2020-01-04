package com.example.alexeyglushkov.taskmanager.coordinators

import com.example.alexeyglushkov.taskmanager.pool.TaskPool
import com.example.alexeyglushkov.taskmanager.providers.TaskProvider
import com.example.alexeyglushkov.taskmanager.runners.ThreadRunner
import com.example.alexeyglushkov.taskmanager.task.Task

interface TaskManagerCoordinator {
    var threadRunner: ThreadRunner
    val taskFilter: TaskProvider.TaskFilter

    fun canAddMoreTasks(): Boolean

    fun onTaskStartedLoading(pool: TaskPool, task: Task)
    fun onTaskFinishedLoading(pool: TaskPool, task: Task)
}