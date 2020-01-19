package com.example.alexeyglushkov.taskmanager.providers

import androidx.annotation.WorkerThread
import com.example.alexeyglushkov.taskmanager.task.Task
import com.example.alexeyglushkov.taskmanager.pool.TaskPool

/**
 * Created by alexeyglushkov on 30.12.14.
 */

// Obligation: a provider defines in which order tasks should be executed
// Obligation: a provider must remove a task from the pool in the takeTopTask method
// Restriction: a provider ignores a task in addTask if the task is not ready to start
// Obligation: a provider changes the status of a task to Task.Status.Waiting in addTask

interface TaskProvider : TaskPool {
    // TODO: maybe delete it and force to pass in a constructor
    var taskProviderId: String
    // TODO: implement it
    // priority is used to determine the order of accessing to the providers int a TaskManager
    // it affects tasks order if the tasks have the same priority
    var priority: Int
    var taskFilter: TaskFilter?

    @WorkerThread fun getTopTask(): Task?
    @WorkerThread fun takeTopTask(): Task?

    interface TaskFilter {
        fun getFilteredTaskTypes(): List<Int>
        fun isFiltered(task: Task): Boolean {
            return getFilteredTaskTypes().contains(task.taskType)
        }
    }
}
