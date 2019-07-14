package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler
import android.os.Looper
import androidx.core.util.Pair
import android.util.Log
import androidx.annotation.WorkerThread

import com.example.alexeyglushkov.tools.HandlerTools

import org.junit.Assert

import java.util.ArrayList

/**
 * Created by alexeyglushkov on 11.06.17.
 */

// TODO: remove that, just use LiveData
open class RestorableTaskProvider(provider: TaskProvider) : TaskProviderWrapper(provider) {
    var isRecording: Boolean = false
        set(recording) {
            field = recording

            if (!this.isRecording) {
                clearStoredTasks()
            }
        }
    var activeTasks: MutableList<Task> = ArrayList()
    var completedTasks = mutableListOf<Task>()

    enum class RestoreState {
        NotRestored, // task wasn't found
        Restored, // completed task was found and should be handled
        ReplacedCompletion // task is in progress, completion will be called when the task finishes
    }

    override fun addTask(task: Task) {
        if (!Tasks.isTaskReadyToStart(task)) {
            Log.e(TAG, "Can't put task " + task.javaClass.toString() + " because it's already started " + task.taskStatus.toString())
            return
        }

        super.addTask(task)
        if (task.taskStatus == Task.Status.NotStarted) {
            return
        }

        HandlerTools.runOnHandlerThread(handler) { task.addTaskStatusListener(this@RestorableTaskProvider) }

        if (isDebug) {
            Log.d(TAG, "activeTasks addTaskStatusListener " + task + " status " + task.taskStatus)
        }
    }

    override fun onTaskStatusChanged(task: Task, oldStatus: Task.Status, newStatus: Task.Status) {
        if (isDebug) {
            Log.d(TAG, "activeTasks onTaskStatusChanged $task from $oldStatus to $newStatus")
        }

        if (Tasks.isTaskCompleted(task)) {
            checkHandlerThread()
            activeTasks.remove(task)

            if (isDebug) {
                Log.d(TAG, "activeTasks removed $task")
            }

            if (isRecording) {
                completedTasks.add(task)
            }
        }
    }

    @WorkerThread
    override fun takeTopTask(typesToFilter: List<Int>?): Task? {
        checkHandlerThread()

        val task = super.takeTopTask(typesToFilter)
        if (task != null) {
            activeTasks.add(task)

            if (isDebug) {
                Log.d(TAG, "activeTasks added $task")
            }
        }

        return task
    }

    private fun findCompletedTask(taskId: String): Task? {
        return findTask(completedTasks, taskId)
    }

    fun restoreTaskCompletion(taskId: String, callback: Task.Callback) {
        val looper = Looper.myLooper()
        restoreTaskCompletion(taskId, getDefaultCompletion(callback, looper))
    }

    fun restoreTaskCompletion(taskId: String, completion: Completion) {
        HandlerTools.runOnHandlerThread(handler) {
            val restoredData = restoreTaskCompletionOnThread(taskId)
            completion.completed(restoredData.first, restoredData.second)
        }
    }

    fun restoreTaskCompletions(completionProvider: TaskCompletionProvider) {
        val looper = Looper.myLooper()

        HandlerTools.runOnHandlerThread(handler) {
            for (task in activeTasks) {
                val callback = completionProvider.getCallback(task)
                if (callback != null) {
                    val taskCompletion = getDefaultCompletion(callback, looper)

                    val restoredData = restoreActiveTask(task)
                    taskCompletion.completed(restoredData.first, restoredData.second)
                }
            }

            for (task in completedTasks) {
                val callback = completionProvider.getCallback(task)
                if (callback != null) {
                    val taskCompletion = getDefaultCompletion(callback, looper)

                    val restoredData = restoreCompletedTask(task)
                    taskCompletion.completed(restoredData.first, restoredData.second)
                }
            }
        }
    }

    interface TaskCompletionProvider {
        fun getCallback(task: Task): Task.Callback?
    }

    fun getDefaultCompletion(taskCallback: Task.Callback?, callbackLooper: Looper?): Completion {
        return object : Completion {
            override fun completed(restoredTask: Task?, restoredState: RestoreState?) {
                if (restoredState == RestoreState.ReplacedCompletion) {
                    // that callback is handled in task manager instead of startCallback
                    restoredTask!!.taskCallback = taskCallback

                } else if (restoredState == RestoreState.Restored) {
                    val h = Handler(callbackLooper)
                    HandlerTools.runOnHandlerThread(h) { taskCallback!!.onCompleted(restoredTask!!.taskStatus == Task.Status.Cancelled) }
                }
            }
        }
    }

    private fun restoreTaskCompletionOnThread(taskId: String): Pair<Task, RestoreState> {
        checkHandlerThread()
        var task = findTask(activeTasks, taskId)

        if (task != null) {
            return restoreActiveTask(task)

        } else {
            task = findCompletedTask(taskId)
            return restoreCompletedTask(task)
        }
    }

    private fun restoreActiveTask(task: Task): Pair<Task, RestoreState> {
        var restoreState = RestoreState.NotRestored
        if (Tasks.isTaskCompleted(task)) {
            // if a task is completed it will be in completedTasks
            Assert.fail("Can't stop here")
        } else {
            restoreState = RestoreState.ReplacedCompletion
        }

        return Pair(task, restoreState)
    }

    private fun restoreCompletedTask(task: Task?): Pair<Task, RestoreState> {
        var restoreState = RestoreState.NotRestored

        if (task != null) {
            restoreState = RestoreState.Restored
        }

        return Pair(task, restoreState)
    }

    private fun findTask(tasks: List<Task>, taskId: String): Task? {
        checkHandlerThread()

        var result: Task? = null
        for (task in tasks) {
            if (task.taskId == taskId) {
                result = task
                break
            }
        }

        return result
    }

    private fun clearStoredTasks() {
        completedTasks.clear()
    }

    interface Completion {
        fun completed(task: Task?, state: RestoreState?)
    }

    protected fun checkHandlerThread() {
        Assert.assertEquals(Looper.myLooper(), handler.looper)
    }

    companion object {

        internal val TAG = "RestorableTaskProvider"
        private val isDebug = false
    }
}
