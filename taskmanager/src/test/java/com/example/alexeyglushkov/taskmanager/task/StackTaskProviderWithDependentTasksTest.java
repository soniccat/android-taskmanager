package com.example.alexeyglushkov.taskmanager.task;

import android.os.Handler;
import android.os.Looper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * Created by alexeyglushkov on 13.08.16.
 */

public class StackTaskProviderWithDependentTasksTest {
    private TaskPoolTestSet poolTest;
    private TaskProviderTestSet providerTest;
    private StackTaskProvider taskProvider;

    @Before
    public void setUp() throws Exception {
        taskProvider = new StackTaskProvider(true, new Handler(Looper.myLooper()), "TestId");

        poolTest = new TaskPoolTestSet();
        providerTest = new TaskProviderTestSet();

        poolTest.before(taskProvider);
        providerTest.before(taskProvider);
    }

    // StackTaskProviderTests

    @Test
    public void testAddedIsTriggeredWhenTaskIsFinished() {
        // Arrange
        TestTask testTask1 = new TestTask();
        TestTask testTask2 = new TestTask();
        StackTaskProvider providerMock = Mockito.spy(taskProvider);
        TaskPool.TaskPoolListener listener = Mockito.mock(TaskPool.TaskPoolListener.class);

        // Act
        providerMock.addListener(listener);
        providerMock.addTask(testTask1);
        providerMock.takeTopTask(null);

        providerMock.addTask(testTask2);

        // Verify
        Mockito.verify(listener, Mockito.never()).onTaskAdded(providerMock, testTask2);
        testTask1.getPrivate().setTaskStatus(Task.Status.Finished);

        Mockito.verify(listener).onTaskAdded(providerMock, testTask2);
        assertEquals(1, taskProvider.getTaskCount());
    }

    @Test
    public void testTakeTopTaskIsEmptyIfBlocked() {
        // Arrange
        TestTask testTask1 = new TestTask();
        TestTask testTask2 = new TestTask();
        StackTaskProvider providerMock = Mockito.spy(taskProvider);

        // Act
        providerMock.addTask(testTask1);
        Task task1 = providerMock.takeTopTask(null);

        providerMock.addTask(testTask2);
        Task task2 = providerMock.takeTopTask(null);

        // Verify
        assertEquals(testTask1, task1);
        assertNull(task2);
        testTask1.getPrivate().setTaskStatus(Task.Status.Finished);

        assertEquals(1, taskProvider.getTaskCount());
    }

    @Test
    public void testGetTopTaskIsEmptyIfBlocked() {
        // Arrange
        TestTask testTask1 = new TestTask();
        TestTask testTask2 = new TestTask();
        StackTaskProvider providerMock = Mockito.spy(taskProvider);

        // Act
        providerMock.addTask(testTask1);
        Task task1 = providerMock.getTopTask(null);
        providerMock.takeTopTask(null);

        providerMock.addTask(testTask2);
        Task task2 = providerMock.getTopTask(null);

        // Verify
        assertEquals(testTask1, task1);
        assertNull(task2);
        testTask1.getPrivate().setTaskStatus(Task.Status.Finished);

        assertEquals(1, taskProvider.getTaskCount());
    }

    // ProviderTests

    @Test
    public void testGetTopTaskWithBlockedTask() {
        providerTest.getTopTaskWithBlockedTask();
    }

    @Test
    public void testTakeTopTaskWithBlockedTask() {
        providerTest.takeTopTaskWithBlockedTask();
    }

    @Test
    public void testSetProviderId() {
        providerTest.setProviderId();
    }

    @Test
    public void testSetPriority() {
        providerTest.setPriority();
    }

    @Test
    public void testGetTopTaskWithoutFilter() {
        providerTest.getTopTaskWithoutFilter();
    }

    @Test
    public void testGetTopTaskWithFilter() {
        providerTest.getTopTaskWithFilter();
    }

    @Test
    public void testTakeTopTaskWithFilter() {
        providerTest.takeTopTaskWithFilter();
    }

    @Test
    public void testRemoveTaskWithUnknownType() {
        providerTest.removeTaskWithUnknownType();
    }

    // PoolTests

    @Test
    public void testSetGetHandler() {
        poolTest.setGetHandler();
    }

    @Test
    public void testAddTask() {
        poolTest.addTask();
    }

    @Test
    public void testAddTaskAddStatusListener() {
        poolTest.addTaskAddStatusListener();
    }

    @Test
    public void testAddStartedTask() {
        poolTest.addStartedTask();
    }

    @Test
    public void testRemoveTask() {
        poolTest.removeTask();
    }

    @Test
    public void testRemoveUnknownTask() {
        poolTest.removeUnknownTask();
    }

    @Test
    public void testGetTask() {
        poolTest.getTask();
    }

    @Test
    public void testGetUnknownTask() {
        poolTest.getUnknownTask();
    }

    @Test
    public void testGetTaskCount() {
        poolTest.getTaskCount();
    }

    @Test
    public void testGetTaskCount2() {
        poolTest.getTaskCount2();
    }

    @Test
    public void testSetGetUserData() {
        poolTest.setGetUserData();
    }

    @Test
    public void testAddStateListener() {
        poolTest.addStateListener();
    }

    @Test
    public void testRemoveStateListener() {
        poolTest.removeStateListener();
    }

    @Test
    public void testChangeTaskStatus() {
        poolTest.changeTaskStatus();
    }

    @Test
    public void testCheckTaskRemovingAfterFinishing() {
        poolTest.checkTaskRemovingAfterFinishing();
    }
}
