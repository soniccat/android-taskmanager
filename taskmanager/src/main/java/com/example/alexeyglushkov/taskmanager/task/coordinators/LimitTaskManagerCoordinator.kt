package com.example.alexeyglushkov.taskmanager.task.coordinators

import androidx.annotation.WorkerThread
import androidx.collection.SparseArrayCompat
import com.example.alexeyglushkov.taskmanager.task.*
import org.junit.Assert
import java.lang.ref.WeakReference
import java.util.ArrayList

class LimitTaskManagerCoordinator(maxLoadingTasks: Int): TaskManagerCoordinator {
    var listeners = WeakRefList<Listener>()
    lateinit var threadRunner: ThreadRunner

    private var _limits = SparseArrayCompat<Float>()
    var limits: SparseArrayCompat<Float>
        @WorkerThread
        get() {
            return threadRunner.run {
                _limits.clone()
            }
        }
        set(value) {
            threadRunner.run {
                _limits = value
            }
        }

    private var _usedSpace = SparseArrayCompat<Int>()
    var usedSpace //type -> task count from loadingTasks
        get() = _usedSpace.clone()
        set(value) {
            _usedSpace = value.clone()
        }

    var loadingTaskCount: Int = 0
    var maxLoadingTasks: Int = 0
        get() {
            return field
        }
        set(value) {
            field = value
            // TODO: handle somehow on the thread
            // TODO: we need run tasks when increase the size
            // TODO: probably we should cancel tasks after decreasing
        }

    val taskFilter = object : TaskProvider.TaskFilter {
        override fun getFilteredTaskTypes(): List<Int> {
            return this@LimitTaskManagerCoordinator.getFilteredTaskTypes()
        }
    }

    init {
        this.maxLoadingTasks = maxLoadingTasks
    }

    private fun reachedLimit(taskType: Int): Boolean {
        return if (_limits.get(taskType, -1.0f) == -1.0f) {
            false
        } else _usedSpace.get(taskType, 0).toFloat() / maxLoadingTasks.toFloat() >= _limits.get(taskType, 0.0f)
    }

    override fun onTaskProviderAdded(taskProvider: TaskProvider) {
        taskProvider.taskFilter = taskFilter
    }

    override fun onTaskProviderRemoved(taskProvider: TaskProvider) {
        taskProvider.taskFilter = null
    }

    override fun canAddMoreTasks(): Boolean {
        return loadingTaskCount < maxLoadingTasks;
    }

    override fun onTaskStartedLoading(pool: TaskPool, task: Task) {
        loadingTaskCount += 1
        updateUsedSpace(task.taskType, true)
    }

    override fun onTaskFinishedLoading(pool: TaskPool, task: Task) {
        loadingTaskCount -= 1
        updateUsedSpace(task.taskType, false)
    }

    private fun updateUsedSpace(taskType: Int, add: Boolean) {
        var count: Int = _usedSpace.get(taskType, 0)
        if (add) {
            ++count
        } else {
            Assert.assertTrue(count > 0)
            --count
        }

        _usedSpace.put(taskType, count)
    }

    fun setLimit(taskType: Int, availableQueuePart: Float) {
        threadRunner.launch {
            if (availableQueuePart <= 0.0f) {
                _limits.remove(taskType)
            } else {
                _limits.put(taskType, availableQueuePart)
            }

            for (listener in listeners) {
                listener.get()?.onLimitsChanged(this, taskType, availableQueuePart)
            }
        }
    }

    private fun getFilteredTaskTypes(): List<Int> {
        val taskTypesToFilter = ArrayList<Int>()
        for (i in 0 until _limits.size()) {
            if (reachedLimit(_limits.keyAt(i))) {
                taskTypesToFilter.add(_limits.keyAt(i))
            }
        }
        return taskTypesToFilter
    }

    // Listeners
    fun removeListener(listener: Listener) {
        listeners.removeValue(listener)
    }
    fun addListener(listener: Listener) {
        listeners.add(WeakReference(listener))
    }

    interface Listener {
        fun onLimitsChanged(coordinator: LimitTaskManagerCoordinator, taskType: Int, availableQueuePart: Float)
    }
}