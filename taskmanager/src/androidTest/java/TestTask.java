import android.util.Log;

import com.example.alexeyglushkov.taskmanager.task.SimpleTask;
import com.example.alexeyglushkov.taskmanager.task.Task;

/**
 * Created by alexeyglushkov on 09.08.15.
 */
public class TestTask extends SimpleTask {
    private Callback storedCallback;

    @Override
    public void startTask(Callback callback) {
        storedCallback = callback;
        Log.d("RRRRRR", "1 " + storedCallback);
    }

    public void finish() {
        Log.d("RRRRRR", "2 " + storedCallback);
        getPrivate().handleTaskCompletion(storedCallback);
    }
}
