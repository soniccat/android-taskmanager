package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler
import android.os.HandlerThread
import com.nhaarman.mockitokotlin2.*

import org.junit.Assert.*
import org.mockito.Mockito.`when`

/**
 * Created by alexeyglushkov on 23.08.15.
 */
class TaskManagerTestSet {
    protected lateinit var taskManager: TaskManager

    fun before(taskManager: TaskManager) {
        this.taskManager = taskManager

        // mock executor
        val executor = mock<TaskExecutor>()
        doAnswer { invocation ->
            val task = invocation.arguments[0] as Task
            val callback = invocation.arguments[1] as Task.Callback
            task.startTask()
            return@doAnswer null
        }.`when`(executor).executeTask(any())

        taskManager.taskExecutor = executor
    }

    fun setMaxLoadingTasks() {
        // Act
        taskManager.maxLoadingTasks = 100

        // Verify
        assertEquals(100, taskManager.maxLoadingTasks)
    }

    fun getTasks() {
        // Arrange
        val task1 = TestTasks.createTaskMock("taskId1", Task.Status.NotStarted)
        val task2 = TestTasks.createTaskMock("taskId2", Task.Status.NotStarted)
        val task3 = TestTasks.createTaskMock("taskId3", Task.Status.NotStarted)

        val taskProvider = createTaskProviderSpy("0", taskManager)

        // Act
        taskManager.maxLoadingTasks = 0
        taskManager.addTaskProvider(taskProvider)
        taskManager.addTask(task1)
        taskManager.addTask(task2)
        taskProvider.addTask(task3)

        // Verify
        assertEquals(3, taskManager.getTasks().size)
        assertTrue(taskManager.getTasks().contains(task1))
        assertTrue(taskManager.getTasks().contains(task2))
        assertTrue(taskManager.getTasks().contains(task3))
    }

    fun getTaskCount() {
        // Arrange
        val task1 = TestTasks.createTaskMock("taskId1", Task.Status.NotStarted)
        val task2 = TestTasks.createTaskMock("taskId2", Task.Status.NotStarted)
        val task3 = TestTasks.createTaskMock("taskId3", Task.Status.NotStarted)

        val taskProvider = createTaskProviderSpy("0", taskManager)

        // Act
        taskManager.maxLoadingTasks = 0
        taskManager.addTaskProvider(taskProvider)
        taskManager.addTask(task1)
        taskManager.addTask(task2)
        taskProvider.addTask(task3)

        // Verify
        assertEquals(3, taskManager.getTaskCount())
    }

    fun getLoadingTaskCount() {
        // Arrange
        val task1 = TestTasks.createTaskMock("taskId1")
        val task2 = TestTasks.createTaskMock("taskId2")
        val task3 = TestTasks.createTaskMock("taskId3")

        // Act
        taskManager.addTask(task1)
        taskManager.addTask(task2)
        taskManager.addTask(task3)

        // Verify
        assertEquals(3, taskManager.loadingTaskCount)
    }

    fun getTaskFromProvider() {
        // Arrange
        val taskProvider1 = createTaskProviderSpy("provider1", taskManager, 20)
        val taskProvider2 = createTaskProviderSpy("provider2", taskManager, 10)
        val task = TestTasks.createTaskMock("task1")

        // Act
        taskManager.maxLoadingTasks = 0
        taskManager.addTaskProvider(taskProvider1)
        taskManager.addTaskProvider(taskProvider2)
        taskProvider1.addTask(task)

        // Verify
        assertEquals(task, taskProvider1.getTask("task1"))
        assertEquals(task, taskManager.getTask("task1"))
    }

    fun addTaskProvider() {
        // Arrange
        val taskProvider1 = createTaskProviderMock("provider1", taskManager, 20)
        val taskProvider2 = createTaskProviderMock("provider2", taskManager, 10)

        // Act
        taskManager.addTaskProvider(taskProvider1)
        taskManager.addTaskProvider(taskProvider2)

        // Verify
        assertEquals(2, taskManager.taskProviders.size)
        assertNotNull(taskManager.getTaskProvider("provider1"))
        assertNotNull(taskManager.getTaskProvider("provider2"))

        assertTrue(taskProviderIndex(taskProvider1) == 0)
        assertTrue(taskProviderIndex(taskProvider2) == 1)
    }

