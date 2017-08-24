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

    private TaskPoolTestSet poolTestSet;
    private TaskManagerTestSet taskManagerTestSet;
    private TaskManager taskManager;

    @Rule
    public UiThreadTestRule rule = new UiThreadTestRule();

    @Before
    public void setUp() throws Exception {
        taskManager = new SimpleTaskManager(10, new Handler(Looper.myLooper()));
        poolTestSet = new TaskPoolTestSet();
        taskManagerTestSet = new TaskManagerTestSet();

        poolTestSet.before(taskManager);
        taskManagerTestSet.before(taskManager);
    }

    @Test @UiThreadTest
    public void testSetMaxLoadingTasks() {
        taskManagerTestSet.setMaxLoadingTasks();
    }

    @Test @UiThreadTest
    public void testGetLoadingTaskCount() {
        taskManagerTestSet.getLoadingTaskCount();
    }

    @Test @UiThreadTest
    public void testAddTask() {
        taskManagerTestSet.addTask();
    }

    @Test @UiThreadTest
    public void testAddStartedTask() {
        taskManagerTestSet.addStartedTask();
    }

    @Test @UiThreadTest
    public void testAddTheSameTaskWithSkipPolicy() {
        taskManagerTestSet.addTheSameTaskWithSkipPolicy();
    }

    @Test @UiThreadTest
    public void testAddTheSameTaskWithCancelPolicy() {
        taskManagerTestSet.addTheSameTaskWithCancelPolicy();
    }

    @Test @UiThreadTest
    public void testTaskCallbackCalled() {
        taskManagerTestSet.taskCallbackCalled();
    }

    @Test @UiThreadTest
    public void testChangedTaskCallbackCalled() {
        taskManagerTestSet.changedTaskCallbackCalled();
    }

    @Test @UiThreadTest
    public void testTaskWithCancelledImmediatelyCallbackCalledAfterCancel() {
        taskManagerTestSet.taskWithCancelledImmediatelyCallbackCalledAfterCancel();
    }

    @Test @UiThreadTest
    public void testTaskWithCancelledImmediatelyAndChangedCallbackCalled() {
        taskManagerTestSet.taskWithCancelledImmediatelyAndChangedCallbackCalled();
    }

    @Test @UiThreadTest
    public void testAddStateListener() {
        taskManagerTestSet.addStateListener();
    }

    @Test @UiThreadTest
    public void testRemoveStateListener() {
        taskManagerTestSet.removeStateListener();
    }

    @Test @UiThreadTest
    public void testRemoveTask() {
        taskManagerTestSet.removeTask();
    }

    @Test @UiThreadTest
    public void testRemoveUnknownTask() {
        taskManagerTestSet.removeUnknownTask();
    }

    @Test @UiThreadTest
    public void testCheckTaskRemovingAfterFinishing() {
        taskManagerTestSet.checkTaskRemovingAfterFinishing();
    }

    @Test @UiThreadTest
    public void testGetTaskFromProvider() {
        taskManagerTestSet.getTaskFromProvider();
    }

    @Test @UiThreadTest
    public void testAddTaskProvider() {
        taskManagerTestSet.addTaskProvider();
    }

    @Test @UiThreadTest
    public void testAddTaskProvider2() {
        taskManagerTestSet.addTaskProvider2();
    }

    @Test @UiThreadTest
    public void testAddTaskProviderWithTheSameId() {
        taskManagerTestSet.addTaskProviderWithTheSameId();
    }

    @Test @UiThreadTest
    public void testRemoveTaskProvider() {
        taskManagerTestSet.removeTaskProvider();
    }

    @Test @UiThreadTest
    public void testSetTaskExecutor() {
        taskManagerTestSet.setTaskExecutor();
    }

    @Test @UiThreadTest
    public void testStartImmediately() {
        taskManagerTestSet.startImmediately();
    }

    @Test @UiThreadTest
    public void testStartImmediatelySkipPolicy() {
        taskManagerTestSet.startImmediatelySkipPolicy();
    }

    @Test @UiThreadTest
    public void testStartImmediatelySkipPolicyWithFinish() {
        taskManagerTestSet.startImmediatelySkipPolicyWithFinish();
    }

    @Test @UiThreadTest
    public void testStartImmediatelyFinish() {
        taskManagerTestSet.startImmediatelyFinish();
    }

    @Test @UiThreadTest
    public void testStartImmediatelyFinishWithChangedCallback() {
        taskManagerTestSet.startImmediatelyFinishWithChangedCallback();
    }

    @Test @UiThreadTest
    public void testStartImmediatelyCancelWithChangedCallback() {
        taskManagerTestSet.startImmediatelyCancelWithChangedCallback();
    }

    @Test @UiThreadTest
    public void testSetTaskProviderPriority() {
        taskManagerTestSet.setTaskProviderPriority();
    }

    @Test @UiThreadTest
    public void testSetGetHandler() {
        taskManagerTestSet.setGetHandler();
    }

    @Test @UiThreadTest
    public void testGetTaskCount() {
        taskManagerTestSet.getTaskCount();
    }

    @Test @UiThreadTest
    public void testGetTasks() {
        taskManagerTestSet.getTasks();
    }

    @Test @UiThreadTest
    public void testSetLimit() {
        taskManagerTestSet.setLimit();
    }

    @Test @UiThreadTest
    public void testSetLimitRemove() {
        taskManagerTestSet.setLimitRemove();
    }

    // PoolTests

    @Test @UiThreadTest
    public void testGetTask() {
        poolTestSet.getTask();
    }

    @Test @UiThreadTest
    public void testGetUnknownTask() {
        poolTestSet.getUnknownTask();
    }

    @Test @UiThreadTest
    public void testSetGetUserData() {
        poolTestSet.setGetUserData();
    }
}
