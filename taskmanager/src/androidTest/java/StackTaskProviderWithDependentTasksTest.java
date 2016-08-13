import android.os.Handler;
import android.os.Looper;
import android.test.AndroidTestCase;

import com.example.alexeyglushkov.taskmanager.task.StackTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskPool;

import org.mockito.Mockito;

/**
 * Created by alexeyglushkov on 13.08.16.
 */
public class StackTaskProviderWithDependentTasksTest extends AndroidTestCase {
    private TaskPoolTest poolTest;
    private TaskProviderTest providerTest;
    private StackTaskProvider taskProvider;

    @Override
    protected void setUp() throws Exception {
        taskProvider = new StackTaskProvider(true, new Handler(Looper.myLooper()), "TestId");

        poolTest = new TaskPoolTest();
        providerTest = new TaskProviderTest();

        poolTest.before(taskProvider);
        providerTest.before(taskProvider);
    }

    // StackTaskProviderTests

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

    public void testGetTopTaskWithBlockedTask() {
        providerTest.getTopTaskWithBlockedTask();
    }

    public void testTakeTopTaskWithBlockedTask() {
        providerTest.takeTopTaskWithBlockedTask();
    }

    public void testSetProviderId() {
        providerTest.setProviderId();
    }

    public void testSetPriority() {
        providerTest.setPriority();
    }

    public void testGetTopTaskWithoutFilter() {
        providerTest.getTopTaskWithoutFilter();
    }

    public void testGetTopTaskWithFilter() {
        providerTest.getTopTaskWithFilter();
    }

    public void testTakeTopTaskWithFilter() {
        providerTest.takeTopTaskWithFilter();
    }

    public void testRemoveTaskWithUnknownType() {
        providerTest.removeTaskWithUnknownType();
    }

    // PoolTests

    public void testSetGetHandler() {
        poolTest.setGetHandler();
    }

    public void testAddTask() {
        poolTest.addTask();
    }

    public void testAddStartedTask() {
        poolTest.addStartedTask();
    }

    public void testRemoveTask() {
        poolTest.removeTask();
    }

    public void testRemoveUnknownTask() {
        poolTest.removeUnknownTask();
    }

    public void testGetTask() {
        poolTest.getTask();
    }

    public void testGetUnknownTask() {
        poolTest.getUnknownTask();
    }

    public void testGetTaskCount() {
        poolTest.getTaskCount();
    }

    public void testGetTaskCount2() {
        poolTest.getTaskCount2();
    }

    public void testSetGetUserData() {
        poolTest.setGetUserData();
    }

    public void testAddStateListener() {
        poolTest.addStateListener();
    }

    public void testRemoveStateListener() {
        poolTest.removeStateListener();
    }

    public void testChangeTaskStatus() {
        poolTest.changeTaskStatus();
    }

    public void testCheckTaskRemovingAfterFinishing() {
        poolTest.checkTaskRemovingAfterFinishing();
    }
}