    fun addTaskProvider2() {
        // Arrange
        val taskProvider1 = createTaskProviderMock("provider1", taskManager, 20)
        val taskProvider2 = createTaskProviderMock("provider2", taskManager, 10)
        val taskProvider3 = createTaskProviderMock("provider3", taskManager, 15)

        // Act
        taskManager.addTaskProvider(taskProvider1)
        taskManager.addTaskProvider(taskProvider2)
        taskManager.addTaskProvider(taskProvider3)

        // Verify
        assertEquals(3, taskManager.taskProviders.size)
        assertNotNull(taskManager.getTaskProvider("provider1"))
        assertNotNull(taskManager.getTaskProvider("provider2"))
        assertNotNull(taskManager.getTaskProvider("provider3"))

        assertTrue(taskProviderIndex(taskProvider1) == 0)
        assertTrue(taskProviderIndex(taskProvider2) == 2)
        assertTrue(taskProviderIndex(taskProvider3) == 1)
    }

    fun addTaskProviderWithTheSameId() {
        // Arrange
        val taskProvider1 = createTaskProviderMock("provider1", taskManager)
        val taskProvider2 = createTaskProviderMock("provider1", taskManager)

        // Act
        taskManager.addTaskProvider(taskProvider1)
        taskManager.addTaskProvider(taskProvider2)

        // Verify
        assertEquals(1, taskManager.taskProviders.size)
        assertEquals(taskProvider2, taskManager.getTaskProvider("provider1"))
    }

    fun removeTaskProvider() {
        // Arrange
        val taskProvider1 = createTaskProviderMock("provider1", taskManager)
        val taskProvider2 = createTaskProviderMock("provider2", taskManager)

        // Act
        taskManager.addTaskProvider(taskProvider1)
        taskManager.addTaskProvider(taskProvider2)
        taskManager.removeTaskProvider(taskProvider1)

        // Verify
        assertEquals(1, taskManager.taskProviders.size)
        assertNull(taskManager.getTaskProvider("provider1"))
        assertNotNull(taskManager.getTaskProvider("provider2"))
    }

    fun setTaskProviderPriority() {
        // Arrange
        val taskProvider1 = createTaskProviderSpy("taskProvider1", taskManager)
        taskProvider1.priority = 20

        val taskProvider2 = createTaskProviderSpy("taskProvider2", taskManager)
        taskProvider2.priority = 10

        // Act
        taskManager.addTaskProvider(taskProvider1)
        taskManager.addTaskProvider(taskProvider2)
        taskManager.setTaskProviderPriority(taskProvider2, 30)

        // Verify 2
        assertTrue(taskProviderIndex(taskProvider1) == 1)
        assertTrue(taskProviderIndex(taskProvider2) == 0)
    }

    fun setTaskExecutor() {
        // Arrange
        val executor = mock<TaskExecutor>()

        // Act
        taskManager.taskExecutor = executor

        // Verify
        assertEquals(executor, taskManager.taskExecutor)
    }

    fun startImmediately() {
        // Arrange
        val task = TestTasks.createTaskMock()
        val taskPrivate = task.private
        val listener = mock<TaskManager.Listener>()

        // Act
        taskManager.addListener(listener)
        taskManager.startImmediately(task)

        // Verify
        verify(taskPrivate).taskStatus = Task.Status.Started
        verify(listener, never()).onTaskAdded(taskManager, task, false)
        verify(listener, never()).onTaskRemoved(taskManager, task, false)
        verify(listener).onTaskAdded(taskManager, task, true)
    }

