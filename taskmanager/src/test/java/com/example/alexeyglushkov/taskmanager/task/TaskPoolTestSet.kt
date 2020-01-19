package com.example.alexeyglushkov.taskmanager.task

import android.os.HandlerThread
import com.example.alexeyglushkov.taskmanager.pool.TaskPool
import com.example.alexeyglushkov.taskmanager.runners.ScopeThreadRunner
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import org.junit.Assert.*

open class TaskPoolTestSet {

    protected lateinit var taskPool: TaskPool

    fun before(taskPool: TaskPool) {
        this.taskPool = taskPool
    }

    fun setGetHandler() {
        // Arrange
        val handlerThread = HandlerThread("HandlerThread")
        handlerThread.start()
        val threadRunner = ScopeThreadRunner(CoroutineScope(Dispatchers.Main), "TestRunner3")

        // Act
        taskPool.threadRunner = threadRunner

        // Verify
        assertEquals(threadRunner, taskPool.threadRunner)
    }

    fun addTask() {
        // Arrange
        val task = TestTasks.createTaskMock()
        val taskPrivate = task.private
        val listener = mock<TaskPool.Listener>()

        // Act
        taskPool.addListener(listener)
        taskPool.addTask(task)

        // Verify
        verify(listener).onTaskAdded(taskPool, task)

        assertEquals(taskPool.getTaskCount(), 1)
        assertTrue(taskPool.getTasks().contains(task))
    }

    fun addTaskCallsAddStatusListener() {
        // Arrange
        val task = spy(TestTask())

        // Act
        taskPool.addTask(task)

        // Verify
        verify<Task>(task).addTaskStatusListener(taskPool)
    }

    fun removeTaskCallsRemoveTaskStatusListener() {
        // Arrange
        val task = spy(TestTask())

        // Act
        taskPool.addTask(task)
        taskPool.removeTask(task)

        // Verify
        verify<Task>(task).removeTaskStatusListener(taskPool)
    }

    fun removeTask() {
        // Arrange
        val task = TestTasks.createTaskMock("taskId", Task.Status.NotStarted)
        val listener = mock<TaskPool.Listener>()

        // Act
        taskPool.addListener(listener)
        taskPool.addTask(task)
        taskPool.removeTask(task)

        // Verify
        verify(listener).onTaskAdded(taskPool, task)
        verify(listener).onTaskRemoved(taskPool, task)

        assertEquals(0, taskPool.getTaskCount())
    }

    fun removeUnknownTask() {
        // Arrange
        val task1 = TestTasks.createTaskMock("taskId", Task.Status.NotStarted)
        val task2 = TestTasks.createTaskMock("taskId", Task.Status.NotStarted)
        val listener = mock<TaskPool.Listener>()

        // Act
        taskPool.addListener(listener)
        taskPool.addTask(task1)
        taskPool.removeTask(task2)

        // Verify
        verify(listener).onTaskAdded(taskPool, task1)
        verify(listener, never()).onTaskRemoved(taskPool, task1)
        verify(listener, never()).onTaskAdded(taskPool, task2)
        verify(listener, never()).onTaskRemoved(taskPool, task2)

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
        val listener1 = mock<TaskPool.Listener>()
        val listener2 = mock<TaskPool.Listener>()

        // Act
        taskPool.addListener(listener1)
        taskPool.addListener(listener2)
        taskPool.addTask(task)

        // Verify
        verify(listener1).onTaskAdded(taskPool, task)
        verify(listener2).onTaskAdded(taskPool, task)
    }

    fun removeStateListener() {
        // Arrange
        val task = TestTasks.createTaskMock("taskId", Task.Status.NotStarted)
        val listener1 = mock<TaskPool.Listener>()
        val listener2 = mock<TaskPool.Listener>()

        // Act
        taskPool.addListener(listener1)
        taskPool.addListener(listener2)
        taskPool.removeListener(listener1)
        taskPool.removeListener(listener2)
        taskPool.addTask(task)

        // Verify
        verify(listener1, never()).onTaskAdded(taskPool, task)
        verify(listener2, never()).onTaskAdded(taskPool, task)
    }

    fun checkTaskRemovingAfterFinishing() {
        // Arrange
        val testTask = TestTask()
        val taskPoolMock = spy(taskPool)

        // Act
        taskPoolMock.addTask(testTask)
        testTask.private.taskStatus = Task.Status.Completed

        // Verify
        assertEquals(0, taskPool.getTaskCount())
    }

    fun cancelTask() {
        // Arrange
        val testTask = TestTask()
        val info = "info"
        val listener = mock<TaskPool.Listener>()

        // Act
        taskPool.addListener(listener)
        taskPool.addTask(testTask)
        taskPool.cancelTask(testTask, info)

        // Verify
        verify(listener).onTaskCancelled(taskPool, testTask, info)
    }
}