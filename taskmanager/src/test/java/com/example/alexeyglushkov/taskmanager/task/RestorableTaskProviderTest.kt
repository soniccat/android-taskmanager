package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler
import android.os.Looper

import junit.framework.Assert

import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

/**
 * Created by alexeyglushkov on 19.08.17.
 */

class RestorableTaskProviderTest {
    @Before
    @Throws(Exception::class)
    fun setUp() {
    }

    protected fun prepareTaskProvider(taskProvider: TaskProvider): TaskProvider {
        return RestorableTaskProvider(taskProvider)
    }

    @Test
    fun testAddTaskWhenProviderDoesNothing() {
        // When
        val taskProvider = Mockito.mock(TaskProvider::class.java)
        val restorableTaskProvider = RestorableTaskProvider(taskProvider)
        val task = Mockito.spy(TestTask())

        // Then
        restorableTaskProvider.addTask(task)

        // Assert
        Mockito.verify<Task>(task, Mockito.never()).addTaskStatusListener(restorableTaskProvider)
    }

    @Test
    fun testAddTaskWhenProviderStartsTask() {
        // When
        val taskProvider = Mockito.mock(TaskProvider::class.java)
        Mockito.doReturn(Handler(Looper.myLooper())).`when`(taskProvider).handler
        val restorableTaskProvider = RestorableTaskProvider(taskProvider)
        val task = Mockito.spy(TestTask())

        Mockito.doAnswer(object : Answer<Any> {
            @Throws(Throwable::class)
            override fun answer(invocation: InvocationOnMock): Any? {
                task.private.taskStatus = Task.Status.Waiting
                return null
            }
        }).`when`(taskProvider).addTask(task)

        // Then
        restorableTaskProvider.addTask(task)

        // Assert
        Mockito.verify<TaskBase>(task).addTaskStatusListener(restorableTaskProvider)
    }

    @Test
    fun testTakeTopTask() {
        // When
        val task = Mockito.mock(Task::class.java)

        val taskProvider = Mockito.mock(TaskProvider::class.java)
        Mockito.doReturn(Handler(Looper.myLooper())).`when`(taskProvider).handler
        Mockito.doReturn(task).`when`(taskProvider).takeTopTask(null)

        val restorableTaskProvider = RestorableTaskProvider(taskProvider)

        restorableTaskProvider.addTask(task)

        // Then
        val takenTask = restorableTaskProvider.takeTopTask(null)

        // Assert
        Assert.assertEquals(task, takenTask)
        Assert.assertEquals(1, restorableTaskProvider.activeTasks.size)
        Assert.assertEquals(takenTask, restorableTaskProvider.activeTasks[0])
    }

    @Test
    fun testOnTaskStatusChangedWhenRecording() {
        // When
        val taskProvider = Mockito.mock(TaskProvider::class.java)
        Mockito.doReturn(Handler(Looper.myLooper())).`when`(taskProvider).handler

        val restorableTaskProvider = RestorableTaskProvider(taskProvider)
        restorableTaskProvider.isRecording = true

        val task = Mockito.mock(Task::class.java)
        Mockito.doReturn(Task.Status.Finished).`when`(task).taskStatus

        restorableTaskProvider.activeTasks.add(task)

        // Then
        restorableTaskProvider.onTaskStatusChanged(task, Task.Status.Started, Task.Status.Finished)

        // Assert
        Assert.assertEquals(0, restorableTaskProvider.activeTasks.size)
        Assert.assertEquals(1, restorableTaskProvider.completedTasks.size)
        Assert.assertEquals(task, restorableTaskProvider.completedTasks[0])
    }

    @Test
    fun testOnTaskStatusChangedWhenNotRecording() {
        // When
        val taskProvider = Mockito.mock(TaskProvider::class.java)
        Mockito.doReturn(Handler(Looper.myLooper())).`when`(taskProvider).handler

        val restorableTaskProvider = RestorableTaskProvider(taskProvider)
        restorableTaskProvider.isRecording = false

        val task = Mockito.mock(Task::class.java)
        Mockito.doReturn(Task.Status.Finished).`when`(task).taskStatus

        restorableTaskProvider.activeTasks.add(task)

        // Then
        restorableTaskProvider.onTaskStatusChanged(task, Task.Status.Started, Task.Status.Finished)

        // Assert
        Assert.assertEquals(0, restorableTaskProvider.activeTasks.size)
        Assert.assertEquals(0, restorableTaskProvider.completedTasks.size)
    }

    @Test
    fun testRestoreTaskCompletionWithActiveTasks() {
        // When
        val taskProvider = Mockito.mock(TaskProvider::class.java)
        Mockito.doReturn(Handler(Looper.myLooper())).`when`(taskProvider).handler

        val restorableTaskProvider = RestorableTaskProvider(taskProvider)

        val task = Mockito.spy(TestTask())
        Mockito.doReturn("TestId").`when`<TaskBase>(task).taskId
        Mockito.doReturn(Task.Status.Started).`when`<TaskBase>(task).taskStatus

        restorableTaskProvider.activeTasks.add(task)

        val callback = Mockito.mock(Task.Callback::class.java)

        // Then
        restorableTaskProvider.restoreTaskCompletion("TestId", callback)

        // Assert
        Assert.assertEquals(callback, task.private.taskCallback)
        Mockito.verify<Task.Callback>(callback, Mockito.never()).onCompleted(false)
    }

    @Test
    fun testRestoreTaskCompletionWithCompletedTasks() {
        // When
        val taskProvider = Mockito.mock(TaskProvider::class.java)
        Mockito.doReturn(Handler(Looper.myLooper())).`when`(taskProvider).handler

        val restorableTaskProvider = RestorableTaskProvider(taskProvider)

        val task = Mockito.spy(TestTask())
        Mockito.doReturn("TestId").`when`<TaskBase>(task).taskId
        Mockito.doReturn(Task.Status.Finished).`when`<TaskBase>(task).taskStatus

        restorableTaskProvider.completedTasks.add(task)

        val callback = Mockito.mock(Task.Callback::class.java)

        // Then
        restorableTaskProvider.restoreTaskCompletion("TestId", callback)

        // Assert
        Assert.assertNull(task.private.taskCallback)
        Mockito.verify<Task.Callback>(callback).onCompleted(false)
    }
}
