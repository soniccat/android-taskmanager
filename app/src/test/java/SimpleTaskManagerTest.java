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
        //taskManager.setHandler(new Handler(Looper.myLooper()));


        poolTest = new TaskPoolTest();
        taskManagerTest = new TaskManagerTest();

        poolTest.before(taskManager);
        taskManagerTest.before(taskManager);
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
