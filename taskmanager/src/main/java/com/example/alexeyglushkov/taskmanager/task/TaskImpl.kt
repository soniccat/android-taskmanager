package com.example.alexeyglushkov.taskmanager.task

import android.os.Looper
import android.util.Log
import androidx.annotation.WorkerThread

import com.example.alexeyglushkov.streamlib.progress.ProgressInfo
import com.example.alexeyglushkov.streamlib.progress.ProgressListener
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater
import com.example.alexeyglushkov.tools.HandlerTools

import org.junit.Assert
import java.lang.Exception

import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.Date

/**
 * Created by alexeyglushkov on 23.07.15.
 */
abstract class TaskImpl : TaskBase, TaskPrivate {
    override var finishCallback: Task.Callback? = null
    override var cancellationInfo: Any? = null
        protected set

    override var taskUserData: Any? = null
    override var needCancelTask: Boolean = false
        protected set
    protected var isCancelled: Boolean = false // is set in subclasses when task is really cancelled
    override var taskProgressMinChange = 0.1f

    override var taskError: Exception? = null
    override var taskResult: Any? = null // TODO: use generic type
    override var taskId: String? = null
    override var loadPolicy: Task.LoadPolicy = Task.LoadPolicy.SkipIfAlreadyAdded
    override var taskPriority: Int = 0
    override var taskType: Int = 0

    protected var startDate: Date? = null
    protected var finishDate: Date? = null

    override var dependencies: WeakRefList<Task> = WeakRefList()
        protected set

    // listeners are cleared in a TaskManager after task finishing or cancelling
    protected var statusListeners = mutableListOf<Task.StatusListener>()
    protected var progressListeners = WeakRefList<ProgressListener>()

    protected var _taskStatus: Task.Status = Task.Status.NotStarted
    override var taskStatus: Task.Status
        get() = _taskStatus
        set(value) {
            val oldStatus = this.taskStatus
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
            val finishTime = if (finishDate == null) Date().time else finishDate.time
            return finishTime - startDate.time
        }
        return -1
    }

    override fun isBlocked(): Boolean {
        var isBlocked = false
        for (task in dependencies) {
            val realTask = task.get()
            if (realTask != null) {
                isBlocked = !Tasks.isTaskFinished(realTask)
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
        checkMainThread()

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
        Assert.assertTrue(Tasks.isTaskFinished(this))

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
