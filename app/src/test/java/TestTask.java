import com.ga.task.SimpleTask;
import com.ga.task.Task;

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
