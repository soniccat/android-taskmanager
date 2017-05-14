import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.UiThreadTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;

import com.example.alexeyglushkov.taskmanager.task.StackTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskPool;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * Created by alexeyglushkov on 13.08.16.
 */

@RunWith(AndroidJUnit4.class)
public class StackTaskProviderWithDependentTasksTest {
    private TaskPoolTest poolTest;
    private TaskProviderTest providerTest;
    private StackTaskProvider taskProvider;

    @Rule
    public UiThreadTestRule rule = new UiThreadTestRule();

    @Before
    public void setUp() throws Exception {
        taskProvider = new StackTaskProvider(true, new Handler(Looper.myLooper()), "TestId");

        poolTest = new TaskPoolTest();
        providerTest = new TaskProviderTest();

        poolTest.before(taskProvider);
        providerTest.before(taskProvider);
    }

    // StackTaskProviderTests

    @Test @UiThreadTest
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

    @Test @UiThreadTest
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

    @Test @UiThreadTest
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
