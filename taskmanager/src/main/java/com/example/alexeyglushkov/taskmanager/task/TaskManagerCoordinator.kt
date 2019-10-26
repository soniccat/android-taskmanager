package com.example.alexeyglushkov.taskmanager.task

interface TaskManagerCoordinator {
    fun canAddMoreTasks(): Boolean

    fun onTaskProviderAdded(taskProvider: TaskProvider)
    fun onTaskProviderRemoved(taskProvider: TaskProvider)

    fun onTaskStartedLoading(pool: TaskPool, task: Task)
    fun onTaskFinishedLoading(pool: TaskPool, task: Task)
}