package com.example.alexeyglushkov.taskmanager.task

import org.mockito.Mockito

/**
 * Created by alexeyglushkov on 30.08.15.
 */
object TestTasks {

    @JvmOverloads
    fun createTaskMock(id: String? = null, status: Task.Status = Task.Status.NotStarted, type: Int = 0, priority: Int = 0): TaskBase {
        val task = Mockito.mock(TaskBase::class.java)
        val taskPrivate = Mockito.mock(TaskPrivate::class.java)

        Mockito.`when`<Task.Status>(task.taskStatus).thenReturn(status)
        Mockito.`when`(task.taskType).thenReturn(type)
        Mockito.`when`(task.taskPriority).thenReturn(priority)
        Mockito.`when`(task.private).thenReturn(taskPrivate)

        if (id != null) {
            Mockito.`when`<String>(task.taskId).thenReturn(id)
        }

        return task
    }

    fun createTestTaskSpy(id: String): TestTask {
        return TestTasks.createTestTaskSpy(id, 0, 0)
    }

    fun createTestTaskSpy(id: String, type: Int): TestTask {
        return TestTasks.createTestTaskSpy(id, type, 0)
    }

    fun createTestTaskSpy(id: String, type: Int, priority: Int): TestTask {
        val testTask = Mockito.spy(TestTask())
        testTask.taskId = id
        testTask.taskPriority = priority
        testTask.taskType = type

        Mockito.`when`(testTask.private).thenReturn(testTask)

        return testTask
    }
}
