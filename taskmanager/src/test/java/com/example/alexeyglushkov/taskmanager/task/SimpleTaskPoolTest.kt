package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler
import android.os.Looper

import org.junit.Before
import org.junit.Test

/**
 * Created by alexeyglushkov on 08.08.15.
 */

class SimpleTaskPoolTest : TaskPoolTestSet() {
    @Before
    @Throws(Exception::class)
    fun setUp() {
        this@SimpleTaskPoolTest.before(SimpleTaskPool(Handler(Looper.myLooper())))
    }

    @Test
    fun testSetGetHandler() {
        this@SimpleTaskPoolTest.setGetHandler()
    }

    @Test
    fun testAddTask() {
        this@SimpleTaskPoolTest.addTask()
    }

    @Test
    fun testAddStartedTask() {
        this@SimpleTaskPoolTest.addStartedTask()
    }

    @Test
    fun testRemoveTask() {
        this@SimpleTaskPoolTest.removeTask()
    }

    @Test
    fun testRemoveUnknownTask() {
        this@SimpleTaskPoolTest.removeUnknownTask()
    }

    @Test
    fun testGetTask() {
        this@SimpleTaskPoolTest.getTask()
    }

    @Test
    fun testGetUnknownTask() {
        this@SimpleTaskPoolTest.getUnknownTask()
    }

    @Test
    fun testGetTaskCount() {
        this@SimpleTaskPoolTest.getTaskCount()
    }

    @Test
    fun testGetTaskCount2() {
        this@SimpleTaskPoolTest.getTaskCount2()
    }

    @Test
    fun testSetGetUserData() {
        this@SimpleTaskPoolTest.setGetUserData()
    }

    @Test
    fun testAddStateListener() {
        this@SimpleTaskPoolTest.addStateListener()
    }

    @Test
    fun testRemoveStateListener() {
        this@SimpleTaskPoolTest.removeStateListener()
    }

    @Test
    fun testChangeTaskStatus() {
        this@SimpleTaskPoolTest.changeTaskStatus()
    }

    @Test
    fun testCheckTaskRemovingAfterFinishing() {
        this@SimpleTaskPoolTest.checkTaskRemovingAfterFinishing()
    }
}
