package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.WorkerThread

import androidx.collection.SparseArrayCompat

import com.example.alexeyglushkov.tools.HandlerTools
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

import org.junit.Assert

import java.util.ArrayList
import java.util.Comparator

/**
 * Created by alexeyglushkov on 30.12.14.
 */

// A task source for TaskManager

// TODO: try to extend SimpleTaskPool to remove code duplications
open class PriorityTaskProvider(scope: CoroutineScope, override var taskProviderId: String) : TaskProvider, TaskPool {
    private var _scope: CoroutineScope?
    override var scope: CoroutineScope
        get() = _scope!!
        set(handler) {
            checkHandlerThread()
            _scope = handler
        }

    override var userData: Any? = null
    private val listeners: MutableList<TaskPool.Listener> = ArrayList()
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

    init {
        _scope = scope
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
    override fun getTopTask(typesToFilter: List<Int>?): Task? {
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
    override fun takeTopTask(typesToFilter: List<Int>?): Task? {
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

    override fun addTask(task: Task) {
        if (task !is TaskBase) { assert(false); return }

        if (!Tasks.isTaskReadyToStart(task)) {
            Log.e(TAG, "Can't put task " + task.javaClass.toString() + " because it's already started " + task.taskStatus.toString())
            return
        }

        // TaskProvider must set Waiting status on the current thread
        task.private.taskStatus = Task.Status.Waiting

        scope.launch {
            addTaskOnThread(task)
        }
    }

    override fun removeTask(task: Task) {
        scope.launch {
            removeTaskOnThread(task)
        }
    }

    override fun onTaskStatusChanged(task: Task, oldStatus: Task.Status, newStatus: Task.Status) {
        if (Tasks.isTaskCompleted(task)) {
            removeTask(task)
        }
    }

    @WorkerThread
    private fun addTaskOnThread(task: Task) {
        task.addTaskStatusListener(this@PriorityTaskProvider)
        addTaskToQueue(task)

        for (listener in listeners) {
            listener.onTaskAdded(this@PriorityTaskProvider, task)
        }
    }

    @WorkerThread
    private fun removeTaskOnThread(task: Task) {
        checkHandlerThread()

        if (removeTaskFromQueue(task)) {
            triggerOnTaskRemoved(task)
        }
    }

    @WorkerThread
    private fun triggerOnTaskRemoved(task: Task) {
        checkHandlerThread()

        for (listener in listeners) {
            listener.onTaskRemoved(this@PriorityTaskProvider, task)
        }
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

    override fun addListener(listener: TaskPool.Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: TaskPool.Listener) {
        listeners.remove(listener)
    }

    private fun checkHandlerThread() {
        //Assert.assertEquals(Looper.myLooper(), this.handler.looper)
    }

    companion object {
        internal val TAG = "PriorityTaskProvider"
    }
}
