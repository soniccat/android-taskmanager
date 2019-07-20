package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler
import android.os.Looper
import com.nhaarman.mockitokotlin2.mock

import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

import java.util.Arrays

/**
 * Created by alexeyglushkov on 09.08.15.
 */

open class PriorityTaskProviderTest {
    private lateinit var poolTestSet: TaskPoolTestSet
    private lateinit var providerTest: TaskProviderTestSet
    protected lateinit var taskProvider: TaskProvider

    protected open val priorityTaskProvider: PriorityTaskProvider
        get() = taskProvider as PriorityTaskProvider

    @Before
    @Throws(Exception::class)
    fun setUp() {
        taskProvider = prepareTaskProvider()

        poolTestSet = TaskPoolTestSet()
        providerTest = TaskProviderTestSet()

        poolTestSet.before(taskProvider)
        providerTest.before(taskProvider)
    }

    protected open fun prepareTaskProvider(): TaskProvider {
        return PriorityTaskProvider(Handler(Looper.myLooper()), "TestId")
    }

    // PriorityTaskProviderTests

    @Test
    fun testUpdatePriorities() {
        // Arrange
        val listener = mock<TaskPool.Listener>()

        taskProvider.addListener(listener)
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 1, 1))
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 2, 2))
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 3, 3))
        taskProvider.addTask(TestTasks.createTestTaskSpy("d", 2, 4))
        taskProvider.addTask(TestTasks.createTestTaskSpy("e", 1, 5))
        taskProvider.addTask(TestTasks.createTestTaskSpy("f", 3, 6))

        // Act
        priorityTaskProvider.updatePriorities(object : PriorityTaskProvider.PriorityProvider {
            override fun getPriority(task: Task): Int {
                return if (task.taskType == 2) {
                    2 * task.taskPriority
                } else task.taskPriority
            }
        })

        val task = taskProvider.getTopTask(null)

        // Verify
        assertEquals("d", task!!.taskId)
    }

    @Test
    fun testTopTaskWithPriorityWithoutFilter() {
        // Arrange
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 1, 1))
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 2, 2))
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 1, 3))
        taskProvider.addTask(TestTasks.createTestTaskSpy("d", 2, 4))
        taskProvider.addTask(TestTasks.createTestTaskSpy("e", 1, 5))
        taskProvider.addTask(TestTasks.createTestTaskSpy("f", 2, 6))

        // Act
        val task = taskProvider.getTopTask(null)

        // Verify
        assertEquals("f", task!!.taskId)
    }

    @Test
    fun testGetTopTaskWithPriorityWithFilter() {
        // Arrange
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 1, 1))
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 2, 2))
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 3, 3))
        taskProvider.addTask(TestTasks.createTestTaskSpy("d", 2, 4))
        taskProvider.addTask(TestTasks.createTestTaskSpy("e", 1, 5))
        taskProvider.addTask(TestTasks.createTestTaskSpy("f", 3, 6))

        // Act
        val task = taskProvider.getTopTask(Arrays.asList(*arrayOf(3)))

        // Verify
        assertEquals("e", task!!.taskId)
    }

    // ProviderTests

    @Test
    fun testGetTopTaskWithBlockedTask() {
        providerTest.getTopTaskWithBlockedTask()
    }

    @Test
    fun testTakeTopTaskWithBlockedTask() {
        providerTest.takeTopTaskWithBlockedTask()
    }

    @Test
    fun testSetProviderId() {
        providerTest.setProviderId()
    }

    @Test
    fun testSetPriority() {
        providerTest.setPriority()
    }

    @Test
    fun testGetTopTaskWithoutFilter() {
        providerTest.getTopTaskWithoutFilter()
    }

    @Test
    fun testGetTopTaskWithFilter() {
        providerTest.getTopTaskWithFilter()
    }

    @Test
    fun testTakeTopTaskWithFilter() {
        providerTest.takeTopTaskWithFilter()
    }

    @Test
    fun testRemoveTaskWithUnknownType() {
        providerTest.removeTaskWithUnknownType()
    }

    // PoolTests

    @Test
    fun testSetGetHandler() {
        poolTestSet.setGetHandler()
    }

    @Test
    fun testAddTask() {
        poolTestSet.addTask()
    }

    @Test
    fun testAddTaskAddStatusListener() {
        poolTestSet.addTaskAddStatusListener()
    }

    @Test
    fun testAddStartedTask() {
        poolTestSet.addStartedTask()
    }

    @Test
    fun testRemoveTask() {
        poolTestSet.removeTask()
    }

    @Test
    fun testRemoveUnknownTask() {
        poolTestSet.removeUnknownTask()
    }

    @Test
    fun testGetTask() {
        poolTestSet.getTask()
    }

    @Test
    fun testGetUnknownTask() {
        poolTestSet.getUnknownTask()
    }

    @Test
    fun testGetTaskCount() {
        poolTestSet.getTaskCount()
    }

    @Test
    fun testGetTaskCount2() {
        poolTestSet.getTaskCount2()
    }

    @Test
    fun testSetGetUserData() {
        poolTestSet.setGetUserData()
    }

    @Test
    fun testAddStateListener() {
        poolTestSet.addStateListener()
    }

    @Test
    fun testRemoveStateListener() {
        poolTestSet.removeStateListener()
    }

    @Test
    fun testChangeTaskStatus() {
        poolTestSet.changeTaskStatus()
    }

    @Test
    fun testCheckTaskRemovingAfterFinishing() {
        poolTestSet.checkTaskRemovingAfterFinishing()
    }
}