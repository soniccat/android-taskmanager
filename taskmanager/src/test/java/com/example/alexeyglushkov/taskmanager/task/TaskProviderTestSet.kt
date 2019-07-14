package com.example.alexeyglushkov.taskmanager.task

import org.mockito.Mockito

import java.util.Arrays

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull

/**
 * Created by alexeyglushkov on 15.08.15.
 */
class TaskProviderTestSet {

    protected lateinit var taskProvider: TaskProvider

    fun before(taskProvider: TaskProvider) {
        this.taskProvider = taskProvider
    }

    fun setProviderId() {
        // Act
        taskProvider.taskProviderId = "testId"

        // Verify
        assertEquals("testId", taskProvider.taskProviderId)
    }

    fun setPriority() {
        // Act
        taskProvider.priority = 12

        // Verify
        assertEquals(12, taskProvider.priority)
    }

    fun getTopTaskWithoutFilter() {

        // Arrange
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 1))
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 2))
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 1))
        taskProvider.addTask(TestTasks.createTestTaskSpy("d", 2))
        taskProvider.addTask(TestTasks.createTestTaskSpy("e", 1))
        taskProvider.addTask(TestTasks.createTestTaskSpy("f", 2))

        // Act
        val task = taskProvider.getTopTask(null)

        // Verify
        assertEquals("a", task!!.taskId)
    }

    fun getTopTaskWithFilter() {

        // Arrange
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 1))
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 2))
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 3))
        taskProvider.addTask(TestTasks.createTestTaskSpy("d", 2))
        taskProvider.addTask(TestTasks.createTestTaskSpy("e", 1))
        taskProvider.addTask(TestTasks.createTestTaskSpy("f", 3))

        // Act
        val task = taskProvider.getTopTask(Arrays.asList(*arrayOf(1)))

        // Verify
        assertEquals("b", task!!.taskId)
    }

    fun getTopTaskWithBlockedTask() {

        // Arrange
        val dTask = TestTasks.createTestTaskSpy("d", 0)
        val blockedTask = TestTasks.createTestTaskSpy("e", 0)
        blockedTask.addTaskDependency(dTask)

        // Act
        taskProvider.addTask(blockedTask)
        taskProvider.addTask(dTask)
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 0))
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 0))
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 0))

        val task = taskProvider.getTopTask(null)

        // Verify
        assertEquals("d", task!!.taskId)
    }

    fun takeTopTaskWithFilter() {
        // Arrange
        val listener = Mockito.mock<TaskPool.Listener>(TaskPool.Listener::class.java)

        taskProvider.addListener(listener)
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 1))
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 2))
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 3))
        taskProvider.addTask(TestTasks.createTestTaskSpy("d", 2))
        taskProvider.addTask(TestTasks.createTestTaskSpy("e", 1))
        taskProvider.addTask(TestTasks.createTestTaskSpy("f", 3))

        // Act
        val task = taskProvider.takeTopTask(Arrays.asList(*arrayOf(1)))

        // Verify
        Mockito.verify<TaskPool.Listener>(listener).onTaskRemoved(taskProvider, task!!)

        assertEquals("b", task.taskId)
        assertEquals(5, taskProvider.getTaskCount())
        assertEquals(null, taskProvider.getTask("b"))
    }

    fun takeTopTaskWithBlockedTask() {

        // Arrange
        val dTask = TestTasks.createTestTaskSpy("d", 0)
        val blockedTask = TestTasks.createTestTaskSpy("e", 0)
        blockedTask.addTaskDependency(dTask)

        // Act
        taskProvider.addTask(blockedTask)
        taskProvider.addTask(dTask)
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 0))
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 0))
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 0))

        val task = taskProvider.takeTopTask(null)

        // Verify
        assertEquals("d", task!!.taskId)
        assertNull(taskProvider.getTask("d"))
    }

    fun removeTaskWithUnknownType() {
        // Arrange
        val task1 = Mockito.mock(TaskBase::class.java)
        val taskPrivate1 = Mockito.mock(TaskPrivate::class.java)
        val listener = Mockito.mock(TaskPool.Listener::class.java)

        Mockito.`when`<Task.Status>(task1.taskStatus).thenReturn(Task.Status.NotStarted)
        Mockito.`when`<String>(task1.taskId).thenReturn("taskId")
        Mockito.`when`(task1.private).thenReturn(taskPrivate1)
        Mockito.`when`(task1.taskType).thenReturn(1)

        val task2 = Mockito.mock(TaskBase::class.java)
        val taskPrivate2 = Mockito.mock(TaskPrivate::class.java)

        Mockito.`when`<Task.Status>(task2.taskStatus).thenReturn(Task.Status.NotStarted)
        Mockito.`when`<String>(task2.taskId).thenReturn("taskId")
        Mockito.`when`(task2.private).thenReturn(taskPrivate2)
        Mockito.`when`(task2.taskType).thenReturn(2)

        // Act
        taskProvider.addListener(listener)
        taskProvider.addTask(task1)
        taskProvider.removeTask(task2)

        // Verify
        Mockito.verify<TaskPool.Listener>(listener).onTaskAdded(taskProvider, task1)
        Mockito.verify<TaskPool.Listener>(listener, Mockito.never()).onTaskRemoved(taskProvider, task1)
        Mockito.verify<TaskPool.Listener>(listener, Mockito.never()).onTaskAdded(taskProvider, task2)
        Mockito.verify<TaskPool.Listener>(listener, Mockito.never()).onTaskRemoved(taskProvider, task2)

        assertEquals(1, taskProvider.getTaskCount())
    }
}
