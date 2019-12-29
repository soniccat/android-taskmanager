package com.example.alexeyglushkov.taskmanager.task

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineExceptionHandler
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
    fun before() {
        val coordinator: TaskManagerCoordinator = TestTaskManagerCoordinator()
        val exceptionHandler = TestCoroutineExceptionHandler()
        val scope = TestCoroutineScope(exceptionHandler)

        val taskScopeDispatcher = TestCoroutineDispatcher()
        val taskScope = TestCoroutineScope(taskScopeDispatcher)
        taskManager = SimpleTaskManager(coordinator, scope, taskScope)
        poolTestSet = TaskPoolTestSet()
        taskManagerTestSet = TaskManagerTestSet()
        taskManagerTestSet.controller = object : TaskManagerTestSet.TaskManagerController {
            override fun pauseTaskRunning() {
                taskScopeDispatcher.pauseDispatcher()
            }

            override fun resumeTaskRunning() {
                taskScopeDispatcher.resumeDispatcher()
            }
        }

        poolTestSet.before(taskManager)
        taskManagerTestSet.before(taskManager)
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
    fun testTaskWithCancelledImmediatelyCallbackCalledAfterCancel() {
        taskManagerTestSet.taskWithCancelledImmediatelyCallbackCalledAfterCancel()
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
    fun checkDefaultWaitingTaskProvider() {
        taskManagerTestSet.checkDefaultWaitingTaskProvider()
    }

    @Test
    fun testRemoveTaskProvider() {
        taskManagerTestSet.removeTaskProvider()
    }

    @Test
    fun testRemoveTaskProviderWithWaitingTasks() {
        taskManagerTestSet.removeTaskProviderWithWaitingTasks()
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
    fun testCancelWaitingTaskFromPool() {
        taskManagerTestSet.cancelWaitingTaskFromPool()
    }

    @Test
    fun testAddTaskFromPoolWhenMaxLoadingTasksIsEnough() {
        taskManagerTestSet.addTaskFromPoolWhenCanLoad()
    }

    @Test
    fun testAddTaskFromPoolWhenMaxLoadingTasksIsNotEnough() {
        taskManagerTestSet.addTaskFromPoolWhenMaxLoadingTasksIsNotEnough()
    }

    @Test
    fun testCancelStartedTaskFromPool() {
        taskManagerTestSet.cancelStartedTaskFromPool()
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
