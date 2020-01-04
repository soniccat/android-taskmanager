package com.example.alexeyglushkov.taskmanager.pool

import androidx.annotation.WorkerThread
import com.example.alexeyglushkov.taskmanager.task.Task
import com.example.alexeyglushkov.taskmanager.runners.ThreadRunner

open class ListTaskPool(threadRunner: ThreadRunner): TaskPoolBase(threadRunner) {
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