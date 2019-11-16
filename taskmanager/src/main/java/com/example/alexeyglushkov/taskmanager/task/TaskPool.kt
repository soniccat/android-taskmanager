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
    var threadRunner: ThreadRunner // TODO: hide scope by an abstract Object content
    var userData: Any?

    fun addTask(task: Task)
    fun removeTask(task: Task)
    fun cancelTask(task: Task, info: Any?) // TODO: think about moving to TaskProvider

    @WorkerThread fun getTask(taskId: String): Task?
    @WorkerThread fun getTaskCount(): Int
    @WorkerThread fun getTasks(): List<Task>

    fun addListener(listener: Listener) // TODO: replace to setListener...
    fun removeListener(listener: Listener)

    interface Listener {
        @WorkerThread fun onTaskConflict(pool: TaskPool, newTask: Task, oldTask: Task): Task
        @WorkerThread fun onTaskAdded(pool: TaskPool, task: Task)
        @WorkerThread fun onTaskRemoved(pool: TaskPool, task: Task)
        @WorkerThread fun onTaskCancelled(pool: TaskPool, task: Task, info: Any?)
    }
}
