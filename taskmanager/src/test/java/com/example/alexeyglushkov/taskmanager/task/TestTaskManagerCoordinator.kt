package com.example.alexeyglushkov.taskmanager.task

open class TestTaskManagerCoordinator: TaskManagerCoordinator {
    override val taskFilter: TaskProvider.TaskFilter = object : TaskProvider.TaskFilter {
        override fun getFilteredTaskTypes(): List<Int> {
            return emptyList()
        }
    }

    override fun canAddMoreTasks(): Boolean {
        return false
    }

    override fun onTaskStartedLoading(pool: TaskPool, task: Task) {
    }

    override fun onTaskFinishedLoading(pool: TaskPool, task: Task) {
    }
}