package com.example.alexeyglushkov.taskmanager.task

import androidx.annotation.WorkerThread

/**
 * Created by alexeyglushkov on 13.08.16.
 */
open class StackTaskProvider(private val areTasksDependent: Boolean, //if enabled the top task blocks the next task until the former finishes
                             override var taskProviderId: String,
                             threadRunner: ThreadRunner): ListTaskPool(threadRunner), TaskProvider, Task.StatusListener {
    override var priority: Int = 0
    private var isBlocked: Boolean = false
    override var taskFilter: TaskProvider.TaskFilter? = null

    override fun triggerOnTaskAdded(task: Task) {
        if (!isBlocked) {
            super.triggerOnTaskAdded(task)
        }
    }

    @WorkerThread
    override fun getTopTask(): Task? {
        var result: Task? = null
        if (canTakeTask()) {
            val index = getTopTaskIndex()
            if (index != -1) {
                result = _tasks[index]
            }
        }

        return result
    }

    @WorkerThread
    override fun takeTopTask(): Task? {
        var result: Task? = null
        if (canTakeTask()) {
            val index = getTopTaskIndex()
            result = _tasks[index]
            _tasks.removeAt(index)
            onTaskTaken(result)
        }

        return result
    }

    @WorkerThread
    private fun onTaskTaken(task: Task) {
        if (areTasksDependent) {
            isBlocked = true

            task.addTaskStatusListener(object : Task.StatusListener {
                override fun onTaskStatusChanged(task: Task, oldStatus: Task.Status, newStatus: Task.Status) {
                    if (Tasks.isTaskFinished(task)) {
                        isBlocked = false

                        if (getTaskCount() > 0) {
                            // TODO: trigger for the next tasks until not blocked
                            triggerOnTaskAdded(_tasks[0])
                        }
                    }
                }
            })
        }

        onTaskRemoved(task)
    }

    private fun canTakeTask(): Boolean {
        return !areTasksDependent || !isBlocked
    }

    private fun getTopTaskIndex(): Int {
        var result = -1
        for (i in 0 until _tasks.size) {
            val task = _tasks[i]
            val taskFilter = taskFilter
            val isFiltered = taskFilter != null && taskFilter.isFiltered(task)

            if (!task.isBlocked() && !isFiltered) {
                result = i
                break
            }
        }

        return result
    }

    override fun tag() = "StackTaskProvider"
}
