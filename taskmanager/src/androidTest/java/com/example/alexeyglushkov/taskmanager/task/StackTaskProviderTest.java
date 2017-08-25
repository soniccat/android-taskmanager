package com.example.alexeyglushkov.taskmanager.task;

import android.os.Handler;
import android.os.Looper;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.UiThreadTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by alexeyglushkov on 13.08.16.
 */

@RunWith(AndroidJUnit4.class)
public class StackTaskProviderTest {
    protected TaskPoolTestSet poolTest;
    protected TaskProviderTestSet providerTest;
    protected TaskProvider taskProvider;

    @Rule
    public UiThreadTestRule rule = new UiThreadTestRule();

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

    @Test @UiThreadTest
    public void testGetTopTaskWithBlockedTask() {
        providerTest.getTopTaskWithBlockedTask();
    }

    @Test @UiThreadTest
    public void testTakeTopTaskWithBlockedTask() {
        providerTest.takeTopTaskWithBlockedTask();
    }

    @Test @UiThreadTest
    public void testSetProviderId() {
        providerTest.setProviderId();
    }

    @Test @UiThreadTest
    public void testSetPriority() {
        providerTest.setPriority();
    }

    @Test @UiThreadTest
    public void testGetTopTaskWithoutFilter() {
        providerTest.getTopTaskWithoutFilter();
    }

    @Test @UiThreadTest
    public void testGetTopTaskWithFilter() {
        providerTest.getTopTaskWithFilter();
    }

    @Test @UiThreadTest
    public void testTakeTopTaskWithFilter() {
        providerTest.takeTopTaskWithFilter();
    }

    @Test @UiThreadTest
    public void testRemoveTaskWithUnknownType() {
        providerTest.removeTaskWithUnknownType();
    }

    // PoolTests

    @Test @UiThreadTest
    public void testSetGetHandler() {
        poolTest.setGetHandler();
    }

    @Test @UiThreadTest
    public void testAddTask() {
        poolTest.addTask();
    }

    @Test @UiThreadTest
    public void testAddTaskAddStatusListener() {
        poolTest.addTaskAddStatusListener();
    }

    @Test @UiThreadTest
    public void testAddStartedTask() {
        poolTest.addStartedTask();
    }

    @Test @UiThreadTest
    public void testRemoveTask() {
        poolTest.removeTask();
    }

    @Test @UiThreadTest
    public void testRemoveUnknownTask() {
        poolTest.removeUnknownTask();
    }

    @Test @UiThreadTest
    public void testGetTask() {
        poolTest.getTask();
    }

    @Test @UiThreadTest
    public void testGetUnknownTask() {
        poolTest.getUnknownTask();
    }

    @Test @UiThreadTest
    public void testGetTaskCount() {
        poolTest.getTaskCount();
    }

    @Test @UiThreadTest
    public void testGetTaskCount2() {
        poolTest.getTaskCount2();
    }

    @Test @UiThreadTest
    public void testSetGetUserData() {
        poolTest.setGetUserData();
    }

    @Test @UiThreadTest
    public void testAddStateListener() {
        poolTest.addStateListener();
    }

    @Test @UiThreadTest
    public void testRemoveStateListener() {
        poolTest.removeStateListener();
    }

    @Test @UiThreadTest
    public void testChangeTaskStatus() {
        poolTest.changeTaskStatus();
    }

    @Test @UiThreadTest
    public void testCheckTaskRemovingAfterFinishing() {
        poolTest.checkTaskRemovingAfterFinishing();
    }
}