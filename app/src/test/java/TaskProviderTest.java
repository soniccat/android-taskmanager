import android.os.Handler;
import android.os.HandlerThread;

import com.ga.task.Task;
import com.ga.task.TaskPool;
import com.ga.task.TaskPrivate;
import com.ga.task.TaskProvider;

import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Created by alexeyglushkov on 15.08.15.
 */
public class TaskProviderTest {

    protected TaskProvider taskProvider;

    public void before(TaskProvider taskProvider) {
        this.taskProvider = taskProvider;
    }

    public void setProviderId() {
        // Act
        taskProvider.setTaskProviderId("testId");

        // Verify
        assertEquals("testId", taskProvider.getTaskProviderId());
    }

    public void setPriority() {
        // Act
        taskProvider.setPriority(12);

        // Verify
        assertEquals(12, taskProvider.getPriority());
    }

    public void getTopTaskWithoutFilter() {

        // Arrange
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 1));
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 2));
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 1));
        taskProvider.addTask(TestTasks.createTestTaskSpy("d", 2));
        taskProvider.addTask(TestTasks.createTestTaskSpy("e", 1));
        taskProvider.addTask(TestTasks.createTestTaskSpy("f", 2));

        // Act
        Task task = taskProvider.getTopTask(null);

        // Verify
        assertTrue(task.getTaskId().equals("a"));
    }

    public void getTopTaskWithPriorityWithoutFilter() {

        // Arrange
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 1, 1));
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 2, 2));
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 1, 3));
        taskProvider.addTask(TestTasks.createTestTaskSpy("d", 2, 4));
        taskProvider.addTask(TestTasks.createTestTaskSpy("e", 1, 5));
        taskProvider.addTask(TestTasks.createTestTaskSpy("f", 2, 6));

        // Act
        Task task = taskProvider.getTopTask(null);

        // Verify
        assertTrue(task.getTaskId().equals("f"));
    }

    public void getTopTask() {

        // Arrange
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 1));
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 2));
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 3));
        taskProvider.addTask(TestTasks.createTestTaskSpy("d", 2));
        taskProvider.addTask(TestTasks.createTestTaskSpy("e", 1));
        taskProvider.addTask(TestTasks.createTestTaskSpy("f", 3));

        // Act
        Task task = taskProvider.getTopTask(Arrays.asList(new Integer[]{1}));

        // Verify
        assertTrue(task.getTaskId().equals("b"));
    }

    public void getTopTaskWithPriority() {

        // Arrange
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 1, 1));
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 2, 2));
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 3, 3));
        taskProvider.addTask(TestTasks.createTestTaskSpy("d", 2, 4));
        taskProvider.addTask(TestTasks.createTestTaskSpy("e", 1, 5));
        taskProvider.addTask(TestTasks.createTestTaskSpy("f", 3, 6));

        // Act
        Task task = taskProvider.getTopTask(Arrays.asList(new Integer[]{3}));

        // Verify
        assertTrue(task.getTaskId().equals("e"));
    }

    public void takeTopTask() {
        // Arrange
        TaskProvider.TaskPoolListener listener = Mockito.mock(TaskProvider.TaskPoolListener.class);

        taskProvider.addListener(listener);
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 1, 1));
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 2, 2));
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 3, 3));
        taskProvider.addTask(TestTasks.createTestTaskSpy("d", 2, 4));
        taskProvider.addTask(TestTasks.createTestTaskSpy("e", 1, 5));
        taskProvider.addTask(TestTasks.createTestTaskSpy("f", 3, 6));

        // Act
        Task task = taskProvider.takeTopTask(Arrays.asList(new Integer[]{3}));

        // Verify
        Mockito.verify(listener).onTaskRemoved(taskProvider, task);

        assertEquals("e", task.getTaskId());
        assertEquals(5, taskProvider.getTaskCount());
        assertEquals(null, taskProvider.getTask("e"));
    }

    public void removeTaskWithUnknownType() {
        // Arrange
        Task task1 = Mockito.mock(Task.class);
        TaskPrivate taskPrivate1 = Mockito.mock(TaskPrivate.class);
        TaskPool.TaskPoolListener listener = Mockito.mock(TaskPool.TaskPoolListener.class);

        Mockito.when(task1.getTaskStatus()).thenReturn(Task.Status.NotStarted);
        Mockito.when(task1.getTaskId()).thenReturn("taskId");
        Mockito.when(task1.getPrivate()).thenReturn(taskPrivate1);
        Mockito.when(task1.getTaskType()).thenReturn(1);

        Task task2 = Mockito.mock(Task.class);
        TaskPrivate taskPrivate2 = Mockito.mock(TaskPrivate.class);

        Mockito.when(task2.getTaskStatus()).thenReturn(Task.Status.NotStarted);
        Mockito.when(task2.getTaskId()).thenReturn("taskId");
        Mockito.when(task2.getPrivate()).thenReturn(taskPrivate2);
        Mockito.when(task2.getTaskType()).thenReturn(2);

        // Act
        taskProvider.addListener(listener);
        taskProvider.addTask(task1);
        taskProvider.removeTask(task2);

        // Verify
        Mockito.verify(listener).onTaskAdded(taskProvider, task1);
        Mockito.verify(listener, Mockito.never()).onTaskRemoved(taskProvider, task1);
        Mockito.verify(listener, Mockito.never()).onTaskAdded(taskProvider, task2);
        Mockito.verify(listener, Mockito.never()).onTaskRemoved(taskProvider, task2);

        assertEquals(1, taskProvider.getTaskCount());
    }
}
