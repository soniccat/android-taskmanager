package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler

/**
 * Created by alexeyglushkov on 30.12.14.
 */

// A storage for tasks you use in a fragment or in an activity
// A pool must remove task when it finishes

interface TaskPool : Task.StatusListener {
    var handler: Handler
    fun getTaskCount(): Int
    val tasks: List<Task>
    var userData: Any?

    fun addTask(task: Task)
    fun removeTask(task: Task)

    fun getTask(taskId: String): Task?

    fun addListener(listener: TaskPoolListener)
    fun removeListener(listener: TaskPoolListener)

    interface TaskPoolListener {
        fun onTaskAdded(pool: TaskPool, task: Task)
        fun onTaskRemoved(pool: TaskPool, task: Task)
    }
}
