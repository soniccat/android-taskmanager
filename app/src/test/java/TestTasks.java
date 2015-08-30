import com.ga.task.Task;
import com.ga.task.TaskPrivate;

import org.mockito.Mockito;

/**
 * Created by alexeyglushkov on 30.08.15.
 */
public class TestTasks {
    public static Task createTaskMock() {
        return createTaskMock(null, Task.Status.NotStarted);
    }

    public static Task createTaskMock(String id, Task.Status status) {
        Task task = Mockito.mock(Task.class);
        TaskPrivate taskPrivate = Mockito.mock(TaskPrivate.class);

        Mockito.when(task.getTaskStatus()).thenReturn(status);
        Mockito.when(task.getTaskType()).thenReturn(0);
        Mockito.when(task.getPrivate()).thenReturn(taskPrivate);

        if (id != null) {
            Mockito.when(task.getTaskId()).thenReturn(id);
        }

        return task;
    }
}
