import com.taskmanager.task.SimpleTaskManager;
import com.taskmanager.task.TaskManager;

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
    public void setMaxLoadingTasks() {
        taskManagerTest.setMaxLoadingTasks();
    }

    @Test
    public void getLoadingTaskCount() {
        taskManagerTest.getLoadingTaskCount();
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
    public void addTheSameTaskWithSkipPolicy() {
        taskManagerTest.addTheSameTaskWithSkipPolicy();
    }

    @Test
    public void addTheSameTaskWithCancelPolicy() {
        taskManagerTest.addTheSameTaskWithCancelPolicy();
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

    @Test
    public void getTaskFromProvider() {
        taskManagerTest.getTaskFromProvider();
    }

    @Test
    public void addTaskProvider() {
        taskManagerTest.addTaskProvider();
    }

    @Test
    public void addTaskProvider2() {
        taskManagerTest.addTaskProvider2();
    }

    @Test
    public void addTaskProviderWithTheSameId() {
        taskManagerTest.addTaskProviderWithTheSameId();
    }

    @Test
    public void removeTaskProvider() {
        taskManagerTest.removeTaskProvider();
    }

    @Test
    public void setTaskExecutor() {
        taskManagerTest.setTaskExecutor();
    }

    @Test
    public void startImmediately() {
        taskManagerTest.startImmediately();
    }

    @Test
    public void startImmediatelySkipPolicy() {
        taskManagerTest.startImmediatelySkipPolicy();
    }

    @Test
    public void startImmediatelyFinish() {
        taskManagerTest.startImmediatelyFinish();
    }

    @Test
    public void setTaskProviderPriority() {
        taskManagerTest.setTaskProviderPriority();
    }

    @Test
    public void setGetHandler() {
        taskManagerTest.setGetHandler();
    }

    @Test
    public void getTaskCount() {
        taskManagerTest.getTaskCount();
    }

    @Test
    public void getTasks() {
        taskManagerTest.getTasks();
    }

    @Test
    public void setLimit() {
        taskManagerTest.setLimit();
    }

    @Test
    public void setLimitRemove() {
        taskManagerTest.setLimitRemove();
    }

    // PoolTests

    @Test
    public void getTask() {
        poolTest.getTask();
    }

    @Test
    public void getUnknownTask() {
        poolTest.getUnknownTask();
    }

    @Test
    public void setGetUserData() {
        poolTest.setGetUserData();
    }
}
