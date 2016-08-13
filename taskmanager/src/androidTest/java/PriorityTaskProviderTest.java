import android.os.Handler;
import android.os.Looper;
import android.test.AndroidTestCase;

import com.example.alexeyglushkov.taskmanager.task.PriorityTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskProvider;

import org.mockito.Mockito;

import java.util.Arrays;

/**
 * Created by alexeyglushkov on 09.08.15.
 */
public class PriorityTaskProviderTest extends AndroidTestCase {

    private TaskPoolTest poolTest;
    private TaskProviderTest providerTest;
    private PriorityTaskProvider taskProvider;

    @Override
    protected void setUp() throws Exception {
        taskProvider = new PriorityTaskProvider(new Handler(Looper.myLooper()), "TestId");

        poolTest = new TaskPoolTest();
        providerTest = new TaskProviderTest();

        poolTest.before(taskProvider);
        providerTest.before(taskProvider);
    }

    // PriorityTaskProviderTests

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
        taskProvider.updatePriorities(new PriorityTaskProvider.PriorityProvider() {
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
