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
import java.util.concurrent.Executors
import kotlin.coroutines.*

/**
 * Created by alexeyglushkov on 20.09.14.
 */
open class SimpleTaskManager : TaskManager, TaskPool.Listener {
    companion object {
        internal val TAG = "SimpleTaskManager"
    }

    private var _scope: CoroutineScope? = null
    override var scope: CoroutineScope
        get() = _scope!!
        set(value) {
            _scope = value

            loadingTasks.scope = value
            waitingTasks.scope = value
            for (provider in taskProviders) {
                provider.scope = value
            }
        }

    private lateinit var callbackHandler: Handler
    override var taskExecutor: TaskExecutor = SimpleTaskExecutor() // TODO: remove
    override var userData: Any? = null
    private var listeners = WeakRefList<TaskManager.Listener>()

    private val taskJob = SupervisorJob()
    private val taskScope = CoroutineScope(Dispatchers.IO + taskJob)

    private lateinit var loadingTasks: TaskPool
    private lateinit var waitingTasks: TaskProvider
    private var taskToJobMap = HashMap<Task, Job>()

    private lateinit var _taskProviders: SafeList<TaskProvider>
    override val taskProviders: SafeList<TaskProvider>
        @WorkerThread
         get() {
            return safeRun { _taskProviders }
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
            var taskCount = loadingTasks.getTaskCount() + waitingTasks.getTaskCount()
            for (provider in taskProviders) {
                taskCount += provider.getTaskCount()
            }

            return@safeRun taskCount
        }
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

    constructor(maxLoadingTasks: Int, scope: CoroutineScope) {
        init(maxLoadingTasks, scope)
    }

    private fun init(maxLoadingTasks: Int, inScope: CoroutineScope?) {
        initScope(inScope)
        this.maxLoadingTasks = maxLoadingTasks

        callbackHandler = Handler(Looper.myLooper())

        loadingTasks = SimpleTaskPool(scope)
        loadingTasks.addListener(this)

        // TODO: consider putting it to taskProviders list
        waitingTasks = PriorityTaskProvider(scope, "TaskManagerProviderId")
        waitingTasks.addListener(this)

        _taskProviders = createTaskProviders()
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
            scope = CoroutineScope(dispatcher + Job())
        }

        scope.launch {
            threadLocal.set(ScopeTheadId)
        }

        _scope = scope
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

        scope.launch {
            // task will be launched in onTaskAdded method
            waitingTasks.addTask(task)
        }
//        HandlerTools.runOnHandlerThread(handler) {
//            // task will be launched in onTaskAdded method
//            waitingTasks.addTask(task)
//        }
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
    private fun addTaskProviderOnThread(provider: TaskProvider) {
        checkScopeThread()

        val oldTaskProvider = getTaskProvider(provider.taskProviderId)
        if (oldTaskProvider != null) {
            taskProviders.remove(oldTaskProvider)
        }

        provider.addListener(this)
        taskProviders.add(provider)
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
        (taskProviders.originalList as SortedList<*>).updateSortedOrder()
    }

    override fun getTaskProvider(id: String): TaskProvider? {
        var taskProvider: TaskProvider? = null

        if (Looper.myLooper() == taskProviders.safeHandler.looper) {
            taskProvider = findProvider(taskProviders.safeList, id)

        } else {
            safeRun {
                taskProvider = findProvider(taskProviders, id)
            }
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

    // == TaskPool interface

    override fun removeTask(task: Task) {
        cancel(task, null)
    }

    @WorkerThread
    override fun getTask(taskId: String): Task? {
        return safeRun {
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
    private fun removeTaskProviderOnThread(provider: TaskProvider) {
        checkScopeThread()

        taskProviders.remove(provider)
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

    // TODO: add getter and setter in the interface
    fun setWaitingTaskProvider(provider: TaskProvider) {
        Assert.assertEquals(provider.scope, scope)

        this.waitingTasks = provider
    }

    // Private

    @WorkerThread
    suspend private fun handleTaskLoadPolicy(task: Task): Boolean {
        checkScopeThread()

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
    suspend private fun startTaskOnThread(task: Task) {
        if (task !is TaskBase) { assert(false); return }
        checkScopeThread()
        assertTrue(Tasks.isTaskReadyToStart(task))

        withContext(scope.coroutineContext) {
            addLoadingTaskOnThread(task)

            logTask(task, "Task started")
            task.private.taskStatus = Task.Status.Started
            task.private.setTaskStartDate(Date())

            val originalCallback = task.taskCallback
            // TODO: need not to call callback from task to remove that
            val callback = object : Task.Callback {
                override fun onCompleted(cancelled: Boolean) {
//                HandlerTools.runOnHandlerThread(handler, Runnable {
//                })
                }
            }

            task.taskCallback = callback
            val job = SupervisorJob()
            taskToJobMap.put(task, job)

            var isCancelled = false
            var result: Any?

            withContext(taskScope.coroutineContext + job) {
                try {
                    result = start(task)
                } catch (e: Throwable) {
                    isCancelled = job.isCancelled || e is CancelError
                }
            }

            val isJobCancelled = coroutineContext[Job]?.isCancelled ?: false
            taskToJobMap.remove(task)
            if (!isCancelled) {
                logTask(task, "Task onCompleted" + if (isCancelled) " (Cancelled)" else "")

                // if the callback was changed while running
                //val resultCallback = if (task.taskCallback == callback) originalCallback else task.taskCallback
                handleTaskCompletionOnThread(task, originalCallback, isCancelled)
            }
        }
    }

    @WorkerThread
    private fun addLoadingTaskOnThread(task: Task) {
        checkScopeThread()

        loadingTasks.addTask(task)
        Log.d(TAG, "loading task count " + loadingTasks.getTaskCount())
    }

    suspend fun start(task: TaskBase): Any? {
        return suspendCancellableCoroutine {
            it.invokeOnCancellation {
                task.private.cancelTask(null)
            }

            task.taskCallback = object : Task.Callback {
                override fun onCompleted(cancelled: Boolean) {
                    val error = task.taskError
                    if (error != null) {
                        it.resumeWithException(error)
                    } else if (cancelled) {
                        it.resumeWithException(CancelError())
                    } else {
                        it.resume(task.taskResult)
                    }
                }

            }
            task.startTask()
        }
    }

    private fun logTask(task: Task, prefix: String) {
        Log.d(TAG, prefix + " " + task.javaClass.toString() + "(" + task.taskStatus + ")" + " id= " + task.taskId + " priority= " + task.taskPriority + " time " + task.taskDuration())
    }

    @WorkerThread
    suspend private fun handleTaskCompletionOnThread(task: Task, callback: Task.Callback?, isCancelled: Boolean) {
        if (task !is TaskBase) { assert(false); return }
        checkScopeThread()

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
    suspend private fun cancelTaskOnThread(task: Task, info: Any?) {
        if (task !is TaskBase) { assert(false); return }
        checkScopeThread()

        if (!task.private.needCancelTask && !Tasks.isTaskCompleted(task)) {
            val st = task.taskStatus
            task.private.cancelTask(info)

            val job = taskToJobMap.get(task)
            if (st == Task.Status.Waiting || st == Task.Status.NotStarted || st == Task.Status.Blocked || task.private.canBeCancelledImmediately()) {
                if (st == Task.Status.Started) {
                    if (job != null) {
                        job.cancel()
                    }
                    //task.private.startCallback?.onCompleted(true) // to get the original callback

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

    /// thread safe stuff

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
        assert(isScopeThread())
    }
}
