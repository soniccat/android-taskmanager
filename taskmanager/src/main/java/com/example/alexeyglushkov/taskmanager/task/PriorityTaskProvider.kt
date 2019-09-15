package com.example.alexeyglushkov.taskmanager.task

import androidx.annotation.WorkerThread

import androidx.collection.SparseArrayCompat

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

import java.util.ArrayList
import java.util.Comparator

/**
 * Created by alexeyglushkov on 30.12.14.
 */

// A task source for TaskManager

open class PriorityTaskProvider(scope: CoroutineScope, override var taskProviderId: String): TaskPoolBase(scope), TaskProvider, Task.StatusListener {
    override var priority: Int = 0

    // Task type -> priority queue
    // TODO: try to use PriorityBlockingQueue
    private val taskQueues: SparseArrayCompat<SortedList<Task>> = SparseArrayCompat()

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
        checkHandlerThread()

        var topTask: Task? = null
        var topPriority = -1

        for (i in 0 until taskQueues.size()) {
            if (typesToFilter == null || !typesToFilter.contains(taskQueues.keyAt(i))) {
                val queue = taskQueues.get(taskQueues.keyAt(i))
                if (queue != null) {
                    val queueTask = getTopTask(queue, false)
                    if (queueTask != null && queueTask.taskPriority > topPriority) {
                        topTask = queueTask
                        topPriority = topTask.taskPriority
                    }
                }
            }
        }

        return topTask
    }

    @WorkerThread
    override fun takeTopTask(): Task? {
        checkHandlerThread()

        val task = getTopTask(typesToFilter)
        if (task != null) {
            val queue = taskQueues.get(task.taskType)
            queue?.let {
                val polledTask = getTopTask(it, true)
                if (polledTask != null) {
                    triggerOnTaskRemoved(polledTask)
                }
            }
        }

        return task
    }

    @WorkerThread
    private fun getTopTask(queue: SortedList<Task>, needPoll: Boolean): Task? {
        var topTask: Task? = null

        val iterator = queue.iterator()
        while (iterator.hasNext()) {
            topTask = iterator.next()
            if (!topTask.isBlocked()) {
                if (needPoll) {
                    iterator.remove()
                }
                break
            }
        }

        return topTask
    }

    fun updatePriorities(provider: PriorityProvider) {
        scope.launch {
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

    class TaskIterator(val taskQueues: SparseArrayCompat<SortedList<Task>>,
                       override val skipBlocked: Boolean = true,
                       override val skipTaskTypes: List<Int> = ArrayList()): TaskProvider.TaskIterator {
        private var queueIndex = 0
        private var taskIndex = 0

        override fun nextTask(): Task? {
            while (queueIndex < taskQueues.size()) {
                val queueKey = taskQueues.keyAt(queueIndex)
                if (!skipTaskTypes.contains(queueKey)) {
                    val queue = taskQueues.get(taskQueues.keyAt(queueIndex))!!
                    while (taskIndex < queue.size) {
                        val task = queue[taskIndex]
                        taskIndex += 1

                        if (!skipBlocked || !task.isBlocked()) {
                            return task;
                        }
                    }
                }

                taskIndex = 0
                queueIndex += 1
            }

            // reset
            taskIndex = 0
            queueIndex = 0

            return null
        }
    }

    override fun getIterator(skipBlocked: Boolean, skipTaskTypes: List<Int>): TaskProvider.TaskIterator {
        return TaskIterator(taskQueues, skipBlocked, skipTaskTypes)
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
