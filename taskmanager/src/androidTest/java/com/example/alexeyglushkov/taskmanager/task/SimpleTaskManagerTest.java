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
 * Created by alexeyglushkov on 23.08.15.
 */

@RunWith(AndroidJUnit4.class)
public class SimpleTaskManagerTest {

    private TaskPoolTest poolTest;
    private TaskManagerTest taskManagerTest;
    private TaskManager taskManager;

    @Rule
    public UiThreadTestRule rule = new UiThreadTestRule();

    @Before
    public void setUp() throws Exception {
        taskManager = new SimpleTaskManager(10, new Handler(Looper.myLooper()));
        poolTest = new TaskPoolTest();
        taskManagerTest = new TaskManagerTest();

        poolTest.before(taskManager);
        taskManagerTest.before(taskManager);
    }

    @Test @UiThreadTest
    public void testSetMaxLoadingTasks() {
        taskManagerTest.setMaxLoadingTasks();
    }

    @Test @UiThreadTest
    public void testGetLoadingTaskCount() {
        taskManagerTest.getLoadingTaskCount();
    }

    @Test @UiThreadTest
    public void testAddTask() {
        taskManagerTest.addTask();
    }

    @Test @UiThreadTest
    public void testAddStartedTask() {
        taskManagerTest.addStartedTask();
    }

    @Test @UiThreadTest
    public void testAddTheSameTaskWithSkipPolicy() {
        taskManagerTest.addTheSameTaskWithSkipPolicy();
    }

    @Test @UiThreadTest
    public void testAddTheSameTaskWithCancelPolicy() {
        taskManagerTest.addTheSameTaskWithCancelPolicy();
    }

    @Test @UiThreadTest
    public void testTaskCallbackCalled() {
        taskManagerTest.taskCallbackCalled();
    }

    @Test @UiThreadTest
    public void testChangedTaskCallbackCalled() {
        taskManagerTest.changedTaskCallbackCalled();
    }

    @Test @UiThreadTest
    public void testTaskWithCancelledImmediatelyCallbackCalledAfterCancel() {
        taskManagerTest.taskWithCancelledImmediatelyCallbackCalledAfterCancel();
    }

    @Test @UiThreadTest
    public void testTaskWithCancelledImmediatelyAndChangedCallbackCalled() {
        taskManagerTest.taskWithCancelledImmediatelyAndChangedCallbackCalled();
    }

    @Test @UiThreadTest
    public void testAddStateListener() {
        taskManagerTest.addStateListener();
    }

    @Test @UiThreadTest
    public void testRemoveStateListener() {
        taskManagerTest.removeStateListener();
    }

    @Test @UiThreadTest
    public void testRemoveTask() {
        taskManagerTest.removeTask();
    }

    @Test @UiThreadTest
    public void testRemoveUnknownTask() {
        taskManagerTest.removeUnknownTask();
    }

    @Test @UiThreadTest
    public void testCheckTaskRemovingAfterFinishing() {
        taskManagerTest.checkTaskRemovingAfterFinishing();
    }

    @Test @UiThreadTest
    public void testGetTaskFromProvider() {
        taskManagerTest.getTaskFromProvider();
    }

    @Test @UiThreadTest
    public void testAddTaskProvider() {
        taskManagerTest.addTaskProvider();
    }

    @Test @UiThreadTest
    public void testAddTaskProvider2() {
        taskManagerTest.addTaskProvider2();
    }

    @Test @UiThreadTest
    public void testAddTaskProviderWithTheSameId() {
        taskManagerTest.addTaskProviderWithTheSameId();
    }

    @Test @UiThreadTest
    public void testRemoveTaskProvider() {
        taskManagerTest.removeTaskProvider();
    }

    @Test @UiThreadTest
    public void testSetTaskExecutor() {
        taskManagerTest.setTaskExecutor();
    }

    @Test @UiThreadTest
    public void testStartImmediately() {
        taskManagerTest.startImmediately();
    }

    @Test @UiThreadTest
    public void testStartImmediatelySkipPolicy() {
        taskManagerTest.startImmediatelySkipPolicy();
    }

    @Test @UiThreadTest
    public void testStartImmediatelySkipPolicyWithFinish() {
        taskManagerTest.startImmediatelySkipPolicyWithFinish();
    }

    @Test @UiThreadTest
    public void testStartImmediatelyFinish() {
        taskManagerTest.startImmediatelyFinish();
    }

    @Test @UiThreadTest
    public void testStartImmediatelyFinishWithChangedCallback() {
        taskManagerTest.startImmediatelyFinishWithChangedCallback();
    }

    @Test @UiThreadTest
    public void testStartImmediatelyCancelWithChangedCallback() {
        taskManagerTest.startImmediatelyCancelWithChangedCallback();
    }

    @Test @UiThreadTest
    public void testSetTaskProviderPriority() {
        taskManagerTest.setTaskProviderPriority();
    }

    @Test @UiThreadTest
    public void testSetGetHandler() {
        taskManagerTest.setGetHandler();
    }

    @Test @UiThreadTest
    public void testGetTaskCount() {
        taskManagerTest.getTaskCount();
    }

    @Test @UiThreadTest
    public void testGetTasks() {
        taskManagerTest.getTasks();
    }

    @Test @UiThreadTest
    public void testSetLimit() {
        taskManagerTest.setLimit();
    }

    @Test @UiThreadTest
    public void testSetLimitRemove() {
        taskManagerTest.setLimitRemove();
    }

    // PoolTests

    @Test @UiThreadTest
    public void testGetTask() {
        poolTest.getTask();
    }

    @Test @UiThreadTest
    public void testGetUnknownTask() {
        poolTest.getUnknownTask();
    }

    @Test @UiThreadTest
    public void testSetGetUserData() {
        poolTest.setGetUserData();
    }
}
