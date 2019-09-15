package com.example.alexeyglushkov.taskmanager.task

interface TaskManagerCoordinator {
    fun canAddTask(task: Task): Boolean
    fun onTaskAdded(pool: TaskPool, task: Task)
    fun onTaskRemoved(pool: TaskPool, task: Task)
}