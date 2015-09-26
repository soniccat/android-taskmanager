import android.os.Handler;
import android.os.Looper;

import com.example.alexeyglushkov.taskmanager.task.SimpleTaskPool;

/**
 * Created by alexeyglushkov on 08.08.15.
 */
public class SimpleTaskPoolTest extends TaskPoolTest {

    @Override
    protected void setUp() throws Exception{
        super.before(new SimpleTaskPool(new Handler(Looper.myLooper())));
    }

    public void testSetGetHandler() {
        super.setGetHandler();
    }

    public void testAddTask() {
        super.addTask();
    }

    public void testAddStartedTask() {
        super.addStartedTask();
    }

    public void testRemoveTask() {
        super.removeTask();
    }

    public void testRemoveUnknownTask() {
        super.removeUnknownTask();
    }

    public void testGetTask() {
        super.getTask();
    }

    public void testGetUnknownTask() {
        super.getUnknownTask();
    }

    public void testGetTaskCount() {
        super.getTaskCount();
    }

    public void testGetTaskCount2() {
        super.getTaskCount2();
    }

    public void testSetGetUserData() {
        super.setGetUserData();
    }

    public void testAddStateListener() {
        super.addStateListener();
    }

    public void testRemoveStateListener() {
        super.removeStateListener();
    }

    public void testChangeTaskStatus() {
        super.changeTaskStatus();
    }

    public void testCheckTaskRemovingAfterFinishing() {
        super.checkTaskRemovingAfterFinishing();
    }
}