    fun startImmediatelySkipPolicy() {
        // Arrange
        val task1 = TestTasks.createTestTaskSpy("taskId")
        val task2 = TestTasks.createTestTaskSpy("taskId")

        val listener = mock<TaskManager.Listener>()

        // Act
        taskManager.addListener(listener)
        taskManager.addTask(task1)
        taskManager.startImmediately(task2)

        // Verify
        assertEquals(Task.Status.Started, task1.taskStatus)
        verify(listener).onTaskAdded(taskManager, task1, true)

        assertEquals(Task.Status.Cancelled, task2.taskStatus)
        verify(listener, never()).onTaskAdded(taskManager, task2, false)
        verify(listener, never()).onTaskRemoved(taskManager, task2, false)
        verify(listener, never()).onTaskAdded(taskManager, task2, true)
        verify(listener, never()).onTaskRemoved(taskManager, task2, true)
    }

    fun startImmediatelySkipPolicyWithFinish() {
        // Arrange
        val task1 = TestTasks.createTestTaskSpy("taskId")
        val task2 = TestTasks.createTestTaskSpy("taskId")

        val callback1 = mock<Task.Callback>()
        val callback2 = mock<Task.Callback>()

        task1.taskCallback = callback1
        task2.taskCallback = callback2

        val listener = mock<TaskManager.Listener>()

        // Act
        taskManager.addListener(listener)
        taskManager.addTask(task1)
        taskManager.startImmediately(task2)
        task1.finish()

        assertEquals(Task.Status.Finished, task1.taskStatus)
        verify(listener).onTaskAdded(taskManager, task1, true)
        verify(listener).onTaskRemoved(taskManager, task1, true)

        assertEquals(Task.Status.Cancelled, task2.taskStatus)
        verify(listener, never()).onTaskAdded(taskManager, task2, false)
        verify(listener, never()).onTaskRemoved(taskManager, task2, false)
        verify(listener, never()).onTaskAdded(taskManager, task2, true)
        verify(listener, never()).onTaskRemoved(taskManager, task2, true)
        verify(callback1).onCompleted(false)
        verify(callback2).onCompleted(true)
    }

    fun startImmediatelyFinish() {
        // Arrange
        val task = TestTask()
        val callback = mock<Task.Callback>()
        task.taskCallback = callback

        val listener = mock<TaskManager.Listener>()

        // Act
        taskManager.addListener(listener)
        taskManager.startImmediately(task)
        task.finish()

        // Verify
        assertEquals(Task.Status.Finished, task.taskStatus)
        verify(listener, never()).onTaskAdded(taskManager, task, false)
        verify(listener, never()).onTaskRemoved(taskManager, task, false)
        verify(listener).onTaskAdded(taskManager, task, true)
        verify(listener).onTaskRemoved(taskManager, task, true)
        verify(callback).onCompleted(false)
        assertEquals(0, taskManager.getTaskCount())
    }

    fun startImmediatelyFinishWithChangedCallback() {
        // Arrange
        val task = TestTask()
        val callback1 = mock<Task.Callback>()
        val callback2 = mock<Task.Callback>()
        task.taskCallback = callback1

        val listener = mock<TaskManager.Listener>()

        // Act
        taskManager.addListener(listener)
        taskManager.startImmediately(task)
        task.taskCallback = callback2
        task.finish()

        // Verify
        assertEquals(Task.Status.Finished, task.taskStatus)
        verify(listener, never()).onTaskAdded(taskManager, task, false)
        verify(listener, never()).onTaskRemoved(taskManager, task, false)
        verify(listener).onTaskAdded(taskManager, task, true)
        verify(listener).onTaskRemoved(taskManager, task, true)
        verify(callback1, never()).onCompleted(any())
        verify(callback2).onCompleted(false)
        assertEquals(0, taskManager.getTaskCount())
    }

