import android.util.Log;

import com.example.alexeyglushkov.taskmanager.task.SimpleTask;
import com.example.alexeyglushkov.taskmanager.task.Task;

/**
 * Created by alexeyglushkov on 09.08.15.
 */
public class TestTask extends SimpleTask {

    @Override
    public void startTask(Callback callback) {
        super.startTask(callback);
        Log.d("TestTask", "startTask " + getStartCallback());
    }

    public void finish() {
        Log.d("TestTask", "finish " + getStartCallback());
        getPrivate().handleTaskCompletion(getStartCallback());
    }
}
