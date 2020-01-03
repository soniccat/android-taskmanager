package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler
import android.os.Looper

import androidx.collection.SparseArrayCompat
import com.example.alexeyglushkov.taskmanager.task.coordinators.LimitTaskManagerCoordinator

import com.example.alexeyglushkov.tools.HandlerTools
import kotlinx.coroutines.launch

import java.lang.ref.WeakReference

/**
 * Created by alexeyglushkov on 23.08.15.
 */
class SimpleTaskManagerSnapshot : TaskManagerSnapshot, TaskManager.Listener {
    private val callbackHandler: Handler

    // public

    override var loadingTasksCount: Int = 0
        private set
    override var waitingTasksCount: Int = 0
        private set
    override var blockedTasksCount: Int = 0
        private set
    override var maxQueueSize: Int = 0
        private set
    override var loadingLimits = SparseArrayCompat<Float>()
        private set
    override var usedLoadingSpace = SparseArrayCompat<Int>()
        private set
    override var waitingTaskInfo = SparseArrayCompat<Int>()
        private set
    override var blockedTaskInfo = SparseArrayCompat<Int>()
        private set

    private var needUpdateSnapshot: Boolean = false
    private val snapshotChangedListeners: WeakRefList<TaskManagerSnapshot.OnSnapshotChangedListener>

    init {
        callbackHandler = Handler(Looper.myLooper())
        snapshotChangedListeners = WeakRefList()
    }

    private fun bind(taskManager: TaskManager) {
        taskManager.threadRunner.launch {
            bindOnThread(taskManager)
            HandlerTools.runOnHandlerThread(callbackHandler) { triggerOnSnapshotListeners() }
        }
    }

    private fun bindOnThread(taskManager: TaskManager) {
        val coordinator: LimitTaskManagerCoordinator

        val aCoordinator = taskManager.taskManagerCoordinator
        if (aCoordinator is LimitTaskManagerCoordinator) {
            coordinator = aCoordinator
        } else {
            throw IllegalArgumentException("LimitTaskManagerCoordinator is expected in taskManager.taskManagerCoordinator")
        }

        loadingTasksCount = taskManager.getLoadingTaskCount()

        waitingTaskInfo = SparseArrayCompat()
        blockedTaskInfo = SparseArrayCompat()

        for (taskProvider in taskManager.taskProviders) {
            for (task in taskProvider.getTasks()) {
                if (task.taskStatus == Task.Status.Blocked) {
                    blockedTasksCount += 1
                    val blockedCount = blockedTaskInfo.get(task.taskType, 0)
                    blockedTaskInfo.put(task.taskType, blockedCount + 1)
                } else {
                    waitingTasksCount += 1
                    val count = waitingTaskInfo.get(task.taskType, 0)
                    waitingTaskInfo.put(task.taskType, count + 1)
                }
            }
        }

        maxQueueSize = coordinator.maxLoadingTasks
        loadingLimits = coordinator.limits
        usedLoadingSpace = coordinator.usedSpace

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

    private fun updateBlockingTaskInfo(taskType: Int, add: Boolean) {
        var count = blockedTaskInfo.get(taskType, 0)
        if (add) {
            ++blockedTasksCount
            ++count
        } else {
            --blockedTasksCount
            --count

            if (count < 0) {
                count = 0
            }

            if (blockedTasksCount < 0) {
                blockedTasksCount = 0
            }
        }

        blockedTaskInfo.put(taskType, count)
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

    override fun onTaskStatusChanged(task: Task, oldStatus: Task.Status) {
        HandlerTools.runOnHandlerThread(callbackHandler) {
            if (task.taskStatus == Task.Status.Blocked) {
                updateBlockingTaskInfo(task.taskType, true)
                if (oldStatus == Task.Status.Waiting) {
                    updateWaitingTaskInfo(task.taskType, false)
                }

            } else if (oldStatus == Task.Status.Blocked) {
                updateBlockingTaskInfo(task.taskType, false)
                if (oldStatus == Task.Status.Waiting) {
                    updateWaitingTaskInfo(task.taskType, true)
                }
            }
        }
    }
}
