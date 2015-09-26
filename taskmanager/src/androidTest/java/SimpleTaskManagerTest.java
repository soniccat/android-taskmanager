import android.os.Handler;
import android.os.Looper;
import android.test.AndroidTestCase;

import com.example.alexeyglushkov.taskmanager.task.SimpleTaskManager;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;

/**
 * Created by alexeyglushkov on 23.08.15.
 */
public class SimpleTaskManagerTest extends AndroidTestCase {

    private TaskPoolTest poolTest;
    private TaskManagerTest taskManagerTest;
    private TaskManager taskManager;

    @Override
    protected void setUp() throws Exception {
        taskManager = new SimpleTaskManager(10, new Handler(Looper.myLooper()));
        poolTest = new TaskPoolTest();
        taskManagerTest = new TaskManagerTest();

        poolTest.before(taskManager);
        taskManagerTest.before(taskManager);
    }

    public void testSetMaxLoadingTasks() {
        taskManagerTest.setMaxLoadingTasks();
    }

    public void testGetLoadingTaskCount() {
        taskManagerTest.getLoadingTaskCount();
    }

    public void testAddTask() {
        taskManagerTest.addTask();
    }

    public void testAddStartedTask() {
        taskManagerTest.addStartedTask();
    }

    public void testAddTheSameTaskWithSkipPolicy() {
        taskManagerTest.addTheSameTaskWithSkipPolicy();
    }

    public void testAddTheSameTaskWithCancelPolicy() {
        taskManagerTest.addTheSameTaskWithCancelPolicy();
    }

    public void testAddStateListener() {
        taskManagerTest.addStateListener();
    }

    public void testRemoveStateListener() {
        taskManagerTest.removeStateListener();
    }

    public void testRemoveTask() {
        taskManagerTest.removeTask();
    }

    public void testRemoveUnknownTask() {
        taskManagerTest.removeUnknownTask();
    }

    public void testCheckTaskRemovingAfterFinishing() {
        taskManagerTest.checkTaskRemovingAfterFinishing();
    }

    public void testGetTaskFromProvider() {
        taskManagerTest.getTaskFromProvider();
    }

    public void testAddTaskProvider() {
        taskManagerTest.addTaskProvider();
    }

    public void testAddTaskProvider2() {
        taskManagerTest.addTaskProvider2();
    }

    public void testAddTaskProviderWithTheSameId() {
        taskManagerTest.addTaskProviderWithTheSameId();
    }

    public void testRemoveTaskProvider() {
        taskManagerTest.removeTaskProvider();
    }

    public void testSetTaskExecutor() {
        taskManagerTest.setTaskExecutor();
    }

    public void testStartImmediately() {
        taskManagerTest.startImmediately();
    }

    public void testStartImmediatelySkipPolicy() {
        taskManagerTest.startImmediatelySkipPolicy();
    }

    public void testStartImmediatelyFinish() {
        taskManagerTest.startImmediatelyFinish();
    }

    public void testSetTaskProviderPriority() {
        taskManagerTest.setTaskProviderPriority();
    }

    public void testSetGetHandler() {
        taskManagerTest.setGetHandler();
    }

    public void testGetTaskCount() {
        taskManagerTest.getTaskCount();
    }

    public void testGetTasks() {
        taskManagerTest.getTasks();
    }

    public void testSetLimit() {
        taskManagerTest.setLimit();
    }

    public void testSetLimitRemove() {
        taskManagerTest.setLimitRemove();
    }

    // PoolTests

    public void testGetTask() {
        poolTest.getTask();
    }

    public void testGetUnknownTask() {
        poolTest.getUnknownTask();
    }

    public void testSetGetUserData() {
        poolTest.setGetUserData();
    }
}
