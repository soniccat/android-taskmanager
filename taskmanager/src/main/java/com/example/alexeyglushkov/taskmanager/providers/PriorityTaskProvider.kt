package com.example.alexeyglushkov.taskmanager.providers

import androidx.annotation.WorkerThread

import androidx.collection.SparseArrayCompat
import com.example.alexeyglushkov.taskmanager.task.*
import com.example.alexeyglushkov.taskmanager.pool.TaskPoolBase
import com.example.alexeyglushkov.taskmanager.tools.SortedList
import com.example.alexeyglushkov.taskmanager.runners.ThreadRunner

import java.util.ArrayList
import java.util.Comparator

/**
 * Created by alexeyglushkov on 30.12.14.
 */

// A task source for TaskManager

open class PriorityTaskProvider(threadRunner: ThreadRunner, override var taskProviderId: String): TaskPoolBase(threadRunner), TaskProvider, Task.StatusListener {
    override var priority: Int = 0
    override var taskFilter: TaskProvider.TaskFilter? = null

    // Task type -> priority queue
    // TODO: try to use PriorityBlockingQueue
    private val taskQueues: SparseArrayCompat<SortedList<Task>> = SparseArrayCompat()

    override fun addTask(task: Task) {
        if (TaskProviders.addTaskCheck(task as TaskBase, tag())) {
            super.addTask(task)
        }
    }

    @WorkerThread
    override fun getTaskCount(): Int {
        checkHandlerThread()

        var resultCount = 0
        for (i in 0 until taskQueues.size()) {
            val queue = taskQueues.get(taskQueues.keyAt(i))
            resultCount += queue?.size ?: 0
        }

        return resultCount
    }

    @WorkerThread
    override fun getTasks(): List<Task> {
        checkHandlerThread()

        val tasks = ArrayList<Task>()
        for (i in 0 until taskQueues.size()) {
            val queue = taskQueues.get(taskQueues.keyAt(i))
            if (queue != null) {
                tasks.addAll(queue)
            }
        }

        return tasks
    }

    private fun createQueue(): SortedList<Task> {
        return SortedList(Comparator { lhs, rhs ->
            if (lhs.taskPriority == rhs.taskPriority) {
                return@Comparator 0
            }

            if (lhs.taskPriority > rhs.taskPriority) {
                -1
            } else 1
        })
    }

    @WorkerThread
    override fun getTopTask(): Task? {
        return getTopTask(false)
    }

    private fun getTopTask(needPoll: Boolean): Task? {
        checkHandlerThread()

        var topTask: Task? = null
        var topPriority = -1
        var topQueue: SortedList<Task>? = null
        val taskFilter = taskFilter

        for (i in 0 until taskQueues.size()) {
            if (taskFilter == null || !taskFilter.getFilteredTaskTypes().contains(taskQueues.keyAt(i))) {
                val queue = taskQueues.get(taskQueues.keyAt(i))
                if (queue != null) {
                    val queueTask = getTopTask(queue, false)
                    if (queueTask != null && queueTask.taskPriority > topPriority) {
                        topQueue = queue
                        topTask = queueTask
                        topPriority = topTask.taskPriority
                    }
                }
            }
        }

        if (needPoll && topQueue != null) {
            val resultTopTask = getTopTask(topQueue, true) // do poll
            assert(resultTopTask == topTask)
        }

        return topTask
    }

    @WorkerThread
    override fun takeTopTask(): Task? {
        checkHandlerThread()

        val task = getTopTask(true)
        if (task != null) {
            onTaskRemoved(task)
        }

        return task
    }

    @WorkerThread
    private fun getTopTask(queue: SortedList<Task>, needPoll: Boolean): Task? {
        var topTask: Task? = null
        val iterator = queue.iterator()
        while (iterator.hasNext()) {
            val aTask = iterator.next()
            val taskFilter = taskFilter
            val isFiltered = taskFilter != null && taskFilter.isFiltered(aTask)

            if (!isFiltered && !aTask.isBlocked()) {
                topTask = aTask
                if (needPoll) {
                    iterator.remove()
                }
                break
            }
        }

        return topTask
    }

    fun updatePriorities(provider: PriorityProvider) {
        threadRunner.launch {
            for (i in 0 until taskQueues.size()) {
                val queue = taskQueues.valueAt(i)
                val tasks = taskQueues.get(taskQueues.keyAt(i))
                if (tasks != null) {
                    for (t in tasks) {
                        t.taskPriority = provider.getPriority(t)
                    }
                }

                queue.updateSortedOrder()
            }
        }
    }

    @WorkerThread
    override fun addTaskInternal(task: Task) {
        addTaskToQueue(task)
    }

    @WorkerThread
    override fun removeTaskInternal(task: Task): Boolean {
        return removeTaskFromQueue(task)
    }

    @WorkerThread
    private fun addTaskToQueue(task: Task) {
        checkHandlerThread()

        var queue = taskQueues.get(task.taskType)
        if (queue == null) {
            queue = createQueue()
            taskQueues.put(task.taskType, queue)
        }

        queue.addInSortedOrder(task)
    }

    @WorkerThread
    private fun removeTaskFromQueue(task: Task): Boolean {
        checkHandlerThread()

        val queue = taskQueues.get(task.taskType)
        return queue?.remove(task) ?: false

    }

    interface PriorityProvider {
        fun getPriority(task: Task): Int
    }

    @WorkerThread
    override fun getTask(taskId: String): Task? {
        checkHandlerThread()

        var resultTask: Task? = null
        var i = 0
        while (i < taskQueues.size() && resultTask == null) {
            val queue = taskQueues.get(taskQueues.keyAt(i))
            queue?.let {
                val iterator = queue.iterator()
                while (iterator.hasNext()) {
                    val task = iterator.next()
                    if (task.taskId != null && task.taskId == taskId) {
                        resultTask = task
                        break
                    }
                }
            }

            ++i
        }

        return resultTask
    }

    override fun tag() = "PriorityTaskProvider"
}
