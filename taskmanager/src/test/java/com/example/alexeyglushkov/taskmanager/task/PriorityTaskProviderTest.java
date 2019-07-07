package com.example.alexeyglushkov.taskmanager.task;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

import org.mockito.Mockito;

import java.util.Arrays;

/**
 * Created by alexeyglushkov on 09.08.15.
 */

public class PriorityTaskProviderTest {
    private TaskPoolTestSet poolTestSet;
    private TaskProviderTestSet providerTest;
    protected TaskProvider taskProvider;

    @Before
    public void setUp() throws Exception {
        taskProvider = prepareTaskProvider();

        poolTestSet = new TaskPoolTestSet();
        providerTest = new TaskProviderTestSet();

        poolTestSet.before(taskProvider);
        providerTest.before(taskProvider);
    }

    @NonNull
    protected TaskProvider prepareTaskProvider() {
        return new PriorityTaskProvider(new Handler(Looper.myLooper()), "TestId");
    }

    protected PriorityTaskProvider getPriorityTaskProvider() {
        return (PriorityTaskProvider)taskProvider;
    }

    // PriorityTaskProviderTests

    @Test
    public void testUpdatePriorities() {
        // Arrange
        TaskProvider.Listener listener = Mockito.mock(TaskProvider.Listener.class);

        taskProvider.addListener(listener);
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 1, 1));
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 2, 2));
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 3, 3));
        taskProvider.addTask(TestTasks.createTestTaskSpy("d", 2, 4));
        taskProvider.addTask(TestTasks.createTestTaskSpy("e", 1, 5));
        taskProvider.addTask(TestTasks.createTestTaskSpy("f", 3, 6));

        // Act
        getPriorityTaskProvider().updatePriorities(new PriorityTaskProvider.PriorityProvider() {
            @Override
            public int getPriority(Task task) {
                if (task.getTaskType() == 2) {
                    return 2 * task.getTaskPriority();
                }
                return task.getTaskPriority();
            }
        });

        Task task = taskProvider.getTopTask(null);

        // Verify
        assertEquals("d", task.getTaskId());
    }

    @Test
    public void testTopTaskWithPriorityWithoutFilter() {
        // Arrange
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 1, 1));
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 2, 2));
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 1, 3));
        taskProvider.addTask(TestTasks.createTestTaskSpy("d", 2, 4));
        taskProvider.addTask(TestTasks.createTestTaskSpy("e", 1, 5));
        taskProvider.addTask(TestTasks.createTestTaskSpy("f", 2, 6));

        // Act
        Task task = taskProvider.getTopTask(null);

        // Verify
        assertEquals("f", task.getTaskId());
    }

    @Test
    public void testGetTopTaskWithPriorityWithFilter() {
        // Arrange
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 1, 1));
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 2, 2));
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 3, 3));
        taskProvider.addTask(TestTasks.createTestTaskSpy("d", 2, 4));
        taskProvider.addTask(TestTasks.createTestTaskSpy("e", 1, 5));
        taskProvider.addTask(TestTasks.createTestTaskSpy("f", 3, 6));

        // Act
        Task task = taskProvider.getTopTask(Arrays.asList(new Integer[]{3}));

        // Verify
        assertEquals("e", task.getTaskId());
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
        poolTestSet.setGetHandler();
    }

    @Test
    public void testAddTask() {
        poolTestSet.addTask();
    }

    @Test
    public void testAddTaskAddStatusListener() {
        poolTestSet.addTaskAddStatusListener();
    }

    @Test
    public void testAddStartedTask() {
        poolTestSet.addStartedTask();
    }

    @Test
    public void testRemoveTask() {
        poolTestSet.removeTask();
    }

    @Test
    public void testRemoveUnknownTask() {
        poolTestSet.removeUnknownTask();
    }

    @Test
    public void testGetTask() {
        poolTestSet.getTask();
    }

    @Test
    public void testGetUnknownTask() {
        poolTestSet.getUnknownTask();
    }

    @Test
    public void testGetTaskCount() {
        poolTestSet.getTaskCount();
    }

    @Test
    public void testGetTaskCount2() {
        poolTestSet.getTaskCount2();
    }

    @Test
    public void testSetGetUserData() {
        poolTestSet.setGetUserData();
    }

    @Test
    public void testAddStateListener() {
        poolTestSet.addStateListener();
    }

    @Test
    public void testRemoveStateListener() {
        poolTestSet.removeStateListener();
    }

    @Test
    public void testChangeTaskStatus() {
        poolTestSet.changeTaskStatus();
    }

    @Test
    public void testCheckTaskRemovingAfterFinishing() {
        poolTestSet.checkTaskRemovingAfterFinishing();
    }
}
