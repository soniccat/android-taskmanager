package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler
import android.os.Looper
import android.util.SparseArray

import androidx.collection.SparseArrayCompat

import com.example.alexeyglushkov.tools.HandlerTools

import java.lang.ref.WeakReference

/**
 * Created by alexeyglushkov on 23.08.15.
 */
class SimpleTaskManagerSnapshot : TaskManagerSnapshot, TaskManager.TaskManagerListener {
    private val callbackHandler: Handler

    //public

    override var loadingTasksCount: Int = 0
        private set
    override var waitingTasksCount: Int = 0
        private set
    override var maxQueueSize: Int = 0
        private set
    override var loadingLimits = SparseArrayCompat<Float>()
        private set
    override var usedLoadingSpace = SparseArrayCompat<Int>()
        private set
    override var waitingTaskInfo = SparseArrayCompat<Int>()
        private set

    private var needUpdateSnapshot: Boolean = false
    private val snapshotChangedListeners: WeakRefList<TaskManagerSnapshot.OnSnapshotChangedListener>

    init {
        callbackHandler = Handler(Looper.myLooper())
        snapshotChangedListeners = WeakRefList()
    }

    private fun bind(taskManager: TaskManager) {
        HandlerTools.runOnHandlerThread(taskManager.handler) {
            bindOnThread(taskManager)

            HandlerTools.runOnHandlerThread(callbackHandler) { triggerOnSnapshotListeners() }
        }
    }

    private fun bindOnThread(taskManager: TaskManager) {
        loadingTasksCount = taskManager.loadingTaskCount

        waitingTaskInfo = SparseArrayCompat()
        for (taskProvider in taskManager.taskProviders) {
            waitingTasksCount += taskProvider.taskCount

            for (task in taskProvider.tasks) {
                var count = waitingTaskInfo.get(task.taskType, 0)
                ++count
                waitingTaskInfo.put(task.taskType, count)
            }
        }

        maxQueueSize = taskManager.maxLoadingTasks
        loadingLimits = taskManager.limits
        usedLoadingSpace = taskManager.usedSpace

        taskManager.addListener(this)
    }

    /*
    public void setMaxQueueSize(int count) {
        maxQueueSize = count;
        triggerOnSnapshotListeners();
    }*/

    fun setLoadingLimit(taskType: Int, availableQueuePart: Float) {
        if (availableQueuePart == -1.0f) {
            loadingLimits.remove(taskType)
        } else {
            loadingLimits.put(taskType, availableQueuePart)
        }

        triggerOnSnapshotListeners()
    }

    private fun updateUsedLoadingSpace(taskType: Int, add: Boolean) {
        var count: Int = usedLoadingSpace.get(taskType, 0)
        if (add) {
            ++count
            ++loadingTasksCount
        } else {
            --count
            --loadingTasksCount

            if (count < 0) {
                count = 0
            }

            if (loadingTasksCount < 0) {
                loadingTasksCount = 0
            }
        }

        usedLoadingSpace.put(taskType, count)
        triggerOnSnapshotListeners()
    }

    private fun updateWaitingTaskInfo(taskType: Int, add: Boolean) {
        var count = waitingTaskInfo.get(taskType, 0)
        if (add) {
            ++waitingTasksCount
            ++count
        } else {
            --waitingTasksCount
            --count

            if (count < 0) {
                count = 0
            }

            if (waitingTasksCount < 0) {
                waitingTasksCount = 0
            }
        }

        waitingTaskInfo.put(taskType, count)
        triggerOnSnapshotListeners()
    }

    internal fun triggerOnSnapshotListeners() {
        for (listenerRef in snapshotChangedListeners) {
            listenerRef.get()?.onSnapshotChanged(this)
        }
    }

    override fun startSnapshotRecording(taskManager: TaskManager) {
        if (needUpdateSnapshot) {
            return
        }

        needUpdateSnapshot = true
        bind(taskManager)
    }

    override fun stopSnapshotRecording() {
        if (!needUpdateSnapshot) {
            return
        }

        needUpdateSnapshot = false
    }

    override fun addSnapshotListener(listener: TaskManagerSnapshot.OnSnapshotChangedListener) {
        snapshotChangedListeners.add(WeakReference(listener))
    }

    override fun removeSnapshotListener(listener: TaskManagerSnapshot.OnSnapshotChangedListener) {
        var i = 0
        for (listenerRef in snapshotChangedListeners) {
            if (listenerRef.get() != null) {
                snapshotChangedListeners.removeAt(i)
                break
            }

            ++i
        }
    }

    // TaskPool.Listener

    override fun onLimitsChanged(taskManager: TaskManager, taskType: Int, availableQueuePart: Float) {
        HandlerTools.runOnHandlerThread(callbackHandler) { setLoadingLimit(taskType, availableQueuePart) }
    }

    override fun onTaskAdded(pool: TaskPool, task: Task, isLoadingQueue: Boolean) {
        HandlerTools.runOnHandlerThread(callbackHandler) {
            if (isLoadingQueue) {
                updateUsedLoadingSpace(task.taskType, true)
            } else {
                updateWaitingTaskInfo(task.taskType, true)
            }
        }
    }

    override fun onTaskRemoved(pool: TaskPool, task: Task, isLoadingQueue: Boolean) {
        HandlerTools.runOnHandlerThread(callbackHandler) {
            if (isLoadingQueue) {
                updateUsedLoadingSpace(task.taskType, false)
            } else {
                updateWaitingTaskInfo(task.taskType, false)
            }
        }
    }
}
