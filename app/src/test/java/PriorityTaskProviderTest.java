import android.os.Handler;
import android.os.Looper;

import com.ga.task.PriorityTaskProvider;
import com.ga.task.Task;
import com.ga.task.TaskProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by alexeyglushkov on 09.08.15.
 */
public class PriorityTaskProviderTest {

    private TaskPoolTest poolTest;
    private TaskProviderTest providerTest;
    private PriorityTaskProvider taskProvider;

    @Before
    public void before() {
        taskProvider = new PriorityTaskProvider(new Handler(Looper.myLooper()), "TestId");

        poolTest = new TaskPoolTest();
        providerTest = new TaskProviderTest();

        poolTest.before(taskProvider);
        providerTest.before(taskProvider);
    }

    // PriorityTaskProviderTests

    @Test
    public void updatePriorities() {
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

    // ProviderTests

    @Test
    public void setProviderId() {
        providerTest.setProviderId();
    }

    @Test
    public void setPriority() {
        providerTest.setPriority();
    }

    @Test
    public void getTopTaskWithoutFilter() {
        providerTest.getTopTaskWithoutFilter();
    }

    @Test
    public void getTopTaskWithPriorityWithoutFilter() {
        providerTest.getTopTaskWithPriorityWithoutFilter();
    }

    @Test
    public void getTopTask() {
        providerTest.getTopTask();
    }

    @Test
    public void getTopTaskWithPriority() {
        providerTest.getTopTaskWithPriority();
    }

    @Test
    public void takeTopTask() {
        providerTest.takeTopTask();
    }

    @Test
    public void removeTaskWithUnknownType() {
        providerTest.removeTaskWithUnknownType();
    }

    // PoolTests

    @Test
    public void setGetHandler() {
        poolTest.setGetHandler();
    }

    @Test
    public void addTask() {
        poolTest.addTask();
    }

    @Test
    public void addStartedTask() {
        poolTest.addStartedTask();
    }

    @Test
    public void removeTask() {
        poolTest.removeTask();
    }

    @Test
    public void removeUnknownTask() {
        poolTest.removeUnknownTask();
    }

    @Test
    public void getTask() {
        poolTest.getTask();
    }

    @Test
    public void getUnknownTask() {
        poolTest.getUnknownTask();
    }

    @Test
    public void getTaskCount() {
        poolTest.getTaskCount();
    }

    @Test
    public void setGetUserData() {
        poolTest.setGetUserData();
    }

    @Test
    public void addStateListener() {
        poolTest.addStateListener();
    }

    @Test
    public void removeStateListener() {
        poolTest.removeStateListener();
    }

    @Test
    public void changeTaskStatus() {
        poolTest.changeTaskStatus();
    }

    @Test
    public void checkTaskRemovingAfterFinishing() {
        poolTest.checkTaskRemovingAfterFinishing();
    }
}
