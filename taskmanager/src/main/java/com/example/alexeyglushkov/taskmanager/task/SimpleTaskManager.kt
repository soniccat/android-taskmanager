package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import androidx.collection.SparseArrayCompat

import android.util.Log
import androidx.annotation.WorkerThread

import com.example.alexeyglushkov.tools.CancelError
import com.example.alexeyglushkov.tools.HandlerTools

import org.junit.Assert

import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.Comparator
import java.util.Date

import junit.framework.Assert.assertTrue

/**
 * Created by alexeyglushkov on 20.09.14.
 */
open class SimpleTaskManager : TaskManager, TaskPool.Listener {
    private var handlerThread: HandlerThread? = null
    private var _handler: Handler? = null
    override var handler: Handler
        get() = _handler!!
        set(value) {
            _handler = value

            loadingTasks.handler = value
            waitingTasks.handler = value
            for (provider in taskProviders) {
                provider.handler = value
            }
        }

    private lateinit var callbackHandler: Handler
    override var taskExecutor: TaskExecutor = SimpleTaskExecutor()
    override var userData: Any? = null
    private var listeners = WeakRefList<TaskManager.Listener>()

    private lateinit var loadingTasks: TaskPool
    private lateinit var waitingTasks: TaskProvider

    private lateinit var _taskProviders: SafeList<TaskProvider>
    override val taskProviders: SafeList<TaskProvider>
        @WorkerThread
        get() {
            checkHandlerThread()
            return _taskProviders
        }

    private var _limits = SparseArrayCompat<Float>()
    override var limits: SparseArrayCompat<Float>
        @WorkerThread
        get() {
            checkHandlerThread()
            return _limits.clone()
        }
        set(value) {
            checkHandlerThread()
            _limits = value
        }

    private var _usedSpace = SparseArrayCompat<Int>()
    override var usedSpace //type -> task count from loadingTasks
        get() = _usedSpace.clone()
        set(value) {
            _usedSpace = value.clone()
        }

    override var maxLoadingTasks: Int = 0
        get() {
            return field
        }
        set(value) {
            field = value
            // TODO: handle somehow on the thread
            // TODO: we need run tasks when increase the size
            // TODO: probably we should cancel tasks after decreasing
        }

    override val loadingTaskCount: Int
        get() = loadingTasks.getTaskCount()

    @WorkerThread
    override fun getTaskCount(): Int {
        checkHandlerThread()

        var taskCount = loadingTasks.getTaskCount() + waitingTasks.getTaskCount()
        for (provider in taskProviders) {
            taskCount += provider.getTaskCount()
        }

        return taskCount
    }

    @WorkerThread
    override fun getTasks(): List<Task> {
        val tasks = ArrayList<Task>()

        tasks.addAll(loadingTasks.getTasks())
        tasks.addAll(waitingTasks.getTasks())
        for (provider in taskProviders) {
            tasks.addAll(provider.getTasks())
        }

        return tasks
    }

    private fun getTaskTypeFilter(): List<Int> {
        val taskTypesToFilter = ArrayList<Int>()
        for (i in 0 until _limits.size()) {
            if (reachedLimit(_limits.keyAt(i))) {
                taskTypesToFilter.add(_limits.keyAt(i))
            }
        }
        return taskTypesToFilter
    }

    constructor(maxLoadingTasks: Int) {
        init(maxLoadingTasks, null)
    }

    constructor(maxLoadingTasks: Int, inHandler: Handler) {
        init(maxLoadingTasks, inHandler)
    }

    private fun init(maxLoadingTasks: Int, inHandler: Handler?) {
        initHandler(inHandler)
        this.maxLoadingTasks = maxLoadingTasks

        callbackHandler = Handler(Looper.myLooper())

        loadingTasks = SimpleTaskPool(handler)
        loadingTasks.addListener(this)

        // TODO: consider putting it to taskProviders list
        waitingTasks = PriorityTaskProvider(handler, "TaskManagerProviderId")
        waitingTasks.addListener(this)

        _taskProviders = createTaskProvidersTreeSet()
    }

    private fun initHandler(inHandler: Handler?) {
        if (inHandler != null) {
            _handler = inHandler
        } else {
            val localHandlerThread = HandlerThread("SimpleTaskManager Thread")
            handlerThread = localHandlerThread
            localHandlerThread.start()
            _handler = Handler(localHandlerThread.looper)
        }
    }

