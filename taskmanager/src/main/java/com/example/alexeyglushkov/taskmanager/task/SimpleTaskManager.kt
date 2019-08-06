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
import kotlinx.coroutines.*
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlin.coroutines.*

/**
 * Created by alexeyglushkov on 20.09.14.
 */
open class SimpleTaskManager : TaskManager, TaskPool.Listener {
    companion object {
        internal val TAG = "SimpleTaskManager"
        const val WaitingTaskProviderId = "WaitingTaskProviderId"
    }

    private var _scope: CoroutineScope? = null
    override var scope: CoroutineScope
        get() = _scope!!
        set(value) {
            _scope = value

            loadingTasks.scope = value
            for (provider in _taskProviders) {
                provider.scope = value
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
    private var taskToCallbackMap = HashMap<Task, Task.Callback?>() // keep original callbacks

    private lateinit var _taskProviders: SafeList<TaskProvider>
    override val taskProviders: List<TaskProvider>
         get() {
            return if (isScopeThread())
                _taskProviders.originalList
            else
                _taskProviders.safeList
        }

    private var _limits = SparseArrayCompat<Float>()
    override var limits: SparseArrayCompat<Float>
        @WorkerThread
        get() {
            return safeRun { _limits.clone() }
        }
        set(value) {
            safeRun {
                _limits = value
            }
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
        return safeRun {
            var taskCount = loadingTasks.getTaskCount()
            for (provider in _taskProviders) {
                taskCount += provider.getTaskCount()
            }

            return@safeRun taskCount
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
        init(maxLoadingTasks, null, null)
    }

    constructor(maxLoadingTasks: Int, scope: CoroutineScope, taskScope: CoroutineScope) {
        init(maxLoadingTasks, scope, taskScope)
    }

    private fun init(maxLoadingTasks: Int, inScope: CoroutineScope?, inTaskScope: CoroutineScope?) {
        initScope(inScope)
        initTaskSope(inTaskScope)
        this.maxLoadingTasks = maxLoadingTasks

        callbackHandler = Handler(Looper.myLooper())

        loadingTasks = SimpleTaskPool(scope)
        loadingTasks.addListener(this)

        _taskProviders = createTaskProviders()
        createWaitingTaskProvider()
    }

    private fun initScope(inScope: CoroutineScope?) {
        val scope: CoroutineScope
        if (inScope != null) {
            scope = inScope
        } else {
            val localHandlerThread = HandlerThread("SimpleTaskManager Thread")
            localHandlerThread.start()
            val handler = Handler(localHandlerThread.looper)
            val dispatcher = handler.asCoroutineDispatcher("SimpleTaskManager handler dispatcher")
            scope = CoroutineScope(dispatcher + SupervisorJob())
        }

        scope.launch {
            threadLocal.set(ScopeTheadId)
        }

        _scope = scope
    }

    private fun initTaskSope(inScope: CoroutineScope?) {
        if (inScope != null) {
            taskScope = inScope
        } else {
            taskScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        }
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
        waitingTasks = PriorityTaskProvider(scope, WaitingTaskProviderId)
        waitingTasks.addListener(this)

        _taskProviders.add(waitingTasks)
    }

    override fun addTask(task: Task) {
        if (task !is TaskBase) { assert(false); return }

        //TODO: think how to handle adding two same task
        //Assert.assertEquals(task.getTaskStatus(), Task.Status.NotStarted);
        if (!Tasks.isTaskReadyToStart(task)) {
            Log.d(TAG, "addTask: Can't add task " + task.javaClass.toString() + " because it has been added " + task.taskStatus.toString())
            return
        }

        // TODO: actually I think that problem of adding the same task is not important
        // TaskManager must set Waiting status on the current thread
        task.private.taskStatus = Task.Status.Waiting

        scope.launch {
            // task will be launched in onTaskAdded method
            waitingTasks.addTask(task)
        }
    }

    override fun startImmediately(task: Task) {
        scope.launch {
            if (handleTaskLoadPolicy(task)) {
                startTaskOnThread(task)

            } else {
                cancelTaskOnThread(task, null)
            }
        }
    }

    override fun cancel(task: Task, info: Any?) {
        scope.launch {
            cancelTaskOnThread(task, info)
        }
    }

    override fun addTaskProvider(provider: TaskProvider) {
        Assert.assertEquals(provider.scope, scope)
        Assert.assertNotNull(provider.taskProviderId)

        scope.launch {
            addTaskProviderOnThread(provider)
        }
    }

    @WorkerThread
    suspend private fun addTaskProviderOnThread(provider: TaskProvider) {
        checkScopeThread()

        val oldTaskProvider = getTaskProvider(provider.taskProviderId)
        if (oldTaskProvider != null) {
            removeTaskProviderOnThread(oldTaskProvider)
        }

        provider.addListener(this)
        _taskProviders.add(provider)
    }

    override fun setTaskProviderPriority(provider: TaskProvider, priority: Int) {
        scope.launch {
            setTaskProviderPriorityOnThread(provider, priority)
        }
    }

    @WorkerThread
    private fun setTaskProviderPriorityOnThread(provider: TaskProvider, priority: Int) {
        checkScopeThread()

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
    override fun onTaskAdded(pool: TaskPool, task: Task) {
        scope.launch {
            val isLoadingPool = pool === loadingTasks
            if (isLoadingPool) {
                updateUsedSpace(task.taskType, true)
            }

            Log.d("add", "onTaskAdded " + isLoadingPool + " " + task.taskId)
            triggerOnTaskAddedOnThread(task, isLoadingPool)

            if (!isLoadingPool) {
                if (handleTaskLoadPolicy(task)) {
                    checkTasksToRunOnThread()

                } else {
                    cancelTaskOnThread(task, null)
                }
            }
        }
    }

    @WorkerThread
    override fun onTaskRemoved(pool: TaskPool, task: Task) {
        scope.launch {
            val isLoadingPool = pool === loadingTasks
            if (isLoadingPool) {
                updateUsedSpace(task.taskType, false)
            }

            Log.d("add", "onTaskRemoved " + isLoadingPool + " " + task.taskId)
            triggerOnTaskRemovedOnThread(task, isLoadingPool)
        }
    }

    @WorkerThread
    override fun onTaskCancelled(pool: TaskPool, task: Task, info: Any?) {
        scope.launch {
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
        return safeRun {
            var task: Task? = loadingTasks.getTask(taskId)
            if (task == null) {
                for (provider in _taskProviders) {
                    task = provider.getTask(taskId)
                    if (task != null) {
                        break
                    }
                }
            }

            return@safeRun task
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
        checkScopeThread()

        for (listener in listeners) {
            listener.get()?.onTaskAdded(this, task, isLoadingQueue)
        }
    }

    @WorkerThread
    private fun triggerOnTaskRemovedOnThread(task: Task, isLoadingQueue: Boolean) {
        checkScopeThread()

        for (listener in listeners) {
            listener.get()?.onTaskRemoved(this, task, isLoadingQueue)
        }
    }

    // ==

    override fun removeTaskProvider(provider: TaskProvider) {
        scope.launch {
            removeTaskProviderOnThread(provider)
        }
    }

    @WorkerThread
    suspend private fun removeTaskProviderOnThread(provider: TaskProvider) {
        checkScopeThread()

        // cancel all tasks
        for (task in ArrayList(provider.getTasks())) {
            cancelTaskOnThread(task, null)
        }

        _taskProviders.remove(provider)
    }

    override fun setLimit(taskType: Int, availableQueuePart: Float) {
        scope.launch {
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

    fun setWaitingTaskProvider(provider: TaskProvider) {
        Assert.assertEquals(provider.scope, scope)

        provider.taskProviderId = WaitingTaskProviderId
        addTaskProvider(provider)
    }

    // Private

    @WorkerThread
    suspend private fun handleTaskLoadPolicy(task: Task): Boolean {
        checkScopeThread()

        //search for the task with the same id
        val taskId = task.taskId
        if (taskId != null) {
            val addedTask = loadingTasks.getTask(taskId) // TODO: what about waiting tasks? need to search through them too...
            assert(addedTask != task)

            if (addedTask != null) {
                if (task.loadPolicy == Task.LoadPolicy.CancelPreviouslyAdded) {
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
    suspend private fun checkTasksToRunOnThread() {
        checkScopeThread()

        if (loadingTasks.getTaskCount() < maxLoadingTasks) {
            val task = takeTaskToRunOnThread()

            if (task != null) {
                startTaskOnThread(task)
            }
        }
    }

    @WorkerThread
    private fun takeTaskToRunOnThread(): Task? {
        checkScopeThread()

        val taskTypesToFilter = getTaskTypeFilter()
        var topTaskProvider: TaskProvider? = null
        var topPriorityTask: Task? = null
        var topPriority = -1

        var i = 0
        for (provider in _taskProviders) {
            val t = provider.getTopTask(taskTypesToFilter)
            if (t != null && t.taskPriority > topPriority) {
                topPriorityTask = t
                topPriority = t.taskPriority
                topTaskProvider = provider
            }

            ++i
        }

        if (topPriorityTask != null && !reachedLimit(topPriorityTask.taskType)) {
            topPriorityTask = topTaskProvider!!.takeTopTask(taskTypesToFilter)
        } else {
            topPriorityTask = null
        }

        return topPriorityTask
    }

    @WorkerThread
    suspend private fun startTaskOnThread(task: Task) {
        if (task !is TaskBase) { assert(false); return }
        checkScopeThread()
        assertTrue(Tasks.isTaskReadyToStart(task))

        scope.launch {
            addLoadingTaskOnThread(task)

            logTask(task, "Task started")
            task.private.taskStatus = Task.Status.Started
            task.private.setTaskStartDate(Date())

            val job = SupervisorJob()
            taskToJobMap.put(task, job)
            taskToCallbackMap.put(task, task.taskCallback)

            var isCancelled = false
            withContext(taskScope.coroutineContext + job) {
                try {
                    start(task)
                } catch (e: Throwable) {
                    isCancelled = job.isCancelled
                }
            }

            logTask(task, "Task onCompleted" + if (isCancelled) " (Cancelled)" else "")
            taskToJobMap.remove(task)
            if (!isCancelled) {
                handleTaskCompletionOnThread(task, false)
            }
        }
    }

    @WorkerThread
    private fun addLoadingTaskOnThread(task: Task) {
        checkScopeThread()

        loadingTasks.addTask(task)
        Log.d(TAG, "loading task count " + loadingTasks.getTaskCount())
    }

    suspend fun start(task: TaskBase) {
        return suspendCancellableCoroutine {
            it.invokeOnCancellation {
                if (!Tasks.isTaskCompleted(task) && !task.private.needCancelTask) {
                    cancel(task, null) // that shouldn't happen as we cancel the job after cancelling the task
                }
            }

            task.taskCallback = object : Task.Callback {
                override fun onCompleted(cancelled: Boolean) {
                    val error = task.taskError
                    if (error != null) {
                        it.resumeWithException(error)
                    } else if (cancelled) {
                        it.resumeWithException(CancellationException())
                    } else {
                        it.resume(Unit)
                    }
                }
            }
            task.startTask()
        }
    }

    @WorkerThread
    suspend private fun handleTaskCompletionOnThread(task: Task, isCancelled: Boolean) {
        if (task !is TaskBase) { assert(false); return }
        checkScopeThread()

        val wasStarted = task.taskStatus == Task.Status.Started
        val status = if (isCancelled) Task.Status.Cancelled else Task.Status.Finished
        if (isCancelled) {
            task.private.taskError = CancelError()
        }

        // the task will be removed from the provider automatically
        Log.d("tag", "task status " + task.taskStatus.toString())
        task.private.taskStatus = status
        task.private.clearAllListeners()

        if (wasStarted) {
            task.taskCallback = taskToCallbackMap[task] // return original callback
            taskToCallbackMap[task] = null
        }

        // TODO: use callback scope
        HandlerTools.runOnHandlerThread(callbackHandler) {
            task.taskCallback?.onCompleted(isCancelled)
        }

        //TODO: for another status like cancelled new task won't be started
        //but we cant't just call checkTasksToRunOnThread because of Task.LoadPolicy.CancelAdded
        //because we want to have new task already in waiting queue but now it isn't
        if (status == Task.Status.Finished) {
            checkTasksToRunOnThread()
        }
    }

    @WorkerThread
    suspend private fun cancelTaskOnThread(task: Task, info: Any?) {
        if (task !is TaskBase) { assert(false); return }
        checkScopeThread()

        if (!task.private.needCancelTask && !Tasks.isTaskCompleted(task)) {
            val st = task.taskStatus
            task.private.cancelTask(info)

            val job = taskToJobMap.get(task)
            val canBeCancelledImmediately = task.private.canBeCancelledImmediately()
            if (Tasks.isTaskReadyToStart(task)) {
                handleTaskCompletionOnThread(task, true)
                logTask(task, "Cancelled")

            } else if (canBeCancelledImmediately) {
                if (st == Task.Status.Started) {
                    if (canBeCancelledImmediately) {
                        handleTaskCompletionOnThread(task, true)
                        logTask(task, "Immediately Cancelled")

                        if (job != null) {
                            job.cancel()
                        }
                    } // else wait until the task handles needCancelTask
                } // else ignore, the callback is already called
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

    /// thread checking stuff

    private val ScopeTheadId = "SimpleTaskManagerScopeTheadId"
    private val threadLocal = ThreadLocal<String>()
    private fun isScopeThread() = threadLocal.get() == ScopeTheadId

    private fun <T> safeRun(block: () -> T) : T {
        if (isScopeThread()) {
            return block()
        } else {
            return runBlocking(scope.coroutineContext) {
                return@runBlocking block()
            }
        }
    }

    fun checkScopeThread() {
        Assert.assertTrue("Scope thread is expected", isScopeThread())
    }

    /// Helpers

    private fun logTask(task: Task, prefix: String) {
        Log.d(TAG, prefix + " " + task.javaClass.toString() + "(" + task.taskStatus + ")" + " id= " + task.taskId + " priority= " + task.taskPriority + " time " + task.taskDuration())
    }
}
