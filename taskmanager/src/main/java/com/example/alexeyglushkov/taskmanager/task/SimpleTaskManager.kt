package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.alexeyglushkov.streamlib.progress.ProgressListener

import com.example.alexeyglushkov.tools.CancelError
import com.example.alexeyglushkov.tools.HandlerTools

import org.junit.Assert

import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.Comparator
import java.util.Date

import kotlinx.coroutines.*
import kotlinx.coroutines.android.asCoroutineDispatcher

/**
 * Created by alexeyglushkov on 20.09.14.
 */
open class SimpleTaskManager : TaskManager, TaskPool.Listener {
    companion object {
        internal val TAG = "SimpleTaskManager"
        const val WaitingTaskProviderId = "WaitingTaskProviderId"
    }

    var _threadRunner: ThreadRunner = InstantThreadRunner()
    // TODO: try to use channel instead of a single thread
    override var threadRunner: ThreadRunner
        get() = _threadRunner
        set(value) {
            _threadRunner = value

            loadingTasks.threadRunner = value
            for (provider in _taskProviders) {
                provider.threadRunner = value
            }
        }

    private lateinit var callbackHandler: Handler
    override var userData: Any? = null
    private var listeners = WeakRefList<TaskManager.Listener>()

    private var _taskScope: CoroutineScope? = null
    private var taskScope: CoroutineScope
        get() = _taskScope!!
        set(value) {
            _taskScope = value
        }

    private lateinit var loadingTasks: TaskPool
    private lateinit var waitingTasks: TaskProvider

    private var taskToJobMap = HashMap<Task, Job>()

    // TODO: check out how we can avoid using SafeList or another solution
    // TODO: think about weakref
    private lateinit var _taskProviders: SafeList<TaskProvider>
    override val taskProviders: List<TaskProvider>
         get() {
            return if (threadRunner.isOnThread())
                _taskProviders.originalList
            else
                _taskProviders.safeList
        }

    private lateinit var _coordinator: TaskManagerCoordinator
    override var taskManagerCoordinator: TaskManagerCoordinator
        set(value) {
            // it's experimental
            // TODO: need to handle it properly
            setupCoordinator(value)
        }
        get() = _coordinator

    override fun getLoadingTaskCount(): Int {
        return loadingTasks.getTaskCount()
    }

    @WorkerThread
    override fun getTaskCount(): Int {
        return threadRunner.run {
            var taskCount = loadingTasks.getTaskCount()
            for (provider in _taskProviders) {
                taskCount += provider.getTaskCount()
            }

            return@run taskCount
        }
    }

    @WorkerThread
    override fun getTasks(): List<Task> {
        val tasks = ArrayList<Task>()

        tasks.addAll(loadingTasks.getTasks())
        for (provider in _taskProviders) {
            tasks.addAll(provider.getTasks())
        }

        return tasks
    }

    constructor(inCoordinator: TaskManagerCoordinator) {
        init(inCoordinator, null, null)
    }

    constructor(inCoordinator: TaskManagerCoordinator, scope: CoroutineScope, taskScope: CoroutineScope) {
        init(inCoordinator, scope, taskScope)
    }

    private fun init(inCoordinator: TaskManagerCoordinator, inScope: CoroutineScope?, inTaskScope: CoroutineScope?) {
        initScope(inScope)
        initTaskSope(inTaskScope)
        setupCoordinator(inCoordinator)

        callbackHandler = Handler(Looper.myLooper())

        loadingTasks = ListTaskPool(threadRunner)
        loadingTasks.addListener(this)

        _taskProviders = createTaskProviders()
        createWaitingTaskProvider()
    }

    private fun initScope(inScope: CoroutineScope?) {
        val scope = if (inScope != null) {
            inScope
        } else {
            val localHandlerThread = HandlerThread("SimpleTaskManager Thread")
            localHandlerThread.start()
            val handler = Handler(localHandlerThread.looper)
            val dispatcher = handler.asCoroutineDispatcher("SimpleTaskManager handler dispatcher")
            CoroutineScope(dispatcher + SupervisorJob())
        }

        _threadRunner = ScopeThreadRunner(scope, "SimpleTaskManagerScopeTheadId")
        scope.launch {
            threadRunner.setup()
        }
    }

    private fun setupCoordinator(aCooridinator: TaskManagerCoordinator) {
        _coordinator = aCooridinator
        _coordinator.threadRunner = threadRunner
    }

    private fun initTaskSope(inScope: CoroutineScope?) {
        taskScope = inScope ?: CoroutineScope(Dispatchers.IO + SupervisorJob())
    }

