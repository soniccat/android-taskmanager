package com.example.alexeyglushkov.taskmanager.task

open class TestTaskManagerCoordinator: TaskManagerCoordinator {
    override fun canAddMoreTasks(): Boolean {
        return false
    }

    override fun onTaskProviderAdded(taskProvider: TaskProvider) {
    }

    override fun onTaskProviderRemoved(taskProvider: TaskProvider) {
    }

    override fun onTaskStartedLoading(pool: TaskPool, task: Task) {
    }

    override fun onTaskFinishedLoading(pool: TaskPool, task: Task) {
    }
}