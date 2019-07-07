package com.example.alexeyglushkov.taskmanager.task;

import org.mockito.Mockito;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * Created by alexeyglushkov on 15.08.15.
 */
public class TaskProviderTestSet {

    protected TaskProvider taskProvider;

    public void before(TaskProvider taskProvider) {
        this.taskProvider = taskProvider;
    }

    public void setProviderId() {
        // Act
        taskProvider.setTaskProviderId("testId");

        // Verify
        assertEquals("testId", taskProvider.getTaskProviderId());
    }

    public void setPriority() {
        // Act
        taskProvider.setPriority(12);

        // Verify
        assertEquals(12, taskProvider.getPriority());
    }

    public void getTopTaskWithoutFilter() {

        // Arrange
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 1));
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 2));
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 1));
        taskProvider.addTask(TestTasks.createTestTaskSpy("d", 2));
        taskProvider.addTask(TestTasks.createTestTaskSpy("e", 1));
        taskProvider.addTask(TestTasks.createTestTaskSpy("f", 2));

        // Act
        Task task = taskProvider.getTopTask(null);

        // Verify
        assertEquals("a", task.getTaskId());
    }

    public void getTopTaskWithFilter() {

        // Arrange
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 1));
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 2));
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 3));
        taskProvider.addTask(TestTasks.createTestTaskSpy("d", 2));
        taskProvider.addTask(TestTasks.createTestTaskSpy("e", 1));
        taskProvider.addTask(TestTasks.createTestTaskSpy("f", 3));

        // Act
        Task task = taskProvider.getTopTask(Arrays.asList(new Integer[]{1}));

        // Verify
        assertEquals("b", task.getTaskId());
    }

    public void getTopTaskWithBlockedTask() {

        // Arrange
        Task dTask = TestTasks.createTestTaskSpy("d", 0);
        Task blockedTask = TestTasks.createTestTaskSpy("e", 0);
        blockedTask.addTaskDependency(dTask);

        // Act
        taskProvider.addTask(blockedTask);
        taskProvider.addTask(dTask);
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 0));
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 0));
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 0));

        Task task = taskProvider.getTopTask(null);

        // Verify
        assertEquals("d", task.getTaskId());
    }

    public void takeTopTaskWithFilter() {
        // Arrange
        TaskProvider.Listener listener = Mockito.mock(TaskProvider.Listener.class);

        taskProvider.addListener(listener);
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 1));
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 2));
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 3));
        taskProvider.addTask(TestTasks.createTestTaskSpy("d", 2));
        taskProvider.addTask(TestTasks.createTestTaskSpy("e", 1));
        taskProvider.addTask(TestTasks.createTestTaskSpy("f", 3));

        // Act
        Task task = taskProvider.takeTopTask(Arrays.asList(new Integer[]{1}));

        // Verify
        Mockito.verify(listener).onTaskRemoved(taskProvider, task);

        assertEquals("b", task.getTaskId());
        assertEquals(5, taskProvider.getTaskCount());
        assertEquals(null, taskProvider.getTask("b"));
    }

    public void takeTopTaskWithBlockedTask() {

        // Arrange
        Task dTask = TestTasks.createTestTaskSpy("d", 0);
        Task blockedTask = TestTasks.createTestTaskSpy("e", 0);
        blockedTask.addTaskDependency(dTask);

        // Act
        taskProvider.addTask(blockedTask);
        taskProvider.addTask(dTask);
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 0));
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 0));
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 0));

        Task task = taskProvider.takeTopTask(null);

        // Verify
        assertEquals("d", task.getTaskId());
        assertNull(taskProvider.getTask("d"));
    }

    public void removeTaskWithUnknownType() {
        // Arrange
        TaskBase task1 = Mockito.mock(TaskBase.class);
        TaskPrivate taskPrivate1 = Mockito.mock(TaskPrivate.class);
        TaskPool.Listener listener = Mockito.mock(TaskPool.Listener.class);

        Mockito.when(task1.getTaskStatus()).thenReturn(Task.Status.NotStarted);
        Mockito.when(task1.getTaskId()).thenReturn("taskId");
        Mockito.when(task1.getPrivate()).thenReturn(taskPrivate1);
        Mockito.when(task1.getTaskType()).thenReturn(1);

        TaskBase task2 = Mockito.mock(TaskBase.class);
        TaskPrivate taskPrivate2 = Mockito.mock(TaskPrivate.class);

        Mockito.when(task2.getTaskStatus()).thenReturn(Task.Status.NotStarted);
        Mockito.when(task2.getTaskId()).thenReturn("taskId");
        Mockito.when(task2.getPrivate()).thenReturn(taskPrivate2);
        Mockito.when(task2.getTaskType()).thenReturn(2);

        // Act
        taskProvider.addListener(listener);
        taskProvider.addTask(task1);
        taskProvider.removeTask(task2);

        // Verify
        Mockito.verify(listener).onTaskAdded(taskProvider, task1);
        Mockito.verify(listener, Mockito.never()).onTaskRemoved(taskProvider, task1);
        Mockito.verify(listener, Mockito.never()).onTaskAdded(taskProvider, task2);
        Mockito.verify(listener, Mockito.never()).onTaskRemoved(taskProvider, task2);

        assertEquals(1, taskProvider.getTaskCount());
    }
}