    private fun createTaskProvidersTreeSet(): SafeList<TaskProvider> {
        val sortedList = SortedList(Comparator<TaskProvider> { lhs, rhs ->
            if (lhs.priority == rhs.priority) {
                return@Comparator 0
            }

            if (lhs.priority > rhs.priority) {
                -1
            } else 1
        })

        return SafeList(sortedList, callbackHandler)
    }

    override fun addTask(task: Task) {
        if (task !is TaskBase) { assert(false); return }

        //TODO: think how to handle adding two same task
        //Assert.assertEquals(task.getTaskStatus(), Task.Status.NotStarted);
        if (!Tasks.isTaskReadyToStart(task)) {
            Log.d(TAG, "Can't put task " + task.javaClass.toString() + " because it has been added " + task.taskStatus.toString())
            return
        }

        // TODO: actually I think that problem of adding the same task is not important
        // TaskManager must set Waiting status on the current thread
        task.private.taskStatus = Task.Status.Waiting

        HandlerTools.runOnHandlerThread(handler) {
            // task will be launched in onTaskAdded method
            waitingTasks.addTask(task)
        }
    }

    override fun startImmediately(task: Task) {
        HandlerTools.runOnHandlerThread(handler) {
            if (handleTaskLoadPolicy(task)) {
                startTaskOnThread(task)

            } else {
                cancelTaskOnThread(task, null)
            }
        }
    }

    override fun cancel(task: Task, info: Any?) {
        HandlerTools.runOnHandlerThread(handler) {
            cancelTaskOnThread(task, info)
        }
    }

    override fun addTaskProvider(provider: TaskProvider) {
        Assert.assertEquals(provider.handler, handler)
        Assert.assertNotNull(provider.taskProviderId)

        HandlerTools.runOnHandlerThread(handler) {
            addTaskProviderOnThread(provider)
        }
    }

    @WorkerThread
    private fun addTaskProviderOnThread(provider: TaskProvider) {
        checkHandlerThread()

        val oldTaskProvider = getTaskProvider(provider.taskProviderId)
        if (oldTaskProvider != null) {
            taskProviders.remove(oldTaskProvider)
        }

        provider.addListener(this)
        taskProviders.add(provider)
    }

    override fun setTaskProviderPriority(provider: TaskProvider, priority: Int) {
        HandlerTools.runOnHandlerThread(handler) { setTaskProviderPriorityOnThread(provider, priority) }
    }

    @WorkerThread
    private fun setTaskProviderPriorityOnThread(provider: TaskProvider, priority: Int) {
        checkHandlerThread()

        provider.priority = priority
        (taskProviders.originalList as SortedList<*>).updateSortedOrder()
    }

    override fun getTaskProvider(id: String): TaskProvider? {
        var taskProvider: TaskProvider? = null

        if (Looper.myLooper() == taskProviders.handler.looper) {
            taskProvider = findProvider(taskProviders.safeList, id)

        } else {
            checkHandlerThread()
            taskProvider = findProvider(this.taskProviders, id)
        }

        return taskProvider
    }

    private fun findProvider(providers: ArrayList<TaskProvider>, id: String): TaskProvider? {
        for (taskProvider in providers) {
            if (taskProvider.taskProviderId == id) {
                return taskProvider
            }
        }
        return null
    }

    // == TaskPool.TaskPoolListener

    @WorkerThread
    override fun onTaskAdded(pool: TaskPool, task: Task) {
        checkHandlerThread()

        val isLoadingPool = pool === loadingTasks
        if (isLoadingPool) {
            updateUsedSpace(task.taskType, true)
        }

        Log.d("add", "onTaskAdded " + isLoadingPool + " " + task.taskId)
        triggerOnTaskAdded(task, isLoadingPool)

        if (!isLoadingPool) {
            if (handleTaskLoadPolicy(task)) {
                checkTasksToRunOnThread()

            } else {
                cancelTaskOnThread(task, null)
            }
        }
    }

    @WorkerThread
    override fun onTaskRemoved(pool: TaskPool, task: Task) {
        checkHandlerThread()

        val isLoadingPool = pool === loadingTasks
        if (isLoadingPool) {
            updateUsedSpace(task.taskType, false)
        }

        Log.d("add", "onTaskRemoved " + isLoadingPool + " " + task.taskId)
        triggerOnTaskRemoved(task, isLoadingPool)
    }

    // == TaskPool interface

    override fun removeTask(task: Task) {
        cancel(task, null)
    }

