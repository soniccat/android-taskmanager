import android.util.Log;

import com.example.alexeyglushkov.taskmanager.task.SimpleTask;
import com.example.alexeyglushkov.taskmanager.task.Task;

/**
 * Created by alexeyglushkov on 09.08.15.
 */
public class TestTask extends SimpleTask {

    @Override
    public void startTask(Callback callback) {
        getPrivate().handleTaskStart(callback);
        Log.d("RRRRRR", "1 " + getStartCallback());
    }

    public void finish() {
        Log.d("RRRRRR", "2 " + getStartCallback());
        getPrivate().handleTaskCompletion(getStartCallback());
    }
}
