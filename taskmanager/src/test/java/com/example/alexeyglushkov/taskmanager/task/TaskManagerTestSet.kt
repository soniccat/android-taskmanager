package com.example.alexeyglushkov.taskmanager.task

import com.example.alexeyglushkov.taskmanager.TaskManager
import com.example.alexeyglushkov.taskmanager.providers.PriorityTaskProvider
import com.example.alexeyglushkov.taskmanager.providers.TaskProvider
import com.example.alexeyglushkov.taskmanager.runners.ScopeThreadRunner
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import org.junit.Assert.*
import org.mockito.Mockito.`when`
import java.lang.Exception

/**
 * Created by alexeyglushkov on 23.08.15.
 */
class TaskManagerTestSet {
    var controller: TaskManagerController? = null
    protected lateinit var taskManager: TaskManager

    fun before(taskManager: TaskManager) {
        this.taskManager = taskManager
    }

    fun getTasks() {
        // Arrange
        val task1 = TestTasks.createTaskMock("taskId1", Task.Status.NotStarted)
        val task2 = TestTasks.createTaskMock("taskId2", Task.Status.NotStarted)
        val task3 = TestTasks.createTaskMock("taskId3", Task.Status.NotStarted)

        val taskProvider = createTaskProviderSpy("0", taskManager)

        // Act
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
        taskManager.addTaskProvider(taskProvider)
        taskManager.addTask(task1)
        taskManager.addTask(task2)
        taskProvider.addTask(task3)

        // Verify
        assertEquals(3, taskManager.getTaskCount())
    }

    fun getLoadingTaskCount() {
        // Arrange
        setCanAddMoreTasks(taskManager)
        val task1 = TestTasks.createTaskMock("taskId1")
        val task2 = TestTasks.createTaskMock("taskId2")
        val task3 = TestTasks.createTaskMock("taskId3")

        // Act
        taskManager.addTask(task1)
        taskManager.addTask(task2)
        taskManager.addTask(task3)

        // Verify
        assertEquals(3, taskManager.getLoadingTaskCount())
    }

    fun getTaskFromProvider() {
        // Arrange
        val taskProvider1 = createTaskProviderSpy("provider1", taskManager, 20)
        val taskProvider2 = createTaskProviderSpy("provider2", taskManager, 10)
        val task = TestTasks.createTaskMock("task1")

        // Act
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
        assertEquals(3, taskManager.taskProviders.size)
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
        assertEquals(4, taskManager.taskProviders.size)
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
        assertEquals(2, taskManager.taskProviders.size)
        assertEquals(taskProvider2, taskManager.getTaskProvider("provider1"))
    }

    fun checkDefaultWaitingTaskProvider() {
        // Verify
        assertEquals(1, taskManager.taskProviders.size)
        assertNotNull(taskManager.taskProviders.get(0).taskProviderId)
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
        assertEquals(2, taskManager.taskProviders.size)
        assertNull(taskManager.getTaskProvider("provider1"))
        assertNotNull(taskManager.getTaskProvider("provider2"))
    }

