import android.os.Handler;
import android.os.Looper;

import com.ga.task.SimpleTaskPool;
import com.ga.task.TaskPool;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by alexeyglushkov on 08.08.15.
 */
public class SimpleTaskPoolTest extends TaskPoolTest {

    @Before
    public void prepare() {
        super.before(new SimpleTaskPool(new Handler(Looper.myLooper())));
    }

    @Test
    public void testSetGetHandler() {
        super.testSetGetHandler();
    }

    @Test
    public void testAddTask() {
        super.testAddTask();
    }

    @Test
    public void addTheSameTaskWithSkipPolicy() {
        super.addTheSameTaskWithSkipPolicy();
    }

    @Test
    public void addTheSameTaskWithCancelPolicy() {
        super.addTheSameTaskWithCancelPolicy();
    }

    @Test
    public void testGetTask() {
        super.testGetTask();
    }

    @Test
    public void testGetTaskCount() {
        super.testGetTaskCount();
    }

    @Test
    public void testSetGetUserData() {
        super.testSetGetUserData();
    }

    @Test
    public void testAddStateListener() {
        super.testAddStateListener();
    }

    @Test
    public void testRemoveStateListener() {
        super.testRemoveStateListener();
    }
}
