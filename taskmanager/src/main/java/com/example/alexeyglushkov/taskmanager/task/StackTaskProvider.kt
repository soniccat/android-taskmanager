package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler

/**
 * Created by alexeyglushkov on 13.08.16.
 */
class StackTaskProvider(private val areTasksDependent: Boolean //if enabled the top task blocks the next task until the former finishes
                        , handler: Handler, override var taskProviderId: String?) : SimpleTaskPool(handler), TaskProvider, Task.StatusListener {
    override var priority: Int = 0
    private var isBlocked: Boolean = false

    override fun triggerOnTaskAdded(task: Task) {
        if (!isBlocked) {
            super.triggerOnTaskAdded(task)
        }
    }

    override fun getTopTask(typesToFilter: List<Int>): Task? {
        var result: Task? = null
        if (canTakeTask()) {
            val index = getTopTaskIndex(typesToFilter)
            if (index != -1) {
                result = tasks[index]
            }
        }

        return result
    }

    override fun takeTopTask(typesToFilter: List<Int>): Task? {
        var result: Task? = null
        if (canTakeTask()) {
            val index = getTopTaskIndex(typesToFilter)
            result = tasks[index]
            tasks.removeAt(index)
            onTaskTaken(result)
        }

        return result
    }

    private fun onTaskTaken(task: Task?) {
        if (areTasksDependent) {
            isBlocked = true

            task!!.addTaskStatusListener { task, oldStatus, newStatus ->
                if (Tasks.isTaskCompleted(task)) {
                    isBlocked = false

                    if (taskCount > 0) {
                        // TODO: trigger for the next tasks until not blocked
                        triggerOnTaskAdded(tasks[0])
                    }
                }
            }
        }

        triggerOnTaskRemoved(task)
    }

    private fun triggerOnTaskRemoved(task: Task) {
        checkHandlerThread()

        for (listener in listeners) {
            listener.onTaskRemoved(this@StackTaskProvider, task)
        }
    }

    private fun canTakeTask(): Boolean {
        return !areTasksDependent || !isBlocked
    }

    private fun getTopTaskIndex(typesToFilter: List<Int>?): Int {
        var result = -1
        for (i in 0 until tasks.size) {
            val task = tasks[i]

            val passFilter = typesToFilter == null || !typesToFilter.contains(task.taskType)
            if (!task.isBlocked && passFilter) {
                result = i
                break
            }
        }

        return result
    }
}
