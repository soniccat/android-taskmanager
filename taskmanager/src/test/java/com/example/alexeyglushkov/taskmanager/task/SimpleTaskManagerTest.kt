package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.test.TestCoroutineScope

import org.junit.Before
import org.junit.Test

/**
 * Created by alexeyglushkov on 23.08.15.
 */

class SimpleTaskManagerTest {

    private lateinit var poolTestSet: TaskPoolTestSet
    private lateinit var taskManagerTestSet: TaskManagerTestSet
    private lateinit var taskManager: TaskManager

    @Before
    @Throws(Exception::class)
    fun setUp() {
        taskManager = SimpleTaskManager(10, TestCoroutineScope())
        poolTestSet = TaskPoolTestSet()
        taskManagerTestSet = TaskManagerTestSet()

        poolTestSet.before(taskManager)
        taskManagerTestSet.before(taskManager)
    }

    @Test
    fun testSetMaxLoadingTasks() {
        taskManagerTestSet.setMaxLoadingTasks()
    }

    @Test
    fun testGetLoadingTaskCount() {
        taskManagerTestSet.getLoadingTaskCount()
    }

    @Test
    fun testAddTask() {
        taskManagerTestSet.addTask()
    }

    @Test
    fun testAddStartedTask() {
        taskManagerTestSet.addStartedTask()
    }

    @Test
    fun testAddTheSameTaskWithSkipPolicy() {
        taskManagerTestSet.addTheSameTaskWithSkipPolicy()
    }

    @Test
    fun testAddTheSameTaskWithCancelPolicy() {
        taskManagerTestSet.addTheSameTaskWithCancelPolicy()
    }

    @Test
    fun testTaskCallbackCalled() {
        taskManagerTestSet.taskCallbackCalled()
    }

    @Test
    fun testChangedTaskCallbackCalled() {
        taskManagerTestSet.changedTaskCallbackCalled()
    }

    @Test
    fun testTaskWithCancelledImmediatelyCallbackCalledAfterCancel() {
        taskManagerTestSet.taskWithCancelledImmediatelyCallbackCalledAfterCancel()
    }

    @Test
    fun testTaskWithCancelledImmediatelyAndChangedCallbackCalled() {
        taskManagerTestSet.taskWithCancelledImmediatelyAndChangedCallbackCalled()
    }

    @Test
    fun testAddStateListener() {
        taskManagerTestSet.addStateListener()
    }

    @Test
    fun testRemoveStateListener() {
        taskManagerTestSet.removeStateListener()
    }

    @Test
    fun testRemoveTask() {
        taskManagerTestSet.removeTask()
    }

    @Test
    fun testRemoveUnknownTask() {
        taskManagerTestSet.removeUnknownTask()
    }

    @Test
    fun testCheckTaskRemovingAfterFinishing() {
        taskManagerTestSet.checkTaskRemovingAfterFinishing()
    }

    @Test
    fun testGetTaskFromProvider() {
        taskManagerTestSet.getTaskFromProvider()
    }

    @Test
    fun testAddTaskProvider() {
        taskManagerTestSet.addTaskProvider()
    }

    @Test
    fun testAddTaskProvider2() {
        taskManagerTestSet.addTaskProvider2()
    }

    @Test
    fun testAddTaskProviderWithTheSameId() {
        taskManagerTestSet.addTaskProviderWithTheSameId()
    }

    @Test
    fun testRemoveTaskProvider() {
        taskManagerTestSet.removeTaskProvider()
    }

    @Test
    fun testSetTaskExecutor() {
        taskManagerTestSet.setTaskExecutor()
    }

    @Test
    fun testStartImmediately() {
        taskManagerTestSet.startImmediately()
    }

    @Test
    fun testStartImmediatelySkipPolicy() {
        taskManagerTestSet.startImmediatelySkipPolicy()
    }

    @Test
    fun testStartImmediatelySkipPolicyWithFinish() {
        taskManagerTestSet.startImmediatelySkipPolicyWithFinish()
    }

    @Test
    fun testStartImmediatelyFinish() {
        taskManagerTestSet.startImmediatelyFinish()
    }

    @Test
    fun testStartImmediatelyFinishWithChangedCallback() {
        taskManagerTestSet.startImmediatelyFinishWithChangedCallback()
    }

    @Test
    fun testStartImmediatelyCancelWithChangedCallback() {
        taskManagerTestSet.startImmediatelyCancelWithChangedCallback()
    }

    @Test
    fun testSetTaskProviderPriority() {
        taskManagerTestSet.setTaskProviderPriority()
    }

    @Test
    fun testSetGetScope() {
        taskManagerTestSet.setGetScope()
    }

    @Test
    fun testGetTaskCount() {
        taskManagerTestSet.getTaskCount()
    }

    @Test
    fun testGetTasks() {
        taskManagerTestSet.getTasks()
    }

    @Test
    fun testSetLimit() {
        taskManagerTestSet.setLimit()
    }

    @Test
    fun testSetLimitRemove() {
        taskManagerTestSet.setLimitRemove()
    }

    // PoolTests

    @Test
    fun testGetTask() {
        poolTestSet.getTask()
    }

    @Test
    fun testGetUnknownTask() {
        poolTestSet.getUnknownTask()
    }

    @Test
    fun testSetGetUserData() {
        poolTestSet.setGetUserData()
    }
}
