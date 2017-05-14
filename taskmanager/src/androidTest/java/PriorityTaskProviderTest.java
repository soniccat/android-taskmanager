import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.UiThreadTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.alexeyglushkov.taskmanager.task.PriorityTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskProvider;

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

    private TaskPoolTest poolTest;
    private TaskProviderTest providerTest;
    private PriorityTaskProvider taskProvider;

    @Before
    public void setUp() throws Exception {
        taskProvider = new PriorityTaskProvider(new Handler(Looper.myLooper()), "TestId");

        poolTest = new TaskPoolTest();
        providerTest = new TaskProviderTest();

        poolTest.before(taskProvider);
        providerTest.before(taskProvider);
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
