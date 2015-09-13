import com.ga.task.Task;
import com.ga.task.TaskImpl;
import com.ga.task.TaskPrivate;

import org.mockito.Mockito;

/**
 * Created by alexeyglushkov on 30.08.15.
 */
public class TestTasks {
    public static Task createTaskMock() {
        return createTaskMock(null, Task.Status.NotStarted);
    }

    public static Task createTaskMock(String id) {
        return createTaskMock(id, Task.Status.NotStarted);
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

    public static TestTask createTestTaskSpy(String id) {
        return TestTasks.createTestTaskSpy(id, 0, 0);
    }

    public static TestTask createTestTaskSpy(String id, int priority) {
        return TestTasks.createTestTaskSpy(id, priority, 0);
    }

    public static TestTask createTestTaskSpy(String id, int type, int priority) {
        TestTask testTask = Mockito.spy(new TestTask());
        testTask.setTaskId(id);
        testTask.setTaskPriority(priority);
        testTask.setTaskType(type);

        TaskImpl taskPrivate = (TaskImpl)Mockito.spy(testTask.getPrivate());
        Mockito.when(testTask.getPrivate()).thenReturn(taskPrivate);
        Mockito.when(taskPrivate.getOuterTask()).thenReturn(testTask);

        return testTask;
    }
}
