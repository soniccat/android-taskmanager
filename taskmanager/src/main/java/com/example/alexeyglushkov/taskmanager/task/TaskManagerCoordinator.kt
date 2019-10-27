package com.example.alexeyglushkov.taskmanager.task

interface TaskManagerCoordinator {
    val taskFilter: TaskProvider.TaskFilter

    fun canAddMoreTasks(): Boolean

    fun onTaskStartedLoading(pool: TaskPool, task: Task)
    fun onTaskFinishedLoading(pool: TaskPool, task: Task)
}