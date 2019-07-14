package com.example.alexeyglushkov.taskmanager.task;

import android.os.Handler;
import android.os.Looper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

/**
 * Created by alexeyglushkov on 23.08.15.
 */

public class SimpleTaskManagerTest {

    private TaskPoolTestSet poolTestSet;
    private TaskManagerTestSet taskManagerTestSet;
    private TaskManager taskManager;

    @Before
    public void setUp() throws Exception {
        taskManager = new SimpleTaskManager(10, new Handler(Looper.myLooper()));
        poolTestSet = new TaskPoolTestSet();
        taskManagerTestSet = new TaskManagerTestSet();

        poolTestSet.before(taskManager);
        taskManagerTestSet.before(taskManager);
    }

    @Test
    public void testSetMaxLoadingTasks() {
        taskManagerTestSet.setMaxLoadingTasks();
    }

    @Test
    public void testGetLoadingTaskCount() {
        taskManagerTestSet.getLoadingTaskCount();
    }

    @Test
    public void testAddTask() {
        taskManagerTestSet.addTask();
    }

    @Test
    public void testAddStartedTask() {
        taskManagerTestSet.addStartedTask();
    }

    @Test
    public void testAddTheSameTaskWithSkipPolicy() {
        taskManagerTestSet.addTheSameTaskWithSkipPolicy();
    }

    @Test
    public void testAddTheSameTaskWithCancelPolicy() {
        taskManagerTestSet.addTheSameTaskWithCancelPolicy();
    }

    @Test
    public void testTaskCallbackCalled() {
        taskManagerTestSet.taskCallbackCalled();
    }

    @Test
    public void testChangedTaskCallbackCalled() {
        taskManagerTestSet.changedTaskCallbackCalled();
    }

    @Test
    public void testTaskWithCancelledImmediatelyCallbackCalledAfterCancel() {
        taskManagerTestSet.taskWithCancelledImmediatelyCallbackCalledAfterCancel();
    }

    @Test
    public void testTaskWithCancelledImmediatelyAndChangedCallbackCalled() {
        taskManagerTestSet.taskWithCancelledImmediatelyAndChangedCallbackCalled();
    }

    @Test
    public void testAddStateListener() {
        taskManagerTestSet.addStateListener();
    }

    @Test
    public void testRemoveStateListener() {
        taskManagerTestSet.removeStateListener();
    }

    @Test
    public void testRemoveTask() {
        taskManagerTestSet.removeTask();
    }

    @Test
    public void testRemoveUnknownTask() {
        taskManagerTestSet.removeUnknownTask();
    }

    @Test
    public void testCheckTaskRemovingAfterFinishing() {
        taskManagerTestSet.checkTaskRemovingAfterFinishing();
    }

    @Test
    public void testGetTaskFromProvider() {
        taskManagerTestSet.getTaskFromProvider();
    }

    @Test
    public void testAddTaskProvider() {
        taskManagerTestSet.addTaskProvider();
    }

    @Test
    public void testAddTaskProvider2() {
        taskManagerTestSet.addTaskProvider2();
    }

    @Test
    public void testAddTaskProviderWithTheSameId() {
        taskManagerTestSet.addTaskProviderWithTheSameId();
    }

    @Test
    public void testRemoveTaskProvider() {
        taskManagerTestSet.removeTaskProvider();
    }

    @Test
    public void testSetTaskExecutor() {
        taskManagerTestSet.setTaskExecutor();
    }

    @Test
    public void testStartImmediately() {
        taskManagerTestSet.startImmediately();
    }

    @Test
    public void testStartImmediatelySkipPolicy() {
        taskManagerTestSet.startImmediatelySkipPolicy();
    }

    @Test
    public void testStartImmediatelySkipPolicyWithFinish() {
        taskManagerTestSet.startImmediatelySkipPolicyWithFinish();
    }

    @Test
    public void testStartImmediatelyFinish() {
        taskManagerTestSet.startImmediatelyFinish();
    }

    @Test
    public void testStartImmediatelyFinishWithChangedCallback() {
        taskManagerTestSet.startImmediatelyFinishWithChangedCallback();
    }

    @Test
    public void testStartImmediatelyCancelWithChangedCallback() {
        taskManagerTestSet.startImmediatelyCancelWithChangedCallback();
    }

    @Test
    public void testSetTaskProviderPriority() {
        taskManagerTestSet.setTaskProviderPriority();
    }

    @Test
    public void testSetGetHandler() {
        taskManagerTestSet.setGetHandler();
    }

    @Test
    public void testGetTaskCount() {
        taskManagerTestSet.getTaskCount();
    }

    @Test
    public void testGetTasks() {
        taskManagerTestSet.getTasks();
    }

    @Test
    public void testSetLimit() {
        taskManagerTestSet.setLimit();
    }

    @Test
    public void testSetLimitRemove() {
        taskManagerTestSet.setLimitRemove();
    }

    // PoolTests

    @Test
    public void testGetTask() {
        poolTestSet.getTask();
    }

    @Test
    public void testGetUnknownTask() {
        poolTestSet.getUnknownTask();
    }

    @Test
    public void testSetGetUserData() {
        poolTestSet.setGetUserData();
    }
}
