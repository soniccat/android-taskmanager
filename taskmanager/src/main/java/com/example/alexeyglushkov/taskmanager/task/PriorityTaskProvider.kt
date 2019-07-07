package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.SparseArray

import androidx.collection.SparseArrayCompat

import com.example.alexeyglushkov.tools.HandlerTools

import org.junit.Assert

import java.util.ArrayList
import java.util.Comparator

/**
 * Created by alexeyglushkov on 30.12.14.
 */

// A task source for TaskManager

// TODO: try to extend SimpleTaskPool to remove code duplications
open class PriorityTaskProvider(handler: Handler, override var taskProviderId: String?) : TaskProvider, TaskPool {
    override var handler: Handler? = null
        set(handler) {
            if (this.handler != null) {
                checkHandlerThread()
            }

            field = handler
        }
    override var userData: Any? = null
    private val listeners: MutableList<TaskPool.TaskPoolListener>
    override var priority: Int = 0

    // Task type -> priority queue
    // TODO: try to use PriorityBlockingQueue
    private val taskQueues: SparseArrayCompat<SortedList<Task>>

    override val taskCount: Int
        get() {
            checkHandlerThread()

            var resultCount = 0
            for (i in 0 until taskQueues.size()) {
                val queue = taskQueues.get(taskQueues.keyAt(i))
                resultCount += queue!!.size
            }

            return resultCount
        }

    override val tasks: List<Task>
        get() {
            checkHandlerThread()

            val tasks = ArrayList<Task>()

            for (i in 0 until taskQueues.size()) {
                val queue = taskQueues.get(taskQueues.keyAt(i))
                tasks.addAll(queue!!)
            }

            return tasks
        }

    init {
        taskQueues = SparseArrayCompat()
        listeners = ArrayList()
        handler = handler
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

    override fun getTopTask(typesToFilter: List<Int>?): Task? {
        checkHandlerThread()

        var topTask: Task? = null
        var topPriority = -1

        for (i in 0 until taskQueues.size()) {
            if (typesToFilter == null || !typesToFilter.contains(taskQueues.keyAt(i))) {
                val queue = taskQueues.get(taskQueues.keyAt(i))
                val queueTask = getTopTask(queue!!, false)

                if (queueTask != null && queueTask.taskPriority > topPriority) {
                    topTask = queueTask
                    topPriority = topTask.taskPriority
                }
            }
        }

        return topTask
    }

    override fun takeTopTask(typesToFilter: List<Int>): Task? {
        checkHandlerThread()

        val task = getTopTask(typesToFilter)
        if (task != null) {
            val queue = taskQueues.get(task.taskType)
            val polledTask = getTopTask(queue!!, true)
            triggerOnTaskRemoved(polledTask)
        }

        return task
    }

    private fun getTopTask(queue: SortedList<Task>, needPoll: Boolean): Task? {
        var topTask: Task? = null

        val iterator = queue.iterator()
        while (iterator.hasNext()) {
            topTask = iterator.next()
            if (!topTask!!.isBlocked) {
                if (needPoll) {
                    iterator.remove()
                }
                break
            }
        }

        return topTask
    }

    fun updatePriorities(provider: PriorityProvider) {
        HandlerTools.runOnHandlerThread(this.handler!!) {
            for (i in 0 until taskQueues.size()) {
                val queue = taskQueues.valueAt(i)
                for (t in taskQueues.get(taskQueues.keyAt(i))!!) {
                    t.taskPriority = provider.getPriority(t)
                }

                queue.updateSortedOrder()
            }
        }
    }

    override fun addTask(task: Task) {
        if (!Tasks.isTaskReadyToStart(task)) {
            Log.e(TAG, "Can't put task " + task.javaClass.toString() + " because it's already started " + task.taskStatus.toString())
            return
        }

        // TaskProvider must set Waiting status on the current thread
        task.private.taskStatus = Task.Status.Waiting

        HandlerTools.runOnHandlerThread(this.handler!!) { addTaskOnThread(task) }
    }

    override fun removeTask(task: Task) {
        HandlerTools.runOnHandlerThread(this.handler!!) { removeTaskOnThread(task) }
    }

    override fun onTaskStatusChanged(task: Task, oldStatus: Task.Status, newStatus: Task.Status) {
        if (Tasks.isTaskCompleted(task)) {
            removeTask(task)
        }
    }

    private fun addTaskOnThread(task: Task) {
        task.addTaskStatusListener(this@PriorityTaskProvider)
        addTaskToQueue(task)

        for (listener in listeners) {
            listener.onTaskAdded(this@PriorityTaskProvider, task)
        }
    }

    private fun removeTaskOnThread(task: Task) {
        checkHandlerThread()

        if (removeTaskFromQueue(task)) {
            triggerOnTaskRemoved(task)
        }
    }

    private fun triggerOnTaskRemoved(task: Task?) {
        checkHandlerThread()

        for (listener in listeners) {
            listener.onTaskRemoved(this@PriorityTaskProvider, task)
        }
    }

    private fun addTaskToQueue(task: Task) {
        checkHandlerThread()

        var queue = taskQueues.get(task.taskType)
        if (queue == null) {
            queue = createQueue()
            taskQueues.put(task.taskType, queue)
        }

        queue.addInSortedOrder(task)
    }

    private fun removeTaskFromQueue(task: Task): Boolean {
        checkHandlerThread()

        val queue = taskQueues.get(task.taskType)
        return queue?.remove(task) ?: false

    }

    interface PriorityProvider {
        fun getPriority(task: Task): Int
    }

    override fun getTask(taskId: String): Task? {
        checkHandlerThread()

        var resultTask: Task? = null
        var i = 0
        while (i < taskQueues.size() && resultTask == null) {
            val queue = taskQueues.get(taskQueues.keyAt(i))
            val iterator = queue!!.iterator()

            while (iterator.hasNext()) {
                val task = iterator.next()
                if (task.taskId != null && task.taskId == taskId) {
                    resultTask = task
                    break
                }
            }
            ++i
        }

        return resultTask
    }

    override fun addListener(listener: TaskPool.TaskPoolListener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: TaskPool.TaskPoolListener) {
        listeners.remove(listener)
    }

    private fun checkHandlerThread() {
        Assert.assertEquals(Looper.myLooper(), this.handler!!.looper)
    }

    companion object {
        internal val TAG = "PriorityTaskProvider"
    }
}
