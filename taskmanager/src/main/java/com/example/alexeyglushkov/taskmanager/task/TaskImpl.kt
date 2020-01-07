package com.example.alexeyglushkov.taskmanager.task

import android.os.Looper
import android.util.Log
import androidx.annotation.WorkerThread

import com.example.alexeyglushkov.streamlib.progress.ProgressInfo
import com.example.alexeyglushkov.streamlib.progress.ProgressListener
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater
import com.example.alexeyglushkov.taskmanager.tools.WeakRefList
import com.example.alexeyglushkov.tools.HandlerTools

import org.junit.Assert
import java.lang.Exception

import java.lang.ref.WeakReference
import java.util.Date

/**
 * Created by alexeyglushkov on 23.07.15.
 */
abstract class TaskImpl : TaskBase, TaskPrivate {
    // Input
    final override var taskId: String? = null
    final override var loadPolicy: Task.LoadPolicy = Task.LoadPolicy.SkipIfAlreadyAdded
    final override var taskPriority: Int = 0
    final override var taskType: Int = 0
    final override var taskUserData: Any? = null
    final override var finishCallback: Task.Callback? = null
    final override var cancellationInfo: Any? = null
        protected set
    final override var taskProgressMinChange = 0.1f

    override var dependencies: WeakRefList<Task> = WeakRefList()
        protected set

    // listeners are cleared in a TaskManager after task finishing or cancelling
    private var statusListeners = mutableListOf<Task.StatusListener>()
    private var progressListeners = WeakRefList<ProgressListener>()

    // Output
    override var taskResult: Any? = null // TODO: use generic type
    override var taskError: Exception? = null
    override var needCancelTask: Boolean = false
        protected set
    protected var isCancelled: Boolean = false // is set in subclasses when task is really cancelled
    private var startDate: Date? = null
    private var finishDate: Date? = null

    private var _taskStatus: Task.Status = Task.Status.NotStarted
    override var taskStatus: Task.Status
        get() = _taskStatus
        set(value) {
            log("TaskImpl", "setTaskStatus $value from $_taskStatus")

            val oldStatus = _taskStatus
            _taskStatus = value

            Log.d("TaskImpl", "setTaskStatus " + value + " " + taskId + " " + Thread.currentThread())
            triggerStatusListeners(oldStatus, this.taskStatus)
        }

    override val task: Task
        get() = this

    override val private: TaskPrivate
        get() = this

    override fun taskDuration(): Long {
        val startDate = startDate
        if (startDate != null) {
            val finishDate = finishDate
            val finishTime = finishDate?.time ?: Date().time
            return finishTime - startDate.time
        }
        return -1
    }

    override fun hasActiveDependencies(): Boolean {
        var isBlocked = false
        for (task in dependencies) {
            val realTask = task.get()
            if (realTask != null) {
                isBlocked = !realTask.isFinished()
                if (isBlocked) {
                    break
                }
            }
        }

        return isBlocked
    }

    override fun cancelTask(info: Any?) {
        needCancelTask = true
        cancellationInfo = info
    }

    @WorkerThread
    override fun addTaskStatusListener(listener: Task.StatusListener) {
        statusListeners.add(listener)
    }

    @WorkerThread
    override fun removeTaskStatusListener(listener: Task.StatusListener) {
        for (i in 0 until statusListeners.size) {
            if (statusListeners[i] === listener) {
                statusListeners.removeAt(i)
                break
            }
        }
    }

    override fun addTaskProgressListener(listener: ProgressListener) {
        progressListeners.add(WeakReference(listener))
    }

    override fun removeTaskProgressListener(listener: ProgressListener) {
        checkMainThread()

        var i = 0
        for (l in progressListeners) {
            if (l.get() === listener) {
                progressListeners.removeAt(i)
                break
            }

            ++i
        }
    }

    @WorkerThread
    override fun clearAllListeners() {
        statusListeners.clear()

        if (progressListeners.size > 0) {
            HandlerTools.runOnMainThread { progressListeners.clear() }
        }
    }

    override fun setTaskStartDate(date: Date) {
        this.startDate = date
    }

    override fun setTaskFinishDate(date: Date) {
        this.finishDate = date
    }

    override fun addTaskDependency(task: Task) {
        dependencies.add(WeakReference(task))
    }

    override fun removeTaskDependency(task: Task) {
        dependencies.removeValue(task)
    }

    override fun clear() {
        Assert.assertTrue(isFinished())

        _taskStatus = Task.Status.NotStarted
        cancellationInfo = null
        needCancelTask = false
        isCancelled = false
        taskError = null
        taskResult = null
        startDate = null
        finishDate = null
    }

    override fun createProgressUpdater(contentSize: Float): ProgressUpdater {
        return ProgressUpdater(contentSize, taskProgressMinChange, object : ProgressUpdater.ProgressUpdaterListener {
            override fun onProgressUpdated(updater: ProgressUpdater) {
                triggerProgressListeners(updater)
            }

            override fun onProgressCancelled(updater: ProgressUpdater, info: Any?) {
                triggerProgressListeners(updater)
                cancelTask(info)
            }
        })
    }

    @WorkerThread
    private fun triggerStatusListeners(oldStatus: Task.Status, newStatus: Task.Status) {
        if (oldStatus != newStatus) {
            val listeners = statusListeners.toList() // create a copy as the statusListeners might be modified in onTaskStatusChanged
            for (l in listeners) {
                l.onTaskStatusChanged(this@TaskImpl, oldStatus, newStatus)
            }
        }
    }

    override fun triggerProgressListeners(progressInfo: ProgressInfo) {
        if (progressListeners.size > 0) {
            HandlerTools.runOnMainThread {
                for (l in progressListeners) {
                    l.get()?.onProgressChanged(this@TaskImpl, progressInfo)
                }
            }
        }
    }

    protected fun setIsCancelled() {
        isCancelled = true
    }

    override fun canBeCancelledImmediately(): Boolean {
        return false
    }

    private fun checkMainThread() {
        Assert.assertEquals(Looper.getMainLooper().thread, Thread.currentThread())
    }
}
