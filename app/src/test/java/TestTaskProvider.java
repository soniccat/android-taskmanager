import android.os.Handler;

import com.taskmanager.task.PriorityTaskProvider;

/**
 * Created by alexeyglushkov on 30.08.15.
 */
public class TestTaskProvider extends PriorityTaskProvider {
    public TestTaskProvider(Handler handler, String id) {
        super(handler, id);
    }


}
