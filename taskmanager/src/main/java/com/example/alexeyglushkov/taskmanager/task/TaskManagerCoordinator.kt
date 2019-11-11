package com.example.alexeyglushkov.taskmanager.task

interface TaskManagerCoordinator {
    var threadRunner: ThreadRunner
    val taskFilter: TaskProvider.TaskFilter

    fun canAddMoreTasks(): Boolean

    fun onTaskStartedLoading(pool: TaskPool, task: Task)
    fun onTaskFinishedLoading(pool: TaskPool, task: Task)
}