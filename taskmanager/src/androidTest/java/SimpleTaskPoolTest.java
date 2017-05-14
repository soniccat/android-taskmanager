import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.UiThreadTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.alexeyglushkov.taskmanager.task.SimpleTaskPool;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by alexeyglushkov on 08.08.15.
 */

@RunWith(AndroidJUnit4.class)
public class SimpleTaskPoolTest extends TaskPoolTest {

    @Rule
    public UiThreadTestRule rule = new UiThreadTestRule();

    @UiThreadTest @Before
    public void setUp() throws Exception{
        SimpleTaskPoolTest.this.before(new SimpleTaskPool(new Handler(Looper.myLooper())));
    }

    @Test @UiThreadTest
    public void testSetGetHandler() {
        SimpleTaskPoolTest.this.setGetHandler();
    }

    @Test @UiThreadTest
    public void testAddTask() {
        SimpleTaskPoolTest.this.addTask();
    }

    @Test @UiThreadTest
    public void testAddStartedTask() {
        SimpleTaskPoolTest.this.addStartedTask();
    }

    @Test @UiThreadTest
    public void testRemoveTask() {
        SimpleTaskPoolTest.this.removeTask();
    }

    @Test @UiThreadTest
    public void testRemoveUnknownTask() {
        SimpleTaskPoolTest.this.removeUnknownTask();
    }

    @Test @UiThreadTest
    public void testGetTask() {
        SimpleTaskPoolTest.this.getTask();
    }

    @Test @UiThreadTest
    public void testGetUnknownTask() {
        SimpleTaskPoolTest.this.getUnknownTask();
    }

    @Test @UiThreadTest
    public void testGetTaskCount() {
        SimpleTaskPoolTest.this.getTaskCount();
    }

    @Test @UiThreadTest
    public void testGetTaskCount2() {
        SimpleTaskPoolTest.this.getTaskCount2();
    }

    @Test @UiThreadTest
    public void testSetGetUserData() {
        SimpleTaskPoolTest.this.setGetUserData();
    }

    @Test @UiThreadTest
    public void testAddStateListener() {
        SimpleTaskPoolTest.this.addStateListener();
    }

    @Test @UiThreadTest
    public void testRemoveStateListener() {
        SimpleTaskPoolTest.this.removeStateListener();
    }

    @Test @UiThreadTest
    public void testChangeTaskStatus() {
        SimpleTaskPoolTest.this.changeTaskStatus();
    }

    @Test @UiThreadTest
    public void testCheckTaskRemovingAfterFinishing() {
        SimpleTaskPoolTest.this.checkTaskRemovingAfterFinishing();
    }
}
