import android.os.Handler;
import android.os.Looper;
import android.support.test.runner.AndroidJUnit4;

import com.example.alexeyglushkov.taskmanager.task.RestorableTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.StackTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskPool;
import com.example.alexeyglushkov.taskmanager.task.TaskPrivate;
import com.example.alexeyglushkov.taskmanager.task.TaskProvider;

import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by alexeyglushkov on 18.06.17.
 */

@RunWith(AndroidJUnit4.class)
public class StackTaskWithRestorableProviderTest extends StackTaskProviderTest{
    @Override
    protected TaskProvider prepareTaskProvider() {
        return new RestorableTaskProvider(new StackTaskProvider(false, new Handler(Looper.myLooper()), "TestId"));
    }
}
