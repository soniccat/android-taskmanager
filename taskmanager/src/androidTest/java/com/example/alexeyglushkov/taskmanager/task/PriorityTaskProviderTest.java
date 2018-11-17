package com.example.alexeyglushkov.taskmanager.task;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.test.annotation.UiThreadTest;
import androidx.test.rule.UiThreadTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static junit.framework.Assert.assertEquals;

import org.mockito.Mockito;

import java.util.Arrays;

/**
 * Created by alexeyglushkov on 09.08.15.
 */

@RunWith(AndroidJUnit4.class)
public class PriorityTaskProviderTest {

    @Rule
    public UiThreadTestRule rule = new UiThreadTestRule();

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

    @Test @UiThreadTest
    public void testUpdatePriorities() {
        // Arrange
        TaskProvider.TaskPoolListener listener = Mockito.mock(TaskProvider.TaskPoolListener.class);

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

    @Test @UiThreadTest
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

    @Test @UiThreadTest
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
        poolTestSet.setGetHandler();
    }

    @Test @UiThreadTest
    public void testAddTask() {
        poolTestSet.addTask();
    }

    @Test @UiThreadTest
    public void testAddTaskAddStatusListener() {
        poolTestSet.addTaskAddStatusListener();
    }

    @Test @UiThreadTest
    public void testAddStartedTask() {
        poolTestSet.addStartedTask();
    }

    @Test @UiThreadTest
    public void testRemoveTask() {
        poolTestSet.removeTask();
    }

    @Test @UiThreadTest
    public void testRemoveUnknownTask() {
        poolTestSet.removeUnknownTask();
    }

    @Test @UiThreadTest
    public void testGetTask() {
        poolTestSet.getTask();
    }

    @Test @UiThreadTest
    public void testGetUnknownTask() {
        poolTestSet.getUnknownTask();
    }

    @Test @UiThreadTest
    public void testGetTaskCount() {
        poolTestSet.getTaskCount();
    }

    @Test @UiThreadTest
    public void testGetTaskCount2() {
        poolTestSet.getTaskCount2();
    }

    @Test @UiThreadTest
    public void testSetGetUserData() {
        poolTestSet.setGetUserData();
    }

    @Test @UiThreadTest
    public void testAddStateListener() {
        poolTestSet.addStateListener();
    }

    @Test @UiThreadTest
    public void testRemoveStateListener() {
        poolTestSet.removeStateListener();
    }

    @Test @UiThreadTest
    public void testChangeTaskStatus() {
        poolTestSet.changeTaskStatus();
    }

    @Test @UiThreadTest
    public void testCheckTaskRemovingAfterFinishing() {
        poolTestSet.checkTaskRemovingAfterFinishing();
    }
}