    private fun createTaskProviders(): SafeList<TaskProvider> {
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

    private fun createWaitingTaskProvider() {
        waitingTasks = PriorityTaskProvider(threadRunner, WaitingTaskProviderId)
        waitingTasks.addListener(this)

        _taskProviders.add(waitingTasks)
    }

    override fun addTask(task: Task) {
        if (task !is TaskBase) { assert(false); return }

        // TODO: think how to handle adding two same task
        // Assert.assertEquals(task.getTaskStatus(), Task.Status.NotStarted);
        if (!Tasks.isTaskReadyToStart(task)) {
            Log.d(TAG, "addTask: Can't add task " + task.javaClass.toString() + " because it has been added " + task.taskStatus.toString())
            return
        }

        // TODO: actually I think that problem of adding the same task is not important
        // TaskManager must set Waiting status on the current thread
        setTaskStatus(task, Task.Status.Waiting)

        threadRunner.launch {
            // task will be launched in onTaskAdded method
            waitingTasks.addTask(task)
        }
    }

    override fun startImmediately(task: Task) {
        threadRunner.launchSuspend {
            if (handleTaskLoadPolicy(task, null)) {
                startTaskOnThread(task)
            }
        }
    }

    override fun cancel(task: Task, info: Any?) {
        threadRunner.launch {
            cancelTaskOnThread(task, info)
        }
    }

    override fun addTaskProvider(provider: TaskProvider) {
        Assert.assertEquals(provider.threadRunner, threadRunner)
        Assert.assertNotNull(provider.taskProviderId)

        threadRunner.launch {
            addTaskProviderOnThread(provider)
        }
    }

    @WorkerThread
    private fun addTaskProviderOnThread(provider: TaskProvider) {
        threadRunner.checkThread()

        val oldTaskProvider = getTaskProvider(provider.taskProviderId)
        if (oldTaskProvider != null) {
            removeTaskProviderOnThread(oldTaskProvider)
        }

        provider.addListener(this)
        _taskProviders.add(provider)
        provider.taskFilter = taskManagerCoordinator.taskFilter
    }

    override fun setTaskProviderPriority(provider: TaskProvider, priority: Int) {
        threadRunner.launch {
            setTaskProviderPriorityOnThread(provider, priority)
        }
    }

    @WorkerThread
    private fun setTaskProviderPriorityOnThread(provider: TaskProvider, priority: Int) {
        threadRunner.checkThread()

        provider.priority = priority
        (_taskProviders.originalList as SortedList<*>).updateSortedOrder()
    }

    override fun getTaskProvider(id: String): TaskProvider? {
        return findProvider(taskProviders, id)
    }

    private fun findProvider(providers: List<TaskProvider>, id: String): TaskProvider? {
        for (taskProvider in providers) {
            if (taskProvider.taskProviderId == id) {
                return taskProvider
            }
        }
        return null
    }

    // == TaskPool.TaskPoolListener

    @WorkerThread
    override fun onTaskConflict(pool: TaskPool, newTask: Task, oldTask: Task): Task {
        threadRunner.checkThread()
        Assert.assertTrue(pool != loadingTasks)

        return threadRunner.run {
            resolveTaskConflict(newTask, oldTask)
        }
    }

    @WorkerThread
    override fun onTaskAdded(pool: TaskPool, task: Task) {
        threadRunner.launch {
            val isLoadingPool = pool === loadingTasks
            if (isLoadingPool) {
                taskManagerCoordinator.onTaskStartedLoading(pool, task)
            }

            logTask(task, "onTaskAdded $isLoadingPool")
            triggerOnTaskAddedOnThread(task, isLoadingPool)

            if (!isLoadingPool) {
                if (handleTaskLoadPolicy(task, pool)) {
                    checkTasksToRunOnThread()
                }
            }
        }
    }

    @WorkerThread
    override fun onTaskRemoved(pool: TaskPool, task: Task) {
        threadRunner.launch {
            val isLoadingPool = pool === loadingTasks
            if (isLoadingPool) {
                taskManagerCoordinator.onTaskFinishedLoading(pool, task)
            }

            logTask(task, "onTaskRemoved $isLoadingPool")
            triggerOnTaskRemovedOnThread(task, isLoadingPool)
        }
    }

    @WorkerThread
    override fun onTaskCancelled(pool: TaskPool, task: Task, info: Any?) {
        threadRunner.launch {
            cancelTaskOnThread(task, info)
        }
    }

    // == TaskPool interface

    override fun cancelTask(task: Task, info: Any?) {
        cancel(task, info)
    }

    override fun removeTask(task: Task) {
        cancel(task, null)
    }

    @WorkerThread
    override fun getTask(taskId: String): Task? {
        threadRunner.checkThread()
        return threadRunner.run {
            var task: Task? = loadingTasks.getTask(taskId)
            if (task == null) {
                for (provider in _taskProviders) {
                    task = provider.getTask(taskId)
                    if (task != null) {
                        break
                    }
                }
            }

            return@run task
        }
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
    private fun triggerOnTaskAddedOnThread(task: Task, isLoadingQueue: Boolean) {
        threadRunner.checkThread()

        for (listener in listeners) {
            listener.get()?.onTaskAdded(this, task, isLoadingQueue)
        }
    }

    @WorkerThread
    private fun triggerOnTaskRemovedOnThread(task: Task, isLoadingQueue: Boolean) {
        threadRunner.checkThread()

        for (listener in listeners) {
            listener.get()?.onTaskRemoved(this, task, isLoadingQueue)
        }
    }

    @WorkerThread
    private fun triggerOnTaskStatusChangedOnThread(task: Task, oldStatus: Task.Status) {
        threadRunner.checkThread()

        for (listener in listeners) {
            listener.get()?.onTaskStatusChanged(task, oldStatus)
        }
    }

    // ==

    override fun removeTaskProvider(provider: TaskProvider) {
        threadRunner.launch {
            removeTaskProviderOnThread(provider)
        }
    }

    @WorkerThread
    private fun removeTaskProviderOnThread(provider: TaskProvider) {
        threadRunner.checkThread()

        // cancel all tasks
        for (task in ArrayList(provider.getTasks())) {
            cancelTaskOnThread(task, null)
        }

        _taskProviders.remove(provider)
        provider.taskFilter = null
    }

    fun setWaitingTaskProvider(provider: TaskProvider) {
        Assert.assertEquals(provider.threadRunner, threadRunner)

        provider.taskProviderId = WaitingTaskProviderId
        addTaskProvider(provider)
    }

    // Private

    // returns true when load policy checks pass
    @WorkerThread
    private fun handleTaskLoadPolicy(newTask: Task, taskPool: TaskPool?): Boolean {
        threadRunner.checkThread()
        if (Tasks.isTaskBlocked(newTask)) {
            return false
        }

        val taskId = newTask.taskId
        if (taskId != null) {
            val pools = ArrayList<TaskPool>(taskProviders) + loadingTasks
            for (pool in pools) {
                if (pool != taskPool) {
                    val oldTask = pool.getTask(taskId)
                    if (oldTask != null) {
                        assert(oldTask != newTask)
                        val resultTask = resolveTaskConflict(newTask, oldTask)
                        if (resultTask == oldTask) {
                            return false // quit when task fails
                        }
                    }
                }

                if (Tasks.isTaskBlocked(newTask)) {
                    return false
                }
            }
        }

        return true
    }

    // returns newTask if we allow adding it in a provider
    @WorkerThread
    private fun resolveTaskConflict(newTask: Task, currentTask: Task): Task {
        threadRunner.checkThread()

        return when (newTask.loadPolicy) {
            Task.LoadPolicy.AddDependencyIfAlreadyAdded -> {
                logTask(newTask, "Conflict: wait until the current task finishes")
                logTask(currentTask, "Conflict: the current task")
                // TODO: we need to call addTaskDependency for all other tasks with this id or probably create a list to store all such dependant tasks
                newTask.addTaskDependency(currentTask)
                newTask
            }
            Task.LoadPolicy.CompleteWhenAlreadyAddedCompletes -> {
                logTask(newTask, "Conflict: this task will complete with the current task")
                logTask(currentTask, "Conflict: the current task")
                connectTaskCompletions(newTask, currentTask)
                newTask
            }
            Task.LoadPolicy.CancelPreviouslyAdded -> {
                logTask(currentTask, "Conflict: this current task is cancelled")
                logTask(newTask, "Conflict: the new task")
                cancelTaskOnThread(currentTask, null)
                newTask
            }
            Task.LoadPolicy.SkipIfAlreadyAdded -> {
                logTask(newTask, "Conflict: this task was skipped as there is another task already")
                logTask(currentTask, "Conflict: another task")
                cancelTaskOnThread(newTask, null)
                currentTask
            }
        }
    }

    // completes aTask when toTask completes with setting taskResult
    // syncs progress state too
    @WorkerThread
    private fun connectTaskCompletions(newTask: Task, currentTask: Task) {
        addTaskBlockedDependency(newTask, currentTask)

        currentTask.addTaskProgressListener(ProgressListener { _, progressInfo ->
            progressInfo?.let {
                newTask as TaskBase
                newTask.private.triggerProgressListeners(progressInfo)
            }
        })

        currentTask.addTaskStatusListener(object : Task.StatusListener {
            override fun onTaskStatusChanged(task: Task, oldStatus: Task.Status, newStatus: Task.Status) {
                if (Tasks.isTaskFinished(task)) {
                    currentTask.removeTaskStatusListener(this)

                    if (task.taskStatus != Task.Status.Cancelled) {
                        newTask as TaskBase
                        newTask.private.taskResult = currentTask.taskResult
                        handleTaskFinishOnThread(newTask, false)
                    } else {
                        setTaskStatus(newTask, Task.Status.Waiting)
                    }
                }
            }
        })
    }

    @WorkerThread
    private fun addTaskBlockedDependency(newTask: Task, currentTask: Task) {
        setTaskStatus(newTask, Task.Status.Blocked)
        newTask.addTaskDependency(currentTask)
    }

    private fun setTaskStatus(task: Task, status: Task.Status) {
        val oldStatus = task.taskStatus
        task as TaskBase
        task.private.taskStatus = status

        threadRunner.launch {
            triggerOnTaskStatusChangedOnThread(task, oldStatus)
        }
    }

    @WorkerThread
    private fun checkTasksToRunOnThread() {
        threadRunner.checkThread()
        threadRunner.launchSuspend {
            if (taskManagerCoordinator.canAddMoreTasks()) {
                val task = takeTaskToRunOnThread()

                if (task != null) {
                    logTask(task, "Have taken task")
                    startTaskOnThread(task)
                }
            }
        }
    }

    @WorkerThread
    private fun takeTaskToRunOnThread(): Task? {
        threadRunner.checkThread()

        var topTaskProvider: TaskProvider? = null
        var topPriorityTask: Task? = null
        var topPriority = -1

        for (provider in _taskProviders) {
            val t = provider.getTopTask()
            if (t != null && t.taskPriority > topPriority) {
                topPriorityTask = t
                topPriority = t.taskPriority
                topTaskProvider = provider
            }
        }

        return if (topTaskProvider != null && topPriorityTask != null) {
            topTaskProvider.takeTopTask()
        } else {
            null
        }
    }

    @WorkerThread
    private suspend fun startTaskOnThread(task: Task) {
        if (task !is TaskBase) { assert(false); return }
        threadRunner.checkThread()
        Assert.assertTrue(Tasks.isTaskReadyToStart(task))

        addLoadingTaskOnThread(task)

        logTask(task, "Task started")
        setTaskStatus(task, Task.Status.Started)
        task.private.setTaskStartDate(Date())

        val job = SupervisorJob()
        taskToJobMap[task] = job

        var isCancelled = false
        withContext(taskScope.coroutineContext + job) {
            try {
                task.startTask()
            } catch (e: Throwable) {
                isCancelled = job.isCancelled
            }
        }

        logTask(task, "Task onCompleted" + if (isCancelled) " (Cancelled)" else "")
        taskToJobMap.remove(task)
        if (!isCancelled) {
            handleTaskFinishOnThread(task, false)
        }
    }

    @WorkerThread
    private fun addLoadingTaskOnThread(task: Task) {
        threadRunner.checkThread()
        loadingTasks.addTask(task)
        Log.d(TAG, "loading task count " + loadingTasks.getTaskCount())
    }

    @WorkerThread
    private fun handleTaskFinishOnThread(task: Task, isCancelled: Boolean) {
        if (task !is TaskBase) { assert(false); return }
        threadRunner.checkThread()

        val status = if (isCancelled) Task.Status.Cancelled else Task.Status.Completed
        if (isCancelled) {
            task.private.taskError = CancelError()
        }

        // the task will be removed from the provider automatically
        logTask(task, "finished")
        setTaskStatus(task, status)
        task.private.clearAllListeners()

        // TODO: use callback scope
        task.finishCallback?.let { finishCallback ->
            HandlerTools.runOnHandlerThread(callbackHandler) {
                finishCallback.onCompleted(isCancelled)
            }
        }

        //TODO: for another status like cancelled new task won't be started
        //but we cant't just call checkTasksToRunOnThread because of Task.LoadPolicy.CancelAdded
        //because we want to have new task already in waiting queue but now it isn't
        if (task.taskStatus == Task.Status.Completed) {
            checkTasksToRunOnThread()
        }
    }

    @WorkerThread
    private fun cancelTaskOnThread(task: Task, info: Any?) {
        if (task !is TaskBase) { assert(false); return }
        threadRunner.checkThread()

        if (!task.private.needCancelTask && !Tasks.isTaskFinished(task)) {
            val st = task.taskStatus
            task.private.cancelTask(info)

            val canBeCancelledImmediately = task.private.canBeCancelledImmediately()
            if (Tasks.isTaskReadyToStart(task)) {
                handleTaskFinishOnThread(task, true)
                logTask(task, "Cancelled")

            } else if (canBeCancelledImmediately) {
                if (st == Task.Status.Started) {
                    if (canBeCancelledImmediately) {
                        handleTaskFinishOnThread(task, true)
                        logTask(task, "Immediately Cancelled")

                        taskToJobMap[task]?.cancel()
                    } // else wait until the task handles needCancelTask
                } // else ignore, the callback is already called
            }
        }
    }

    /// Helpers

    private fun logTask(task: Task, prefix: String) {
        Tasks.logTask(TAG, task, prefix)
    }
}
