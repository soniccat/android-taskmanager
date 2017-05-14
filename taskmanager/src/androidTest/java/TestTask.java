import android.util.Log;

import com.example.alexeyglushkov.taskmanager.task.SimpleTask;
import com.example.alexeyglushkov.taskmanager.task.Task;

/**
 * Created by alexeyglushkov on 09.08.15.
 */
public class TestTask extends SimpleTask {
    @Override
    public void startTask(Callback callback) {
        setTaskCallback(callback);
    }

    public void finish() {
        getPrivate().handleTaskCompletion(getTaskCallback());
    }
}
