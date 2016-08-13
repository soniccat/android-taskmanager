import android.os.Handler;
import android.os.Looper;
import android.test.AndroidTestCase;

import com.example.alexeyglushkov.taskmanager.task.StackTaskProvider;

/**
 * Created by alexeyglushkov on 13.08.16.
 */
public class StackTaskProviderTest extends AndroidTestCase {
    private TaskPoolTest poolTest;
    private TaskProviderTest providerTest;
    private StackTaskProvider taskProvider;

    @Override
    protected void setUp() throws Exception {
        taskProvider = new StackTaskProvider(false, new Handler(Looper.myLooper()), "TestId");

        poolTest = new TaskPoolTest();
        providerTest = new TaskProviderTest();

        poolTest.before(taskProvider);
        providerTest.before(taskProvider);
    }

    public void testGetTopTaskWithBlockedTask() {
        providerTest.getTopTaskWithBlockedTask();
    }

    public void testTakeTopTaskWithBlockedTask() {
        providerTest.takeTopTaskWithBlockedTask();
    }

    // ProviderTests

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
