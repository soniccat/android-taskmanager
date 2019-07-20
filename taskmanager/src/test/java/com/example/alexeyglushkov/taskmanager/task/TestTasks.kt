package com.example.alexeyglushkov.taskmanager.task

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import org.mockito.Mockito.`when`

/**
 * Created by alexeyglushkov on 30.08.15.
 */
object TestTasks {

    @JvmOverloads
    fun createTaskMock(id: String? = null, status: Task.Status = Task.Status.NotStarted, type: Int = 0, priority: Int = 0): TaskBase {
        val task = mock<TaskBase>()
        val taskPrivate = mock<TaskPrivate>()

        `when`<Task.Status>(task.taskStatus).thenReturn(status)
        `when`(task.taskType).thenReturn(type)
        `when`(task.taskPriority).thenReturn(priority)
        `when`(task.private).thenReturn(taskPrivate)

        if (id != null) {
            `when`<String>(task.taskId).thenReturn(id)
        }

        return task
    }

    fun createTestTaskSpy(id: String): TestTask {
        return createTestTaskSpy(id, 0, 0)
    }

    fun createTestTaskSpy(id: String, type: Int): TestTask {
        return createTestTaskSpy(id, type, 0)
    }

    fun createTestTaskSpy(id: String, type: Int, priority: Int): TestTask {
        val testTask = spy(TestTask())
        testTask.taskId = id
        testTask.taskPriority = priority
        testTask.taskType = type

        `when`(testTask.private).thenReturn(testTask)

        return testTask
    }
}
