package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler
import android.os.Looper

import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull

/**
 * Created by alexeyglushkov on 13.08.16.
 */

class StackTaskProviderWithDependentTasksTest {
    private lateinit var poolTest: TaskPoolTestSet
    private lateinit var providerTest: TaskProviderTestSet
    private lateinit var taskProvider: StackTaskProvider

    @Before
    @Throws(Exception::class)
    fun setUp() {
        taskProvider = StackTaskProvider(true, Handler(Looper.myLooper()), "TestId")

        poolTest = TaskPoolTestSet()
        providerTest = TaskProviderTestSet()

        poolTest.before(taskProvider)
        providerTest.before(taskProvider)
    }

    // StackTaskProviderTests

    @Test
    fun testAddedIsTriggeredWhenTaskIsFinished() {
        // Arrange
        val testTask1 = TestTask()
        val testTask2 = TestTask()
        val providerMock = Mockito.spy(taskProvider)
        val listener = Mockito.mock(TaskPool.Listener::class.java)

        // Act
        providerMock.addListener(listener)
        providerMock.addTask(testTask1)
        providerMock.takeTopTask(null)

        providerMock.addTask(testTask2)

        // Verify
        Mockito.verify<TaskPool.Listener>(listener, Mockito.never()).onTaskAdded(providerMock, testTask2)
        testTask1.private.taskStatus = Task.Status.Finished

        Mockito.verify<TaskPool.Listener>(listener).onTaskAdded(providerMock, testTask2)
        assertEquals(1, taskProvider.getTaskCount())
    }

    @Test
    fun testTakeTopTaskIsEmptyIfBlocked() {
        // Arrange
        val testTask1 = TestTask()
        val testTask2 = TestTask()
        val providerMock = Mockito.spy(taskProvider)

        // Act
        providerMock.addTask(testTask1)
        val task1 = providerMock.takeTopTask(null)

        providerMock.addTask(testTask2)
        val task2 = providerMock.takeTopTask(null)

        // Verify
        assertEquals(testTask1, task1)
        assertNull(task2)
        testTask1.private.taskStatus = Task.Status.Finished

        assertEquals(1, taskProvider.getTaskCount())
    }

    @Test
    fun testGetTopTaskIsEmptyIfBlocked() {
        // Arrange
        val testTask1 = TestTask()
        val testTask2 = TestTask()
        val providerMock = Mockito.spy(taskProvider)

        // Act
        providerMock.addTask(testTask1)
        val task1 = providerMock.getTopTask(null)
        providerMock.takeTopTask(null)

        providerMock.addTask(testTask2)
        val task2 = providerMock.getTopTask(null)

        // Verify
        assertEquals(testTask1, task1)
        assertNull(task2)
        testTask1.private.taskStatus = Task.Status.Finished

        assertEquals(1, taskProvider.getTaskCount())
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
        poolTest.setGetHandler()
    }

    @Test
    fun testAddTask() {
        poolTest.addTask()
    }

    @Test
    fun testAddTaskAddStatusListener() {
        poolTest.addTaskAddStatusListener()
    }

    @Test
    fun testAddStartedTask() {
        poolTest.addStartedTask()
    }

    @Test
    fun testRemoveTask() {
        poolTest.removeTask()
    }

    @Test
    fun testRemoveUnknownTask() {
        poolTest.removeUnknownTask()
    }

    @Test
    fun testGetTask() {
        poolTest.getTask()
    }

    @Test
    fun testGetUnknownTask() {
        poolTest.getUnknownTask()
    }

    @Test
    fun testGetTaskCount() {
        poolTest.getTaskCount()
    }

    @Test
    fun testGetTaskCount2() {
        poolTest.getTaskCount2()
    }

    @Test
    fun testSetGetUserData() {
        poolTest.setGetUserData()
    }

    @Test
    fun testAddStateListener() {
        poolTest.addStateListener()
    }

    @Test
    fun testRemoveStateListener() {
        poolTest.removeStateListener()
    }

    @Test
    fun testChangeTaskStatus() {
        poolTest.changeTaskStatus()
    }

    @Test
    fun testCheckTaskRemovingAfterFinishing() {
        poolTest.checkTaskRemovingAfterFinishing()
    }
}