    fun removeTaskProviderWithWaitingTasks() {
        // Arrange
        val listener = mock<Task.Callback>()
        val task = TestTasks.createTestTaskSpy("taskId")
        task.finishCallback = listener

        val taskProvider = createTaskProviderMock("provider", taskManager)
        `when`(taskProvider.getTasks()).doReturn(listOf(task))
        `when`(taskProvider.takeTopTask()).doReturn(task)
        `when`(taskProvider.getTopTask()).doReturn(task)
        `when`(task.canBeCancelledImmediately()).thenReturn(true)

        // Act
        taskManager.addTaskProvider(taskProvider)
        taskManager.removeTaskProvider(taskProvider)

        // Verify
        assertEquals(Task.Status.Cancelled, task.taskStatus)
        verify(listener).onCompleted(true)
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

    fun startImmediately() {
        // Arrange
        this.controller?.pauseTaskRunning()
        val task = TestTask()
        val listener = mock<TaskManager.Listener>()

        // Act
        taskManager.addListener(listener)
        taskManager.startImmediately(task)

        // Verify
        assertEquals(Task.Status.Started, task.taskStatus)
        verify(listener, never()).onTaskAdded(taskManager, task, false)
        verify(listener, never()).onTaskRemoved(taskManager, task, false)
        verify(listener).onTaskAdded(taskManager, task, true)
    }

    fun startImmediatelySkipPolicy() {
        // Arrange
        setCanAddMoreTasks(taskManager)
        this.controller?.pauseTaskRunning()
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
        setCanAddMoreTasks(taskManager)
        this.controller?.pauseTaskRunning()

        val task1 = TestTasks.createTestTaskSpy("taskId")
        val task2 = TestTasks.createTestTaskSpy("taskId")

        val callback1 = mock<Task.Callback>()
        val callback2 = mock<Task.Callback>()

        task1.finishCallback = callback1
        task2.finishCallback = callback2

        val listener = mock<TaskManager.Listener>()

        // Act
        taskManager.addListener(listener)
        taskManager.addTask(task1)
        taskManager.startImmediately(task2)

        this.controller?.resumeTaskRunning()

        assertEquals(Task.Status.Completed, task1.taskStatus)
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
        this.controller?.pauseTaskRunning()
        val task = TestTask()
        val callback = mock<Task.Callback>()
        task.finishCallback = callback

        val listener = mock<TaskManager.Listener>()

        // Act
        taskManager.addListener(listener)
        taskManager.startImmediately(task)
        task.finish()
        this.controller?.resumeTaskRunning()

        // Verify
        assertEquals(Task.Status.Completed, task.taskStatus)
        verify(listener, never()).onTaskAdded(taskManager, task, false)
        verify(listener, never()).onTaskRemoved(taskManager, task, false)
        verify(listener).onTaskAdded(taskManager, task, true)
        verify(listener).onTaskRemoved(taskManager, task, true)
        verify(callback).onCompleted(false)
        assertEquals(0, taskManager.getTaskCount())
    }

    fun addTaskSetTaskToWaiting() {
        // Arrange
        val task = TestTask()
        val listener = mock<TaskManager.Listener>()

        // Act
        taskManager.addListener(listener)
        taskManager.addTask(task)

        // Verify
        assertEquals(Task.Status.Waiting, task.taskStatus)
        verify(listener).onTaskAdded(taskManager, task, false)

        assertEquals(taskManager.getTaskCount(), 1)
        assertTrue(taskManager.getTasks().contains(task))
    }

    fun addTaskStartsTask() {
        // Arrange
        setCanAddMoreTasks(taskManager)
        controller?.pauseTaskRunning()
        val task = TestTask()
        val listener = mock<TaskManager.Listener>()

        // Act
        taskManager.addListener(listener)
        taskManager.addTask(task)

        // Verify
        assertEquals(Task.Status.Started, task.taskStatus)
        //verify(taskPrivate, atLeast(1)).taskStatus = Task.Status.Completed
        verify(listener).onTaskAdded(taskManager, task, false)
        verify(listener).onTaskAdded(taskManager, task, true)

        assertEquals(taskManager.getTaskCount(), 1)
        assertTrue(taskManager.getTasks().contains(task))
    }

    fun addStartedTask() {
        // Arrange
        val task = TestTasks.createTaskMock(null, Task.Status.Started)
        val listener = mock<TaskManager.Listener>()

        // Act
        taskManager.addListener(listener)
        taskManager.addTask(task)

        // Verify
        assertEquals(Task.Status.Started, task.taskStatus)
        verify(task, never()).addTaskStatusListener(taskManager)
        verify(listener, never()).onTaskAdded(taskManager, task, true)
        verify(listener, never()).onTaskAdded(taskManager, task, false)

        assertEquals(taskManager.getTaskCount(), 0)
        assertFalse(taskManager.getTasks().contains(task))
    }

    fun addTaskTwiceWithSameIdWithSkipPolicyAtStart() {
        // Arrange
        setCanAddMoreTasks(taskManager)
        this.controller?.pauseTaskRunning()
        val listener = mock<TaskManager.Listener>()

        val task1 = TestTasks.createTestTaskSpy("taskId")
        val task2 = TestTasks.createTestTaskSpy("taskId")
        task2.loadPolicy = Task.LoadPolicy.SkipIfAlreadyAdded

        // Act
        taskManager.addListener(listener)
        taskManager.addTask(task1)
        taskManager.addTask(task2)

        // Verify
        assertEquals(Task.Status.Started, task1.taskStatus)
        verify(listener).onTaskAdded(taskManager, task1, true)
        verify(listener).onTaskRemoved(taskManager, task1, false)
        verify(listener).onTaskAdded(taskManager, task1, false)

        assertEquals(Task.Status.Cancelled, task2.taskStatus)
        verify(listener).onTaskAdded(taskManager, task2, false)
        verify(listener, never()).onTaskAdded(taskManager, task2, true)
        verify(listener).onTaskRemoved(taskManager, task2, false)

        assertEquals(taskManager.getTaskCount(), 1)
        assertTrue(taskManager.getTasks().contains(task1))
    }

    fun addTaskTwiceWithSameIdWithSkipPolicyAtEnd() {
        // Arrange
        setCanAddMoreTasks(taskManager)
        this.controller?.pauseTaskRunning()
        val listener = mock<TaskManager.Listener>()

        val task1 = TestTasks.createTestTaskSpy("taskId")
        val task2 = TestTasks.createTestTaskSpy("taskId")
        task2.loadPolicy = Task.LoadPolicy.SkipIfAlreadyAdded

        // Act
        taskManager.addListener(listener)
        taskManager.addTask(task1)
        taskManager.addTask(task2)
        controller?.resumeTaskRunning()

        // Verify
        assertEquals(Task.Status.Completed, task1.taskStatus)
        assertEquals(Task.Status.Cancelled, task2.taskStatus)

        assertEquals(taskManager.getTaskCount(), 0)
    }

    fun addTaskTwiceWithSameIdWithCancelPolicyAtStart() {
        // Arrange
        setCanAddMoreTasks(taskManager)
        this.controller?.pauseTaskRunning()
        val task1 = TestTasks.createTestTaskSpy("taskId")
        `when`(task1.canBeCancelledImmediately()).thenReturn(true)

        val task2 = TestTasks.createTestTaskSpy("taskId")
        task2.loadPolicy = Task.LoadPolicy.CancelPreviouslyAdded

        // Act
        taskManager.addTask(task1)
        taskManager.addTask(task2)

        // Verify
        assertEquals(Task.Status.Cancelled, task1.taskStatus)
        assertTrue(task1.private.needCancelTask)

        assertEquals(Task.Status.Started, task2.taskStatus)
        assertFalse(task2.private.needCancelTask)

        assertEquals(1, taskManager.getTaskCount())
        assertTrue(taskManager.getTasks().contains(task2))
    }

    fun addTaskTwiceWithSameIdWithCancelPolicyAtEnd() {
        // Arrange
        setCanAddMoreTasks(taskManager)
        this.controller?.pauseTaskRunning()
        val task1 = TestTasks.createTestTaskSpy("taskId")
        `when`(task1.canBeCancelledImmediately()).thenReturn(true)

        val task2 = TestTasks.createTestTaskSpy("taskId")
        task2.loadPolicy = Task.LoadPolicy.CancelPreviouslyAdded

        // Act
        taskManager.addTask(task1)
        taskManager.addTask(task2)
        controller?.resumeTaskRunning()

        // Verify
        assertEquals(Task.Status.Cancelled, task1.taskStatus)
        assertEquals(Task.Status.Completed, task2.taskStatus)

        assertEquals(0, taskManager.getTaskCount())
    }

    fun addTaskTwiceWithSameIdWithAddDependencyPolicyAtStart() {
        // Arrange
        setCanAddMoreTasks(taskManager)
        this.controller?.pauseTaskRunning()
        val task1 = TestTasks.createTestTaskSpy("taskId")
        val task2 = TestTasks.createTestTaskSpy("taskId")
        task2.loadPolicy = Task.LoadPolicy.AddDependencyIfAlreadyAdded

        // Act
        taskManager.addTask(task1)
        taskManager.addTask(task2)

        // Verify
        assertEquals(Task.Status.Started, task1.taskStatus)
        assertFalse(task1.private.needCancelTask)

        assertEquals(Task.Status.Waiting, task2.taskStatus)
        assertFalse(task2.private.needCancelTask)
        verify(task2).addTaskDependency(task1)

        assertEquals(2, taskManager.getTaskCount())
        assertTrue(taskManager.getTasks().contains(task1))
        assertTrue(taskManager.getTasks().contains(task2))
    }

    fun addTaskTwiceWithSameIdWithAddDependencyPolicyAtEnd() {
        // Arrange
        setCanAddMoreTasks(taskManager)
        this.controller?.pauseTaskRunning()
        val task1 = TestTasks.createTestTaskSpy("taskId")
        val task2 = TestTasks.createTestTaskSpy("taskId")
        task2.loadPolicy = Task.LoadPolicy.AddDependencyIfAlreadyAdded

        // Act
        taskManager.addTask(task1)
        taskManager.addTask(task2)
        this.controller?.resumeTaskRunning()

        // Verify
        assertEquals(Task.Status.Completed, task1.taskStatus)
        assertEquals(Task.Status.Completed, task2.taskStatus)

        assertEquals(0, taskManager.getTaskCount())
    }

    fun addTaskTwiceWithSameIdWithCompletePolicyAtStart() {
        // Arrange
        setCanAddMoreTasks(taskManager)
        this.controller?.pauseTaskRunning()
        val task1 = TestTasks.createTestTaskSpy("taskId")
        val task2 = TestTasks.createTestTaskSpy("taskId")
        task2.loadPolicy = Task.LoadPolicy.CompleteWhenAlreadyAddedCompletes

        // Act
        taskManager.addTask(task1)
        taskManager.addTask(task2)

        // Verify
        assertEquals(Task.Status.Started, task1.taskStatus)
        assertFalse(task1.private.needCancelTask)

        assertEquals(Task.Status.Blocked, task2.taskStatus)
        assertFalse(task2.private.needCancelTask)
        verify(task2).addTaskDependency(task1)

        assertEquals(2, taskManager.getTaskCount())
        assertTrue(taskManager.getTasks().contains(task1))
        assertTrue(taskManager.getTasks().contains(task2))
    }

    fun addTaskTwiceWithSameIdWithCompletePolicyAtEnd() {
        // Arrange
        setCanAddMoreTasks(taskManager)
        this.controller?.pauseTaskRunning()
        val task1 = TestTasks.createTestTaskSpy("taskId")
        val task2 = TestTasks.createTestTaskSpy("taskId")
        task2.loadPolicy = Task.LoadPolicy.CompleteWhenAlreadyAddedCompletes

        // Act
        taskManager.addTask(task1)
        taskManager.addTask(task2)
        controller?.resumeTaskRunning()

        // Verify
        assertEquals(Task.Status.Completed, task1.taskStatus)
        assertEquals(Task.Status.Completed, task2.taskStatus)

        assertEquals(0, taskManager.getTaskCount())
    }

    fun addTaskTwiceWithTheSameTask() {
        // Arrange
        val task = TestTasks.createTestTaskSpy("taskId")

        // Act
        taskManager.addTask(task)
        taskManager.addTask(task)

        // Assert
        assertEquals(1, taskManager.getTaskCount())
    }

    fun taskCallbackCalled() {
        // Arrange
        setCanAddMoreTasks(taskManager)
        this.controller?.pauseTaskRunning()
        val listener = mock<TaskManager.Listener>()

        val task = TestTasks.createTestTaskSpy("taskId")
        val callback = mock<Task.Callback>()
        task.finishCallback = callback

        taskManager.addListener(listener)

        // Act
        taskManager.addTask(task)
        task.finish()
        this.controller?.resumeTaskRunning()

        // Verify
        assertEquals(Task.Status.Completed, task.taskStatus)
        verify(listener).onTaskRemoved(taskManager, task, true)
        verify(callback).onCompleted(false)
    }

    fun taskWithCancelledImmediatelyCallbackCalledAfterCancel() {
        // Arrange
        setCanAddMoreTasks(taskManager)
        this.controller?.pauseTaskRunning()
        val listener = mock<TaskManager.Listener>()

        val task = TestTasks.createTestTaskSpy("taskId")
        `when`(task.canBeCancelledImmediately()).thenReturn(true)

        val callback = mock<Task.Callback>()
        task.finishCallback = callback

        taskManager.addListener(listener)

        // Act
        taskManager.addTask(task)
        taskManager.cancel(task, null)
        task.finish()
        this.controller?.resumeTaskRunning()

        // Verify
        assertEquals(Task.Status.Cancelled, task.taskStatus)
        verify(listener).onTaskRemoved(taskManager, task, true)
        verify(callback).onCompleted(true)
    }

    fun addStateListener() {
        // Arrange
        setCanAddMoreTasks(taskManager)
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
        setCanAddMoreTasks(taskManager)
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
        setCanAddMoreTasks(taskManager)
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
        setCanAddMoreTasks(taskManager)
        val testTask = TestTask()

        // Act
        taskManager.addTask(testTask)

        // Verify
        assertEquals(0, taskManager.getTaskCount())
    }

    fun setGetScope() {
        // Arrange
        val threadRunner = ScopeThreadRunner(CoroutineScope(Dispatchers.Main), "testRunner2")
        val taskProvider = createTaskProviderMock("0", taskManager)

        // Act
        taskManager.addTaskProvider(taskProvider)
        taskManager.threadRunner = threadRunner

        // Verify
        verify(taskProvider).threadRunner = threadRunner
        assertEquals(threadRunner, taskManager.threadRunner)
    }

    fun addTaskFromPoolWhenCanLoad() {
        // Arrange
        setCanAddMoreTasks(taskManager)
        this.controller?.pauseTaskRunning()
        val taskProvider = createTaskProviderMock("0", taskManager)
        val taskCallback = mock<Task.Callback>()
        val testTask = TestTasks.createTestTaskSpy("taskId")
        testTask.finishCallback = taskCallback

        `when`(taskProvider.getTopTask()).doReturn(testTask)
        `when`(taskProvider.takeTopTask()).doReturn(testTask)

        // Act
        taskManager.addTaskProvider(taskProvider)
        taskManager.onTaskAdded(taskProvider, testTask)

        // Verify
        assertEquals(Task.Status.Started, testTask.taskStatus)
    }

    fun addTaskFromPoolWhenMaxLoadingTasksIsNotEnough() {
        // Arrange
        val taskProvider = createTaskProviderMock("0", taskManager)
        val taskCallback = mock<Task.Callback>()
        val testTask = TestTasks.createTestTaskSpy("taskId")
        testTask.finishCallback = taskCallback

        `when`(taskProvider.getTopTask()).doReturn(testTask)
        `when`(taskProvider.takeTopTask()).doReturn(testTask)

        // Act
        taskManager.addTaskProvider(taskProvider)
        taskManager.onTaskAdded(taskProvider, testTask)

        // Verify
        assertTrue(testTask.isReadyToStart())
    }

    fun cancelWaitingTaskFromPool() {
        // Arrange
        val taskProvider = createTaskProviderMock("0", taskManager)
        val taskCallback = mock<Task.Callback>()
        val testTask = TestTasks.createTestTaskSpy("taskId")
        testTask.finishCallback = taskCallback
        val cancelInfo = "info"

        // Act
        taskManager.addTaskProvider(taskProvider)
        taskManager.onTaskCancelled(taskProvider, testTask, cancelInfo)

        // Verify
        assertEquals(Task.Status.Cancelled, testTask.taskStatus)
        assertEquals(cancelInfo, testTask.cancellationInfo)
        verify(taskCallback).onCompleted(true)
    }

    fun cancelStartedTaskFromPool() {
        // Arrange
        setCanAddMoreTasks(taskManager)
        this.controller?.pauseTaskRunning()
        val taskProvider = createTaskProviderMock("0", taskManager)
        val taskCallback = mock<Task.Callback>()
        val testTask = TestTasks.createTestTaskSpy("taskId")
        testTask.finishCallback = taskCallback
        val cancelInfo = "info"

        `when`(taskProvider.getTopTask()).doReturn(testTask)
        `when`(taskProvider.takeTopTask()).doReturn(testTask)
        `when`(testTask.canBeCancelledImmediately()).thenReturn(true)

        // Act
        taskManager.addTaskProvider(taskProvider)
        taskManager.onTaskAdded(taskProvider, testTask)
        taskManager.onTaskCancelled(taskProvider, testTask, cancelInfo)

        // Verify
        assertEquals(Task.Status.Cancelled, testTask.taskStatus)
        assertEquals(cancelInfo, testTask.cancellationInfo)
        verify(taskCallback).onCompleted(true)
    }

    // Tools

    // Simulate the situation when a task coordinator allows running more tasks
    private fun setCanAddMoreTasks(taskManager: TaskManager) {
        (taskManager.taskManagerCoordinator as TestTaskManagerCoordinator).canAddMoreTasks = true
    }

    private fun createTaskProviderSpy(id: String, taskManager: TaskManager): TaskProvider {
        val taskProvider = TestTaskProvider(taskManager.threadRunner, id)
        return spy(taskProvider)
    }

    private fun createTaskProviderMock(id: String, taskManager: TaskManager, priority: Int = 0): TaskProvider {
        val provider = mock<TaskProvider>()
        `when`(provider.taskProviderId).thenReturn(id)
        `when`(provider.threadRunner).thenReturn(taskManager.threadRunner)
        `when`(provider.priority).thenReturn(priority)
        return provider
    }

    private fun createTaskProviderSpy(id: String, taskManager: TaskManager, priority: Int): TaskProvider {
        val provider = PriorityTaskProvider(taskManager.threadRunner, id)
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

    interface TaskManagerController {
        fun pauseTaskRunning() // Simulate the situation when a task is started and is executing
        fun resumeTaskRunning()
    }
}
