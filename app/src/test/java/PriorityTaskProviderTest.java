import android.os.Handler;
import android.os.Looper;

import com.ga.task.PriorityTaskProvider;
import com.ga.task.TaskPool;
import com.ga.task.TaskProvider;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by alexeyglushkov on 09.08.15.
 */
public class PriorityTaskProviderTest {

    private TaskPoolTest poolTest;
    private TaskProviderTest providerTest;

    @Before
    public void before() {
        PriorityTaskProvider provider = new PriorityTaskProvider(new Handler(Looper.myLooper()));

        poolTest = new TaskPoolTest();
        providerTest = new TaskProviderTest();

        poolTest.before(provider);
        providerTest.before(provider);
    }


    // ProviderTests

    @Test
    public void getTopTaskWithoutFilter() {
        providerTest.getTopTaskWithoutFilter();
    }

    @Test
    public void getTopTaskWithPriorityWithoutFilter() {
        providerTest.getTopTaskWithPriorityWithoutFilter();
    }

    @Test
    public void getTopTask() {
        providerTest.getTopTask();
    }

    @Test
    public void getTopTaskWithPriority() {
        providerTest.getTopTaskWithPriority();
    }

    @Test
    public void takeTopTask() {
        providerTest.takeTopTask();
    }

    @Test
    public void removeTaskWithUnknownType() {
        providerTest.removeTaskWithUnknownType();
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
