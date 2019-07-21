package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler
import androidx.annotation.WorkerThread
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

/**
 * Created by alexeyglushkov on 30.12.14.
 */

// A storage for tasks you use in a fragment or in an activity
// A pool must remove task when it finishes

interface TaskPool : Task.StatusListener {
    var scope: CoroutineScope
    var userData: Any?

    fun addTask(task: Task)
    fun removeTask(task: Task)

    @WorkerThread fun getTask(taskId: String): Task?
    @WorkerThread fun getTaskCount(): Int
    @WorkerThread fun getTasks(): List<Task>

    fun addListener(listener: Listener)
    fun removeListener(listener: Listener)

    interface Listener {
        fun onTaskAdded(pool: TaskPool, task: Task)
        fun onTaskRemoved(pool: TaskPool, task: Task)
    }
}
