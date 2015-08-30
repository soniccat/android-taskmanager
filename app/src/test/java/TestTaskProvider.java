import android.os.Handler;

import com.ga.task.PriorityTaskProvider;
import com.ga.task.Task;
import com.ga.task.TaskPool;
import com.ga.task.TaskProvider;

import java.util.List;

/**
 * Created by alexeyglushkov on 30.08.15.
 */
public class TestTaskProvider extends PriorityTaskProvider {
    public TestTaskProvider(Handler handler, String id) {
        super(handler, id);
    }


}
