import android.os.Handler;
import android.os.Looper;

import com.ga.task.SimpleTaskPool;

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
    public void setGetHandler() {
        super.setGetHandler();
    }

    @Test
    public void addTask() {
        super.addTask();
    }

    @Test
    public void addStartedTask() {
        super.addStartedTask();
    }

    @Test
    public void removeTask() {
        super.removeTask();
    }

    @Test
    public void removeUnknownTask() {
        super.removeUnknownTask();
    }

    @Test
    public void getTask() {
        super.getTask();
    }

    @Test
    public void getUnknownTask() {
        super.getUnknownTask();
    }

    @Test
    public void getTaskCount() {
        super.getTaskCount();
    }

    @Test
    public void getTaskCount2() {
        super.getTaskCount2();
    }

    @Test
    public void setGetUserData() {
        super.setGetUserData();
    }

    @Test
    public void addStateListener() {
        super.addStateListener();
    }

    @Test
    public void removeStateListener() {
        super.removeStateListener();
    }

    @Test
    public void changeTaskStatus() {
        super.changeTaskStatus();
    }

    @Test
    public void checkTaskRemovingAfterFinishing() {
        super.checkTaskRemovingAfterFinishing();
    }
}
