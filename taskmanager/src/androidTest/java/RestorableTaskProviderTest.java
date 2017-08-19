import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.test.rule.UiThreadTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.alexeyglushkov.taskmanager.task.PriorityTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.RestorableTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.TaskProvider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * Created by alexeyglushkov on 19.08.17.
 */

@RunWith(AndroidJUnit4.class)
public class RestorableTaskProviderTest {
    @Rule
    public UiThreadTestRule rule = new UiThreadTestRule();

    @Before
    public void setUp() throws Exception {
    }

    @NonNull
    protected TaskProvider prepareTaskProvider(TaskProvider taskProvider) {
        return new RestorableTaskProvider(taskProvider);
    }
}