    fun startImmediatelyCancelWithChangedCallback() {
        // Arrange
        val task = TestTasks.createTestTaskSpy("taskId")
        `when`(task.canBeCancelledImmediately()).thenReturn(true)

        val callback1 = mock<Task.Callback>()
        val callback2 = mock<Task.Callback>()
        task.taskCallback = callback1

        val listener = mock<TaskManager.Listener>()

        // Act
        taskManager.addListener(listener)
        taskManager.startImmediately(task)
        task.taskCallback = callback2
        taskManager.cancel(task, null)

        // Verify
        assertEquals(Task.Status.Cancelled, task.taskStatus)
        verify(listener, never()).onTaskAdded(taskManager, task, false)
        verify(listener, never()).onTaskRemoved(taskManager, task, false)
        verify(listener).onTaskAdded(taskManager, task, true)
        verify(listener).onTaskRemoved(taskManager, task, true)
        verify(callback1, never()).onCompleted(any())
        verify(callback2).onCompleted(true)
        assertEquals(0, taskManager.getTaskCount())
    }

    fun addTask() {
        // Arrange
        val task = TestTasks.createTaskMock()
        val listener = mock<TaskManager.Listener>()
        val taskPrivate = task.private

        // Act
        taskManager.addListener(listener)
        taskManager.addTask(task)

        // Verify
        verify(taskPrivate, atLeast(1)).taskStatus = Task.Status.Waiting
        verify(listener).onTaskAdded(taskManager, task, false)
        verify(listener).onTaskAdded(taskManager, task, true)

        assertEquals(taskManager.getTaskCount(), 1)
        assertTrue(taskManager.getTasks().contains(task))
    }

    fun addStartedTask() {
        // Arrange
        val task = TestTasks.createTaskMock(null, Task.Status.Started)
        val listener = mock<TaskManager.Listener>()
        val taskPrivate = task.private

        // Act
        taskManager.addListener(listener)
        taskManager.addTask(task)

        // Verify
        verify(taskPrivate, never()).taskStatus = Task.Status.Waiting
        verify(task, never()).addTaskStatusListener(taskManager)
        verify(listener, never()).onTaskAdded(taskManager, task, true)
        verify(listener, never()).onTaskAdded(taskManager, task, false)

        assertEquals(taskManager.getTaskCount(), 0)
        assertFalse(taskManager.getTasks().contains(task))
    }

    fun addTheSameTaskWithSkipPolicy() {
        // Arrange
        val listener = mock<TaskManager.Listener>()

        val task1 = TestTasks.createTestTaskSpy("taskId")
        val task2 = TestTasks.createTestTaskSpy("taskId")
        task2.loadPolicy = Task.LoadPolicy.SkipIfAdded

        // Act
        taskManager.addListener(listener)
        taskManager.addTask(task1)
        taskManager.addTask(task2)

        // Verify
        assertEquals(Task.Status.Started, task1.taskStatus)
        verify<Task>(task1, atLeastOnce()).taskId
        verify(listener).onTaskAdded(taskManager, task1, true)
        verify(listener).onTaskRemoved(taskManager, task1, false)
        verify(listener).onTaskAdded(taskManager, task1, false)

        assertEquals(Task.Status.Cancelled, task2.taskStatus)
        verify<Task>(task2, atLeastOnce()).taskId
        verify(listener).onTaskAdded(taskManager, task2, false)
        verify(listener, never()).onTaskAdded(taskManager, task2, true)
        verify(listener).onTaskRemoved(taskManager, task2, false)

        assertEquals(taskManager.getTaskCount(), 1)
        assertTrue(taskManager.getTasks().contains(task1))
    }

    fun addTheSameTaskWithCancelPolicy() {
        // Arrange
        val task1 = TestTasks.createTestTaskSpy("taskId")
        val task2 = TestTasks.createTestTaskSpy("taskId")
        task2.loadPolicy = Task.LoadPolicy.CancelAdded

        // Act
        taskManager.addTask(task1)
        taskManager.addTask(task2)

        // Verify
        assertEquals(Task.Status.Started, task1.taskStatus)
        assertTrue(task1.private.needCancelTask)

        assertEquals(Task.Status.Started, task2.taskStatus)
        assertFalse(task2.private.needCancelTask)

        assertEquals(taskManager.getTaskCount(), 2)
        assertTrue(taskManager.getTasks().contains(task1))
        assertTrue(taskManager.getTasks().contains(task2))
    }

