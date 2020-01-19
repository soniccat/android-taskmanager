package com.example.alexeyglushkov.taskmanager.task

import com.example.alexeyglushkov.taskmanager.pool.ListTaskPool

import org.junit.Before
import org.junit.Test

/**
 * Created by alexeyglushkov on 08.08.15.
 */

class SimpleTaskPoolBaseTest : TaskPoolTestSet() {
    @Before
    @Throws(Exception::class)
    fun setUp() {
        this@SimpleTaskPoolBaseTest.before(ListTaskPool(TestTasks.createTestScopeThreadRunner()))
    }

    @Test
    fun testSetGetHandler() {
        this@SimpleTaskPoolBaseTest.setGetHandler()
    }

    @Test
    fun testAddTask() {
        this@SimpleTaskPoolBaseTest.addTask()
    }

    @Test
    fun testRemoveTask() {
        this@SimpleTaskPoolBaseTest.removeTask()
    }

    @Test
    fun testRemoveUnknownTask() {
        this@SimpleTaskPoolBaseTest.removeUnknownTask()
    }

    @Test
    fun testGetTask() {
        this@SimpleTaskPoolBaseTest.getTask()
    }

    @Test
    fun testGetUnknownTask() {
        this@SimpleTaskPoolBaseTest.getUnknownTask()
    }

    @Test
    fun testGetTaskCount() {
        this@SimpleTaskPoolBaseTest.getTaskCount()
    }

    @Test
    fun testGetTaskCount2() {
        this@SimpleTaskPoolBaseTest.getTaskCount2()
    }

    @Test
    fun testSetGetUserData() {
        this@SimpleTaskPoolBaseTest.setGetUserData()
    }

    @Test
    fun testAddStateListener() {
        this@SimpleTaskPoolBaseTest.addStateListener()
    }

    @Test
    fun testRemoveStateListener() {
        this@SimpleTaskPoolBaseTest.removeStateListener()
    }

    @Test
    fun testCheckTaskRemovingAfterFinishing() {
        this@SimpleTaskPoolBaseTest.checkTaskRemovingAfterFinishing()
    }

    @Test
    fun testCancelTask() {
        this@SimpleTaskPoolBaseTest.cancelTask()
    }
}
