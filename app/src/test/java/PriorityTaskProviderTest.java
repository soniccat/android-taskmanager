import android.os.Handler;
import android.os.Looper;

import com.ga.task.PriorityTaskProvider;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by alexeyglushkov on 09.08.15.
 */
public class PriorityTaskProviderTest extends TaskPoolTest {

    @Before
    public void prepare() {
        super.before(new PriorityTaskProvider(new Handler(Looper.myLooper())));
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