    @WorkerThread
    override fun getTask(taskId: String): Task? {
        checkHandlerThread()

        var task: Task? = loadingTasks.getTask(taskId)
        if (task == null) {
            task = waitingTasks.getTask(taskId)
        }

        if (task == null) {
            for (provider in taskProviders) {
                task = provider.getTask(taskId)
                if (task != null) {
                    break
                }
            }
        }

        return task
    }

    override fun removeListener(listener: TaskManager.Listener) {
        listeners.removeValue(listener)
    }

    override fun addListener(listener: TaskManager.Listener) {
        listeners.add(WeakReference(listener))
    }

    override fun addListener(listener: TaskPool.Listener) {
        //unnecessary
    }

    override fun removeListener(listener: TaskPool.Listener) {
        //unnecessary
    }

    override fun onTaskStatusChanged(task: Task, oldStatus: Task.Status, newStatus: Task.Status) {
        //unnecessary
    }

    @WorkerThread
    private fun triggerOnTaskAdded(task: Task, isLoadingQueue: Boolean) {
        checkHandlerThread()

        for (listener in listeners) {
            listener.get()?.onTaskAdded(this, task, isLoadingQueue)
        }
    }

    @WorkerThread
    private fun triggerOnTaskRemoved(task: Task, isLoadingQueue: Boolean) {
        checkHandlerThread()

        for (listener in listeners) {
            listener.get()?.onTaskRemoved(this, task, isLoadingQueue)
        }
    }

    // ==

    override fun removeTaskProvider(provider: TaskProvider) {
        HandlerTools.runOnHandlerThread(handler) { removeTaskProviderOnThread(provider) }
    }

    @WorkerThread
    private fun removeTaskProviderOnThread(provider: TaskProvider) {
        checkHandlerThread()

        taskProviders.remove(provider)
    }

    override fun setLimit(taskType: Int, availableQueuePart: Float) {
        HandlerTools.runOnHandlerThread(handler) {
            if (availableQueuePart <= 0.0f) {
                _limits.remove(taskType)
            } else {
                _limits.put(taskType, availableQueuePart)
            }

            for (listener in listeners) {
                listener.get()?.onLimitsChanged(this@SimpleTaskManager, taskType, availableQueuePart)
            }
        }
    }

    // TODO: add getter and setter in the interface
    fun setWaitingTaskProvider(provider: TaskProvider) {
        Assert.assertEquals(provider.handler, handler)

        this.waitingTasks = provider
    }

    // Private

    @WorkerThread
    private fun handleTaskLoadPolicy(task: Task): Boolean {
        checkHandlerThread()

        //search for the task with the same id
        val taskId = task.taskId
        if (taskId != null) {
            val addedTask = loadingTasks.getTask(taskId)
            assert(addedTask != task)

            if (addedTask != null) {
                if (task.loadPolicy == Task.LoadPolicy.CancelAdded) {
                    cancelTaskOnThread(addedTask, null)
                } else {
                    Log.d(TAG, "The task was skipped due to the Load Policy " + task.loadPolicy.toString() + task.javaClass.toString() + " " + taskId + " " + task.taskStatus.toString())
                    return false
                }
            }
        }
        return true
    }

    @WorkerThread
    private fun checkTasksToRunOnThread() {
        checkHandlerThread()

        if (this.loadingTasks.getTaskCount() < this.maxLoadingTasks) {
            val task = takeTaskToRunOnThread()

            if (task != null) {
                startTaskOnThread(task)
            }
        }
    }

    @WorkerThread
    private fun takeTaskToRunOnThread(): Task? {
        checkHandlerThread()

        val taskTypesToFilter = getTaskTypeFilter()
        val topWaitingTask = this.waitingTasks.getTopTask(taskTypesToFilter)

        var topTaskProvider: TaskProvider? = null
        var topPriorityTask: Task? = null
        var topPriority = -1

        if (topWaitingTask != null) {
            topPriorityTask = topWaitingTask
            topPriority = topWaitingTask.taskPriority
        }

        var i = 0
        for (provider in taskProviders) {
            val t = provider.getTopTask(taskTypesToFilter)
            if (t != null && t.taskPriority > topPriority) {
                topPriorityTask = t
                topTaskProvider = provider
            }

            ++i
        }

        if (topPriorityTask != null && !reachedLimit(topPriorityTask.taskType)) {
            if (topTaskProvider == null) {
                topPriorityTask = waitingTasks.takeTopTask(taskTypesToFilter)

            } else {
                topPriorityTask = topTaskProvider.takeTopTask(taskTypesToFilter)
            }
        } else {
            topPriorityTask = null
        }

        return topPriorityTask
    }