    fun taskCallbackCalled() {
        // Arrange
        val listener = mock<TaskManager.Listener>()

        val task = TestTasks.createTestTaskSpy("taskId")
        val callback = mock<Task.Callback>()
        task.taskCallback = callback

        taskManager.addListener(listener)

        // Act
        taskManager.addTask(task)
        task.finish()

        // Verify
        assertEquals(Task.Status.Finished, task.taskStatus)
        verify(listener).onTaskRemoved(taskManager, task, true)
        verify(callback).onCompleted(false)
    }

    fun changedTaskCallbackCalled() {
        // Arrange
        val listener = mock<TaskManager.Listener>()

        val task = TestTasks.createTestTaskSpy("taskId")
        val callback = mock<Task.Callback>()
        val newCallback = mock<Task.Callback>()
        task.taskCallback = callback

        taskManager.addListener(listener)

        // Act
        taskManager.addTask(task)
        task.taskCallback = newCallback
        task.finish()

        // Verify
        assertEquals(Task.Status.Finished, task.taskStatus)
        verify(listener).onTaskRemoved(taskManager, task, true)
        verify(callback, never()).onCompleted(any())
        verify(newCallback).onCompleted(false)
    }

    fun taskWithCancelledImmediatelyCallbackCalledAfterCancel() {
        // Arrange
        val listener = mock<TaskManager.Listener>()

        val task = TestTasks.createTestTaskSpy("taskId")
        `when`(task.canBeCancelledImmediately()).thenReturn(true)

        val callback = mock<Task.Callback>()
        task.taskCallback = callback

        taskManager.addListener(listener)

        // Act
        taskManager.addTask(task)
        taskManager.cancel(task, null)
        task.finish()

        // Verify
        assertEquals(Task.Status.Cancelled, task.taskStatus)
        verify(listener).onTaskRemoved(taskManager, task, true)
        verify(callback).onCompleted(true)
    }

    fun taskWithCancelledImmediatelyAndChangedCallbackCalled() {
        // Arrange
        val listener = mock<TaskManager.Listener>()

        val task = TestTasks.createTestTaskSpy("taskId")
        `when`(task.canBeCancelledImmediately()).thenReturn(true)

        val callback = mock<Task.Callback>()
        val newCallback = mock<Task.Callback>()
        task.taskCallback = callback

        taskManager.addListener(listener)

        // Act
        taskManager.addTask(task)
        task.taskCallback = newCallback
        taskManager.cancel(task, null)
        task.finish()

        // Verify
        assertEquals(Task.Status.Cancelled, task.taskStatus)
        verify(listener).onTaskRemoved(taskManager, task, true)
        verify(callback, never()).onCompleted(true)
        verify(newCallback).onCompleted(true)
    }

    fun addStateListener() {
        // Arrange
        val task = TestTasks.createTaskMock("taskId", Task.Status.NotStarted)
        val listener1 = mock<TaskManager.Listener>()
        val listener2 = mock<TaskManager.Listener>()

        // Act
        taskManager.addListener(listener1)
        taskManager.addListener(listener2)
        taskManager.addTask(task)

        // Verify
        verify(listener1).onTaskAdded(taskManager, task, false)
        verify(listener1).onTaskAdded(taskManager, task, true)
        verify(listener2).onTaskAdded(taskManager, task, false)
        verify(listener2).onTaskAdded(taskManager, task, true)
    }

    fun removeStateListener() {
        // Arrange
        val task = TestTasks.createTaskMock("taskId", Task.Status.NotStarted)
        val listener1 = mock<TaskManager.Listener>()
        val listener2 = mock<TaskManager.Listener>()

        // Act
        taskManager.addListener(listener1)
        taskManager.addListener(listener2)
        taskManager.removeListener(listener1)
        taskManager.removeListener(listener2)
        taskManager.addTask(task)

        // Verify
        verify(listener1, never()).onTaskAdded(taskManager, task, false)
        verify(listener1, never()).onTaskAdded(taskManager, task, true)
        verify(listener2, never()).onTaskAdded(taskManager, task, false)
        verify(listener2, never()).onTaskAdded(taskManager, task, true)
    }

