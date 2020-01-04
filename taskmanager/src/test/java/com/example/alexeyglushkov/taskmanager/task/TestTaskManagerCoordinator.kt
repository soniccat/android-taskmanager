package com.example.alexeyglushkov.taskmanager.task

import com.example.alexeyglushkov.taskmanager.coordinators.TaskManagerCoordinator
import com.example.alexeyglushkov.taskmanager.pool.TaskPool
import com.example.alexeyglushkov.taskmanager.providers.TaskProvider
import com.example.alexeyglushkov.taskmanager.runners.InstantThreadRunner
import com.example.alexeyglushkov.taskmanager.runners.ThreadRunner

open class TestTaskManagerCoordinator(var canAddMoreTasks: Boolean = false): TaskManagerCoordinator {
    override var threadRunner: ThreadRunner = InstantThreadRunner()

    override val taskFilter: TaskProvider.TaskFilter = object : TaskProvider.TaskFilter {
        override fun getFilteredTaskTypes(): List<Int> {
            return emptyList()
        }
    }

    override fun canAddMoreTasks(): Boolean {
        return canAddMoreTasks
    }

    override fun onTaskStartedLoading(pool: TaskPool, task: Task) {
    }

    override fun onTaskFinishedLoading(pool: TaskPool, task: Task) {
    }
}