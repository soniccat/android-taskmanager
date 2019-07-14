package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler
import android.os.HandlerThread

import org.mockito.Mockito

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue

open class TaskPoolTestSet {

    protected lateinit var taskPool: TaskPool

    fun before(taskPool: TaskPool) {
        this.taskPool = taskPool
    }

    fun setGetHandler() {
        // Arrange
        val handlerThread = HandlerThread("HandlerThread")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)

        // Act
        taskPool.handler = handler

        // Verify
        assertTrue(taskPool.handler != null)
        assertEquals(handler, taskPool.handler)
    }

    fun addTask() {
        // Arrange
        val task = TestTasks.createTaskMock()
        val taskPrivate = task.private
        val listener = Mockito.mock(TaskPool.Listener::class.java)

        // Act
        taskPool.addListener(listener)
        taskPool.addTask(task)

        // Verify
        Mockito.verify(taskPrivate).taskStatus = Task.Status.Waiting
        Mockito.verify<TaskPool.Listener>(listener).onTaskAdded(taskPool, task)

        assertEquals(taskPool.getTaskCount(), 1)
        assertTrue(taskPool.getTasks().contains(task))
    }

    fun addTaskAddStatusListener() {
        // Arrange
        val task = Mockito.spy(TestTask())

        // Act
        taskPool.addTask(task)

        // Verify
        Mockito.verify<Task>(task).addTaskStatusListener(taskPool)
    }

    fun addStartedTask() {
        // Arrange
        val task = TestTasks.createTaskMock(null, Task.Status.Started)
        val taskPrivate = task.private
        val listener = Mockito.mock(TaskPool.Listener::class.java)

        // Act
        taskPool.addListener(listener)
        taskPool.addTask(task)

        // Verify
        Mockito.verify(taskPrivate, Mockito.never()).taskStatus = Task.Status.Waiting
        Mockito.verify(task, Mockito.never()).addTaskStatusListener(taskPool)
        Mockito.verify<TaskPool.Listener>(listener, Mockito.never()).onTaskAdded(taskPool, task)

        assertEquals(taskPool.getTaskCount(), 0)
        assertFalse(taskPool.getTasks().contains(task))
    }

    fun removeTask() {
        // Arrange
        val task = TestTasks.createTaskMock("taskId", Task.Status.NotStarted)
        val listener = Mockito.mock(TaskPool.Listener::class.java)

        // Act
        taskPool.addListener(listener)
        taskPool.addTask(task)
        taskPool.removeTask(task)

        // Verify
        Mockito.verify<TaskPool.Listener>(listener).onTaskAdded(taskPool, task)
        Mockito.verify<TaskPool.Listener>(listener).onTaskRemoved(taskPool, task)

        assertEquals(0, taskPool.getTaskCount())
    }

    fun removeUnknownTask() {
        // Arrange
        val task1 = TestTasks.createTaskMock("taskId", Task.Status.NotStarted)
        val task2 = TestTasks.createTaskMock("taskId", Task.Status.NotStarted)
        val listener = Mockito.mock(TaskPool.Listener::class.java)

        // Act
        taskPool.addListener(listener)
        taskPool.addTask(task1)
        taskPool.removeTask(task2)

        // Verify
        Mockito.verify<TaskPool.Listener>(listener).onTaskAdded(taskPool, task1)
        Mockito.verify<TaskPool.Listener>(listener, Mockito.never()).onTaskRemoved(taskPool, task1)
        Mockito.verify<TaskPool.Listener>(listener, Mockito.never()).onTaskAdded(taskPool, task2)
        Mockito.verify<TaskPool.Listener>(listener, Mockito.never()).onTaskRemoved(taskPool, task2)

        assertEquals(1, taskPool.getTaskCount())
    }

    fun getTask() {
        // Arrange
        val task = TestTasks.createTaskMock("taskId", Task.Status.NotStarted)

        // Act
        taskPool.addTask(task)
        val returnedTask = taskPool.getTask("taskId")

        // Verify
        assertEquals(task, returnedTask)
    }

    fun getUnknownTask() {
        // Arrange
        val task = TestTasks.createTaskMock("taskId", Task.Status.NotStarted)

        // Act
        taskPool.addTask(task)
        val returnedTask = taskPool.getTask("taskId2")

        // Verify
        assertEquals(null, returnedTask)

    }

    fun getTaskCount() {
        // Arrange
        val task1 = TestTasks.createTaskMock("taskId1", Task.Status.NotStarted)
        val task2 = TestTasks.createTaskMock("taskId2", Task.Status.NotStarted)

        // Act
        taskPool.addTask(task1)
        taskPool.addTask(task2)

        // Verify
        assertEquals(2, taskPool.getTaskCount())
    }

    fun getTaskCount2() {
        // Arrange
        val task1 = TestTasks.createTaskMock("taskId1", Task.Status.NotStarted)
        val task2 = TestTasks.createTaskMock("taskId2", Task.Status.NotStarted)
        val task3 = TestTasks.createTaskMock("taskId3", Task.Status.NotStarted)

        // Act
        taskPool.addTask(task1)
        taskPool.addTask(task2)
        taskPool.addTask(task3)

        // Verify
        assertEquals(3, taskPool.getTaskCount())
    }

    fun setGetUserData() {
        // Arrange
        val data = "data"

        // Act
        taskPool.userData = data

        // Verify
        assertEquals(data, taskPool.userData as String)
    }

    fun addStateListener() {
        // Arrange
        val task = TestTasks.createTaskMock("taskId", Task.Status.NotStarted)
        val listener1 = Mockito.mock(TaskPool.Listener::class.java)
        val listener2 = Mockito.mock(TaskPool.Listener::class.java)

        // Act
        taskPool.addListener(listener1)
        taskPool.addListener(listener2)
        taskPool.addTask(task)

        // Verify
        Mockito.verify<TaskPool.Listener>(listener1).onTaskAdded(taskPool, task)
        Mockito.verify<TaskPool.Listener>(listener2).onTaskAdded(taskPool, task)
    }

    fun removeStateListener() {
        // Arrange
        val task = TestTasks.createTaskMock("taskId", Task.Status.NotStarted)
        val listener1 = Mockito.mock(TaskPool.Listener::class.java)
        val listener2 = Mockito.mock(TaskPool.Listener::class.java)

        // Act
        taskPool.addListener(listener1)
        taskPool.addListener(listener2)
        taskPool.removeListener(listener1)
        taskPool.removeListener(listener2)
        taskPool.addTask(task)

        // Verify
        Mockito.verify<TaskPool.Listener>(listener1, Mockito.never()).onTaskAdded(taskPool, task)
        Mockito.verify<TaskPool.Listener>(listener2, Mockito.never()).onTaskAdded(taskPool, task)
    }

    fun changeTaskStatus() {
        // Arrange
        val testTask = TestTask()
        val taskPoolMock = Mockito.spy(taskPool)

        // Act
        taskPoolMock.addTask(testTask)
        testTask.private.taskStatus = Task.Status.Started

        // Verify
        Mockito.verify(taskPoolMock).onTaskStatusChanged(testTask, Task.Status.Waiting, Task.Status.Started)

        assertEquals(1, taskPool.getTaskCount())
    }

    fun checkTaskRemovingAfterFinishing() {
        // Arrange
        val testTask = TestTask()
        val taskPoolMock = Mockito.spy(taskPool)

        // Act
        taskPoolMock.addTask(testTask)
        testTask.private.taskStatus = Task.Status.Finished

        // Verify
        assertEquals(0, taskPool.getTaskCount())
    }
}