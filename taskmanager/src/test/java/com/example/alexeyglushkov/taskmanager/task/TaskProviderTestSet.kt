package com.example.alexeyglushkov.taskmanager.task

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify

import java.util.Arrays

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import org.mockito.Mockito.`when`

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
        val task = taskProvider.getTopTask()

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

        taskProvider.taskFilter = object : TaskProvider.TaskFilter {
            override fun getFilteredTaskTypes(): List<Int> {
                return listOf(1)
            }
        }

        // Act
        val task = taskProvider.getTopTask()

        // Verify
        assertEquals("b", task!!.taskId)
    }

    fun getTopTaskWithDependantTask() {
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

        val task = taskProvider.getTopTask()

        // Verify
        assertEquals("d", task!!.taskId)
    }

    fun getTopTaskWithBlockedTask() {
        // Arrange
        val dTask = TestTasks.createTestTaskSpy("d", 0)
        val blockedTask = TestTasks.createTestTaskSpy("e", 0)
        `when`(blockedTask.taskStatus).thenReturn(Task.Status.Blocked)

        // Act
        taskProvider.addTask(blockedTask)
        taskProvider.addTask(dTask)
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 0))
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 0))
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 0))

        val task = taskProvider.getTopTask()

        // Verify
        assertEquals("d", task!!.taskId)
    }

    fun takeTopTaskWithFilter() {
        // Arrange
        val listener = mock<TaskPool.Listener>()

        taskProvider.addListener(listener)
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 1))
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 2))
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 3))
        taskProvider.addTask(TestTasks.createTestTaskSpy("d", 2))
        taskProvider.addTask(TestTasks.createTestTaskSpy("e", 1))
        taskProvider.addTask(TestTasks.createTestTaskSpy("f", 3))

        taskProvider.taskFilter = object : TaskProvider.TaskFilter {
            override fun getFilteredTaskTypes(): List<Int> {
                return listOf(1)
            }
        }

        // Act
        val task = taskProvider.takeTopTask()

        // Verify
        verify(listener).onTaskRemoved(taskProvider, task!!)

        assertEquals("b", task.taskId)
        assertEquals(5, taskProvider.getTaskCount())
        assertEquals(null, taskProvider.getTask("b"))
    }

    fun takeTopTaskWithDependantTask() {
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

        val task = taskProvider.takeTopTask()

        // Verify
        assertEquals("d", task!!.taskId)
        assertNull(taskProvider.getTask("d"))
    }

    fun takeTopTaskWithBlockedTask() {
        // Arrange
        val dTask = TestTasks.createTestTaskSpy("d", 0)
        val blockedTask = TestTasks.createTestTaskSpy("e", 0)
        `when`(blockedTask.taskStatus).thenReturn(Task.Status.Blocked)

        // Act
        taskProvider.addTask(blockedTask)
        taskProvider.addTask(dTask)
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 0))
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 0))
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 0))

        val task = taskProvider.takeTopTask()

        // Verify
        assertEquals("d", task!!.taskId)
        assertNull(taskProvider.getTask("d"))
    }

    fun removeTaskWithUnknownType() {
        // Arrange
        val task1 = mock<TaskBase>()
        val taskPrivate1 = mock<TaskPrivate>()
        val listener = mock<TaskPool.Listener>()

        `when`<Task.Status>(task1.taskStatus).thenReturn(Task.Status.NotStarted)
        `when`<String>(task1.taskId).thenReturn("taskId")
        `when`(task1.private).thenReturn(taskPrivate1)
        `when`(task1.taskType).thenReturn(1)

        val task2 = mock<TaskBase>()
        val taskPrivate2 = mock<TaskPrivate>()

        `when`<Task.Status>(task2.taskStatus).thenReturn(Task.Status.NotStarted)
        `when`<String>(task2.taskId).thenReturn("taskId")
        `when`(task2.private).thenReturn(taskPrivate2)
        `when`(task2.taskType).thenReturn(2)

        // Act
        taskProvider.addListener(listener)
        taskProvider.addTask(task1)
        taskProvider.removeTask(task2)

        // Verify
        verify(listener).onTaskAdded(taskProvider, task1)
        verify(listener, never()).onTaskRemoved(taskProvider, task1)
        verify(listener, never()).onTaskAdded(taskProvider, task2)
        verify(listener, never()).onTaskRemoved(taskProvider, task2)

        assertEquals(1, taskProvider.getTaskCount())
    }
}
