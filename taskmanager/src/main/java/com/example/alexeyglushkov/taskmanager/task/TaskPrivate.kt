package com.example.alexeyglushkov.taskmanager.task

import com.example.alexeyglushkov.streamlib.progress.ProgressInfo
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater

import java.util.Date

/**
 * Created by alexeyglushkov on 21.07.15.
 */

// Contains methods being used only by TaskManager, TaskPool or a Task

interface TaskPrivate : Task {
    val needCancelTask: Boolean

    // Get dependencies
    //
    // Caller: TaskManager
    //
    val dependencies: WeakRefList<Task>

    // Marks the task that it should be cancelled
    // it will have Cancelled state if the task is started and successfully cancelled
    // if it isn't possible to cancel the task the state will be set to Finished
    // otherwise NotStarted state will be set
    //
    // Caller: TaskManager
    //
    fun cancelTask(info: Any?)

    // Set current state of the task
    //
    // Caller: Client, TaskManager
    //
    //fun setTaskStatus(status: Task.Status)
    override var taskStatus: Task.Status

    // Set Date after changing the state to Started
    //
    // Caller: TaskManager
    //
    fun setTaskStartDate(date: Date)

    // Set error
    //
    // Caller: Task
    // TODO: use throwable
    override var taskError: Exception?

    // TODO: need to call
    // Set Date after changing the state to Started
    //
    // Caller: TaskManager
    //
    fun setTaskFinishDate(date: Date)

    // Clear all progress and status listeners
    //
    // Caller: TaskManager
    //
    fun clearAllListeners()

    // Create a ProgressUpdater which trigger progress listeners on changes
    // Generally used to pass in a reader object
    //
    // Caller: Task
    //
    fun createProgressUpdater(contentSize: Float): ProgressUpdater

    // Notify all progress listeners
    //
    // Caller: Task
    //
    fun triggerProgressListeners(progressInfo: ProgressInfo)

    // To keep the result
    //
    // Caller: Task
    //
    override var taskResult: Any? // TODO: use generic type

    // To reset loaded data and states to load it again
    //
    // Caller:
    //
    fun clear()

    // Flag shows that the command could be removed from the queue before finishing
    // It make sense to keep it false for a task that can affect performance if it is being cancelled
    //
    // Caller: TaskManager
    //
    fun canBeCancelledImmediately(): Boolean
}