    fun removeTask() {
        // Arrange
        val task = TestTasks.createTaskMock("taskId", Task.Status.NotStarted)
        val listener = mock<TaskManager.Listener>()
        val taskPrivate = task.private

        // Act
        taskManager.addListener(listener)
        taskManager.addTask(task)
        taskManager.removeTask(task)

        // Verify
        verify(listener).onTaskAdded(taskManager, task, false)
        verify(listener).onTaskAdded(taskManager, task, true)
        verify(listener).onTaskRemoved(taskManager, task, false)
        verify(taskPrivate).cancelTask(null)

        assertEquals(1, taskManager.getTaskCount())
    }

    fun removeUnknownTask() {
        // Arrange
        val task1 = TestTasks.createTaskMock("taskId", Task.Status.NotStarted)
        val task2 = TestTasks.createTaskMock("taskId", Task.Status.NotStarted)
        val listener = mock<TaskManager.Listener>()

        // Act
        taskManager.addListener(listener)
        taskManager.addTask(task1)
        taskManager.removeTask(task2)

        // Verify
        verify(listener).onTaskAdded(taskManager, task1, false)
        verify(listener).onTaskAdded(taskManager, task1, true)
        verify(listener).onTaskRemoved(taskManager, task1, false)
        verify(listener, never()).onTaskRemoved(taskManager, task1, true)
        verify(listener, never()).onTaskAdded(taskManager, task2, false)
        verify(listener, never()).onTaskAdded(taskManager, task2, true)
        verify(listener, never()).onTaskRemoved(taskManager, task2, false)
        verify(listener, never()).onTaskRemoved(taskManager, task2, true)

        assertEquals(1, taskManager.getTaskCount())
    }

    fun checkTaskRemovingAfterFinishing() {
        // Arrange
        val testTask = TestTask()

        // Act
        taskManager.addTask(testTask)
        testTask.private.taskStatus = Task.Status.Finished

        // Verify
        assertEquals(0, taskManager.getTaskCount())
    }

    fun setGetHandler() {
        // Arrange
        val handlerThread = HandlerThread("HandlerThread")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        val taskProvider = createTaskProviderMock("0", taskManager)

        // Act
        taskManager.addTaskProvider(taskProvider)
        taskManager.handler = handler

        // Verify
        verify(taskProvider).handler = handler
        assertEquals(handler, taskManager.handler)
    }

    fun setLimit() {
        val listener = mock<TaskManager.Listener>()

        // Act
        taskManager.addListener(listener)
        taskManager.setLimit(1, 0.5f)

        // Verify
        verify(listener).onLimitsChanged(taskManager, 1, 0.5f)
        assertEquals(0.5f, taskManager.limits.get(1)!!, 0.001f)

    }

    fun setLimitRemove() {
        // Act
        taskManager.setLimit(1, 0.5f)
        taskManager.setLimit(1, 0.0f)

        // Verify
        assertNull(taskManager.limits.get(1))
    }

    // Tools

    private fun createTaskProviderSpy(id: String, taskManager: TaskManager): TaskProvider {
        val taskProvider = TestTaskProvider(taskManager.handler, id)
        return spy(taskProvider)
    }

    private fun createTaskProviderMock(id: String, taskManager: TaskManager, priority: Int = 0): TaskProvider {
        val provider = mock<TaskProvider>()
        `when`(provider.taskProviderId).thenReturn(id)
        `when`(provider.handler).thenReturn(taskManager.handler)
        `when`(provider.priority).thenReturn(priority)
        return provider
    }

    private fun createTaskProviderSpy(id: String, taskManager: TaskManager, priority: Int): TaskProvider {
        val provider = PriorityTaskProvider(taskManager.handler, id)
        provider.priority = priority
        return provider
    }

    private fun taskProviderIndex(taskProvider: TaskProvider): Int {
        var i = 0
        for (p in taskManager.taskProviders) {
            if (p === taskProvider) {
                break
            }

            ++i
        }

        return i
    }
}
