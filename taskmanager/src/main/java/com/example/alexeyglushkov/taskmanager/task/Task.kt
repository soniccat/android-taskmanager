package com.example.alexeyglushkov.taskmanager.task

import com.example.alexeyglushkov.streamlib.progress.ProgressListener

/**
 * Created by alexeyglushkov on 20.09.14.
 */

// A task represents an action that takes time to finish. It can be downloading data, processing
// information, work with a database and other.
// The task can be started only once. If you want to repeat it you should create new one.
//
// If you work with a com.example.alexeyglushkov.wordteacher.model on com.example.alexeyglushkov.wordteacher.main thread the task shouldn't work with the com.example.alexeyglushkov.wordteacher.model data stored
// outside the task. The com.example.alexeyglushkov.wordteacher.model data should be changed on com.example.alexeyglushkov.wordteacher.main thread after task completion and
// notify UI.
//
// All setters mustn't be called after starting the task

interface Task : TaskContainer {
    // Set the callback to handle the result.
    // TaskManager uses this callback but calls the original
    //
    // Caller: Client, TaskManager
    //
    var taskCallback: Callback? // get the current callback

    //TODO: add custom thread callback support through set/get

    // Return info passed in the cancel method
    //
    // Caller: Client
    //
    val cancellationInfo: Any?

    // Get the current Status
    //
    // Caller: Client, TaskManager, TaskPool
    //
    val taskStatus: Status
    // Set/Get current type of the task
    // The type is used in TaskManager to be able to set load limits on a particular type
    //
    // Caller: Client, TaskManager (getter only), TaskProvider (getter only)
    //
    var taskType: Int
    // Set/Get the task's id. Tasks with the same id means that they download the same data for the same caller.
    // It's useful when you want to bind the task to a specific com.example.alexeyglushkov.wordteacher.model object and provide an additional load policy
    // The right load policy can prevent needless loadings
    // nil means that the task doesn't have the id
    //
    // Caller: Client, TaskManager (getter only), TaskPool (getter only)
    //
    var taskId: String?
    // For tasks with the same id TaskManager uses a load policy to handle the situation when they
    // want to load data simultaneously
    //
    // Caller: Client, TaskManager (getter only), TaskPool (getter only)
    //
    var loadPolicy: LoadPolicy

    // Return the error of the task happened during the execution
    // Basically it should be called from the start callback
    //
    // Caller: Client
    //
    val taskError: Exception?

    // Get the result object
    //
    // Caller: Client
    //
    val taskResult: Any?
    // Set/Get the priority of the task
    //
    // Caller: Client, TaskProvider (getter only), TaskManager (getter only)
    //
    var taskPriority: Int
    // Set minimal progress change to trigger the ProgressListener
    //
    // Caller: Client
    //
    var taskProgressMinChange: Float

    // TODO: now it works wrong
    // Get time passed between Started and Finished/Cancelled states
    //
    // Caller: Client, TaskManager
    //
    fun taskDuration(): Long

    //TODO: finish comments
    // Set/Get additional information to the task
    //
    // Caller: Client
    //
    var taskUserData: Any?
    fun isBlocked(): Boolean

    // Handy way to access private methods
    // should be called only in TaskManager, TaskPool
    //
    // Caller: TaskManager
    //
    //val private: TaskPrivate

    // add/remove dependency
    //
    // Caller: Client
    //
    fun addTaskDependency(task: Task)

    fun removeTaskDependency(task: Task)

    // TODO: add auto clear listeners
    // Add/Remove a listener to get status changes
    // after setting Finished or Cancelled states all listeners are cleared by TaskManager
    //
    // Caller: Client, TaskPool
    //
    fun addTaskStatusListener(listener: StatusListener)

    fun removeTaskStatusListener(listener: StatusListener)

    // TODO: add auto clear listeners
    // Add/Remove a Progress listener
    // after setting Finished or Cancelled states all listeners are cleared by TaskManager
    //
    // Caller: Client
    //
    fun addTaskProgressListener(listener: ProgressListener)

    fun removeTaskProgressListener(listener: ProgressListener)

    // Start the task
    // It's from private part but must be implemented on this level
    // Transfer callback from TaskManager
    //
    // Caller: TaskManager
    suspend fun startTask()

    enum class Status {
        NotStarted, //not started
        Waiting, //in queue, must be set on the caller thread
        Blocked, //is waiting until all dependencies finish
        Started, //started
        Finished, //successfully loaded
        Cancelled //has cancelled but still is loading or cancelled while waiting in a queue
    }

    enum class LoadPolicy {
        SkipIfAlreadyAdded, // don't load this task if there is another loading task with the same id
        CancelPreviouslyAdded // cancel already added task, in this case you shouldn't do anything with cancelled task
        // TODO: AddDependency add dependency to start after finishing
    }

    // TODO: pass Error to get to know that the task was cancelled or an error happened
    // TODO: maybe call it completion?
    interface Callback {
        //Here I put the cancelled as the argument to emphasise that it must be handled (also it can be got from status)
        fun onCompleted(cancelled: Boolean)
    }

    interface StatusListener {
        // TODO: remove newStatus arg
        fun onTaskStatusChanged(task: Task, oldStatus: Status, newStatus: Status)
    }
}
