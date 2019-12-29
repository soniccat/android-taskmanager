package com.example.alexeyglushkov.taskmanager.task

import org.junit.Before
import org.junit.Test

/**
 * Created by alexeyglushkov on 13.08.16.
 */

open class StackTaskProviderTest {
    protected lateinit var poolTest: TaskPoolTestSet
    protected lateinit var providerTest: TaskProviderTestSet
    protected lateinit var taskProvider: TaskProvider

    @Before
    @Throws(Exception::class)
    fun setUp() {
        taskProvider = prepareTaskProvider()

        poolTest = TaskPoolTestSet()
        providerTest = TaskProviderTestSet()

        preparePoolTest()
        prepareProviderTest()
    }

    protected open fun prepareTaskProvider(): TaskProvider {
        return StackTaskProvider(false, "TestId", TestTasks.createTestScopeThreadRunner())
    }

    protected fun preparePoolTest() {
        poolTest.before(taskProvider)
    }

    protected fun prepareProviderTest() {
        providerTest.before(taskProvider)
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
    fun addTaskCallsAddStatusListener() {
        poolTest.addTaskCallsAddStatusListener()
    }

    @Test
    fun removeTaskCallsRemoveTaskStatusListener() {
        poolTest.removeTaskCallsRemoveTaskStatusListener()
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

    @Test
    fun testCancelTask() {
        poolTest.cancelTask()
    }
}