    @WorkerThread
    private fun startTaskOnThread(task: Task) {
        if (task !is TaskBase) { assert(false); return }
        checkHandlerThread()
        assertTrue(Tasks.isTaskReadyToStart(task))

        addLoadingTaskOnThread(task)

        logTask(task, "Task started")
        task.private.taskStatus = Task.Status.Started
        task.private.setTaskStartDate(Date())

        val originalCallback = task.taskCallback
        val callback = object : Task.Callback {
            override fun onCompleted(cancelled: Boolean) {
                HandlerTools.runOnHandlerThread(handler, Runnable {
                    if (Tasks.isTaskCompleted(task)) {
                        // may happen if canBeCancelledImmediately returns true
                        return@Runnable
                    }

                    logTask(task, "Task onCompleted" + if (cancelled) " (Cancelled)" else "")

                    // if the callback was changed while running
                    val resultCallback = if (task.taskCallback === this) originalCallback else task.taskCallback
                    handleTaskCompletionOnThread(task, resultCallback, cancelled)
                })
            }
        }

        task.taskCallback = callback
        taskExecutor.executeTask(task, callback)
    }

    private fun logTask(task: Task, prefix: String) {
        Log.d(TAG, prefix + " " + task.javaClass.toString() + "(" + task.taskStatus + ")" + " id= " + task.taskId + " priority= " + task.taskPriority + " time " + task.taskDuration())
    }

    @WorkerThread
    private fun handleTaskCompletionOnThread(task: Task, callback: Task.Callback?, isCancelled: Boolean) {
        if (task !is TaskBase) { assert(false); return }
        checkHandlerThread()

        val status = if (isCancelled) Task.Status.Cancelled else Task.Status.Finished
        if (isCancelled) {
            task.private.taskError = CancelError()
        }

        // the task will be removed from the provider automatically
        Log.d("tag", "task status " + task.taskStatus.toString())
        task.private.taskStatus = status
        task.private.clearAllListeners()

        task.taskCallback = callback // return original callback

        HandlerTools.runOnHandlerThread(callbackHandler) {
            callback?.onCompleted(isCancelled)
        }

        //TODO: for the another status like cancelled new task won't be started
        //but we cant't just call checkTasksToRunOnThread because of Task.LoadPolicy.CancelAdded
        //because we want to have new task already in waiting queue but now it isn't
        if (status == Task.Status.Finished) {
            checkTasksToRunOnThread()
        }
    }

    @WorkerThread
    private fun cancelTaskOnThread(task: Task, info: Any?) {
        if (task !is TaskBase) { assert(false); return }
        checkHandlerThread()

        if (!task.private.needCancelTask && !Tasks.isTaskCompleted(task)) {
            val st = task.taskStatus
            task.private.cancelTask(info)

            if (st == Task.Status.Waiting || st == Task.Status.NotStarted || st == Task.Status.Blocked || task.private.canBeCancelledImmediately()) {
                if (st == Task.Status.Started) {
                    task.private.startCallback?.onCompleted(true) // to get the original callback

                } else {
                    handleTaskCompletionOnThread(task, task.taskCallback, true)
                    logTask(task, "Cancelled")
                }
            }
        }
    }

    private fun reachedLimit(taskType: Int): Boolean {
        return if (_limits.get(taskType, -1.0f) == -1.0f) {
            false
        } else _usedSpace.get(taskType, 0).toFloat() / maxLoadingTasks.toFloat() >= _limits.get(taskType, 0.0f)

    }

    private fun updateUsedSpace(taskType: Int, add: Boolean) {
        var count: Int = _usedSpace.get(taskType, 0)
        if (add) {
            ++count
        } else {
            assertTrue(count > 0)
            --count
        }

        _usedSpace.put(taskType, count)
    }

    // helpers

    @WorkerThread
    private fun addLoadingTaskOnThread(task: Task) {
        if (task !is TaskBase) { assert(false); return }
        checkHandlerThread()

        this.loadingTasks.addTask(task)
        Log.d(TAG, "loading task count " + loadingTasks.getTaskCount())
    }

    private fun checkHandlerThread() {
        Assert.assertEquals(Looper.myLooper(), handler.looper)
    }

    companion object {
        internal val TAG = "SimpleTaskManager"
    }
}
