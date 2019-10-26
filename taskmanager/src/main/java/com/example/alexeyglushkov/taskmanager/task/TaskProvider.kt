package com.example.alexeyglushkov.taskmanager.task

import androidx.annotation.WorkerThread
import java.util.*

/**
 * Created by alexeyglushkov on 30.12.14.
 */

// A provider defines in which order tasks should be executed
// A provider must remove a task from the pool in the takeTopTask method

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
