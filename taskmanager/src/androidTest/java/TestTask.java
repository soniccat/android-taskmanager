import com.example.alexeyglushkov.taskmanager.task.SimpleTask;

/**
 * Created by alexeyglushkov on 09.08.15.
 */
public class TestTask extends SimpleTask {
    private Callback callback;

    @Override
    public void startTask(Callback callback) {
        this.callback = callback;
    }

    public void finish() {
        getPrivate().handleTaskCompletion(this.callback);
    }
}
