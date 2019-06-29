package com.example.alexeyglushkov.taskmanager.task;

import android.os.Handler;
import android.os.Looper;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by alexeyglushkov on 13.08.16.
 */

public class StackTaskProviderTest {
    protected TaskPoolTestSet poolTest;
    protected TaskProviderTestSet providerTest;
    protected TaskProvider taskProvider;

    @Before
    public void setUp() throws Exception {
        taskProvider = prepareTaskProvider();

        poolTest = new TaskPoolTestSet();
        providerTest = new TaskProviderTestSet();

        preparePoolTest();
        prepareProviderTest();
    }

    protected TaskProvider prepareTaskProvider() {
        return new StackTaskProvider(false, new Handler(Looper.myLooper()), "TestId");
    }

    protected void preparePoolTest() {
        poolTest.before(taskProvider);
    }

    protected void prepareProviderTest() {
        providerTest.before(taskProvider);
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
