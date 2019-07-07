package com.example.alexeyglushkov.taskmanager.task

/**
 * Created by alexeyglushkov on 08.02.15.
 */
interface TaskExecutor {
    fun executeTask(task: Task, callback: Task.Callback)
}
