package com.example.alexeyglushkov.taskmanager.task

import androidx.annotation.WorkerThread
import kotlinx.coroutines.CoroutineScope

open class ListTaskPool(scope: CoroutineScope): TaskPoolBase(scope) {
    //TODO: think about a map
    protected val _tasks = mutableListOf<Task>()

    @WorkerThread
    override fun addTaskInternal(task: Task) {
        _tasks.add(task)
    }

    @WorkerThread
    override fun removeTaskInternal(task: Task): Boolean {
        return _tasks.remove(task)
    }

    //// Getters

    @WorkerThread
    override fun getTaskCount(): Int {
        checkHandlerThread()
        return _tasks.size
    }

    @WorkerThread
    override fun getTasks(): List<Task> {
        checkHandlerThread()
        return _tasks
    }

    @WorkerThread
    override fun getTask(taskId: String): Task? {
        checkHandlerThread()

        for (task in _tasks) {
            if (task.taskId != null && task.taskId == taskId) {
                return task
            }
        }

        return null
    }
}