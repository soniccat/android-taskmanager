import com.taskmanager.task.SimpleTask;

/**
 * Created by alexeyglushkov on 09.08.15.
 */
public class TestTask extends SimpleTask {
    @Override
    public void startTask() {

    }

    public void finish() {
        getPrivate().handleTaskCompletion();
    }
}
