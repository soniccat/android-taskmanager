import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.ga.task.PriorityTaskProvider;
import com.ga.task.SimpleTaskManager;
import com.ga.task.TaskManager;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by alexeyglushkov on 23.08.15.
 */
public class SimpleTaskManagerTest {

    private TaskPoolTest poolTest;
    private TaskManagerTest taskManagerTest;
    private TaskManager taskManager;

    @Before
    public void before() {
        taskManager = new SimpleTaskManager(10);
        poolTest = new TaskPoolTest();
        taskManagerTest = new TaskManagerTest();

        poolTest.before(taskManager);
        taskManagerTest.before(taskManager);
    }

    @Test
    public void setMaxLoadingTasksTest() {
        taskManagerTest.setMaxLoadingTasksTest();
    }

    @Test
    public void addTask() {
        taskManagerTest.addTask();
    }

    @Test
    public void addStartedTask() {
        taskManagerTest.addStartedTask();
    }

    @Test
    public void addStateListener() {
        taskManagerTest.addStateListener();
    }

    @Test
    public void removeStateListener() {
        taskManagerTest.removeStateListener();
    }

    @Test
    public void removeTask() {
        taskManagerTest.removeTask();
    }

    @Test
    public void removeUnknownTask() {
        taskManagerTest.removeUnknownTask();
    }

    @Test
    public void checkTaskRemovingAfterFinishing() {
        taskManagerTest.checkTaskRemovingAfterFinishing();
    }

    // PoolTests

    @Test
    public void setGetHandler() {
        poolTest.setGetHandler();
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
}
