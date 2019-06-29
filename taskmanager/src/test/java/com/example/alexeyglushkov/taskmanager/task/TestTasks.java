package com.example.alexeyglushkov.taskmanager.task;

import org.mockito.Mockito;

/**
 * Created by alexeyglushkov on 30.08.15.
 */
public class TestTasks {
    public static Task createTaskMock() {
        return createTaskMock(null, Task.Status.NotStarted, 0, 0);
    }

    public static Task createTaskMock(String id) {
        return createTaskMock(id, Task.Status.NotStarted, 0, 0);
    }

    public static Task createTaskMock(String id, Task.Status status) {
        return createTaskMock(id, status, 0, 0);
    }

    public static Task createTaskMock(String id, Task.Status status, int type, int priority) {
        Task task = Mockito.mock(Task.class);
        TaskPrivate taskPrivate = Mockito.mock(TaskPrivate.class);

        Mockito.when(task.getTaskStatus()).thenReturn(status);
        Mockito.when(task.getTaskType()).thenReturn(type);
        Mockito.when(task.getTaskPriority()).thenReturn(priority);
        Mockito.when(task.getPrivate()).thenReturn(taskPrivate);

        if (id != null) {
            Mockito.when(task.getTaskId()).thenReturn(id);
        }

        return task;
    }

    public static TestTask createTestTaskSpy(String id) {
        return TestTasks.createTestTaskSpy(id, 0, 0);
    }

    public static TestTask createTestTaskSpy(String id, int type) {
        return TestTasks.createTestTaskSpy(id, type, 0);
    }

    public static TestTask createTestTaskSpy(String id, int type, int priority) {
        TestTask testTask = Mockito.spy(new TestTask());
        testTask.setTaskId(id);
        testTask.setTaskPriority(priority);
        testTask.setTaskType(type);

        Mockito.when(testTask.getPrivate()).thenReturn(testTask);

        return testTask;
    }
}
