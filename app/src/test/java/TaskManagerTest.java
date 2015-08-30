import android.os.Handler;

import com.ga.task.Task;
import com.ga.task.TaskManager;
import com.ga.task.TaskPool;
import com.ga.task.TaskPrivate;
import com.ga.task.TaskProvider;

import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by alexeyglushkov on 23.08.15.
 */
public class TaskManagerTest {
    protected TaskManager taskManager;

    public void before(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void setMaxLoadingTasksTest() {
        // Act
        taskManager.setMaxLoadingTasks(100);

        // Verify
        assertEquals(100, taskManager.getMaxLoadingTasks());
    }

    public void getLoadingTaskCountTest() {
        // Arrange
        Task task1 = TestTasks.createTaskMock("taskId1");
        Task task2 = TestTasks.createTaskMock("taskId2");
        Task task3 = TestTasks.createTaskMock("taskId3");

        // Act
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        // Verify
        assertEquals(3, taskManager.getLoadingTaskCount());
    }

    public void addTaskProviderTest() {
        // Arrange
        TaskProvider taskProvider1 = createTaskProviderMock("provider1", taskManager);
        TaskProvider taskProvider2 = createTaskProviderMock("provider2", taskManager);

        // Act
        taskManager.addTaskProvider(taskProvider1);
        taskManager.addTaskProvider(taskProvider2);

        // Verify
        assertEquals(2, taskManager.getTaskProviders().size());
        assertNotNull(taskManager.getTaskProvider("provider1"));
        assertNotNull(taskManager.getTaskProvider("provider2"));
    }

    public void removeTaskProviderTest() {
        // Arrange
        TaskProvider taskProvider1 = createTaskProviderMock("provider1", taskManager);
        TaskProvider taskProvider2 = createTaskProviderMock("provider2", taskManager);

        // Act
        taskManager.addTaskProvider(taskProvider1);
        taskManager.addTaskProvider(taskProvider2);
        taskManager.removeTaskProvider(taskProvider1);

        // Verify
        assertEquals(1, taskManager.getTaskProviders().size());
        assertNull(taskManager.getTaskProvider("provider1"));
        assertNotNull(taskManager.getTaskProvider("provider2"));
    }

    public void addTask() {
        // Arrange
        Task task = TestTasks.createTaskMock();
        TaskManager.TaskManagerListener listener = Mockito.mock(TaskManager.TaskManagerListener.class);
        TaskPrivate taskPrivate = task.getPrivate();

        // Act
        taskManager.addListener(listener);
        taskManager.addTask(task);

        // Verify
        Mockito.verify(taskPrivate, Mockito.atLeast(1)).setTaskStatus(Task.Status.Waiting);
        Mockito.verify(listener).onTaskAdded(taskManager, task, false);
        Mockito.verify(listener).onTaskAdded(taskManager, task, true);

        assertEquals(taskManager.getTaskCount(), 1);
        assertTrue(taskManager.getTasks().contains(task));
    }

    public void addStartedTask() {
        // Arrange
        Task task = TestTasks.createTaskMock(null, Task.Status.Started);
        TaskManager.TaskManagerListener listener = Mockito.mock(TaskManager.TaskManagerListener.class);
        TaskPrivate taskPrivate = task.getPrivate();

        // Act
        taskManager.addListener(listener);
        taskManager.addTask(task);

        // Verify
        Mockito.verify(taskPrivate, Mockito.never()).setTaskStatus(Task.Status.Waiting);
        Mockito.verify(task, Mockito.never()).addTaskStatusListener(taskManager);
        Mockito.verify(listener, Mockito.never()).onTaskAdded(taskManager, task, true);
        Mockito.verify(listener, Mockito.never()).onTaskAdded(taskManager, task, false);

        assertEquals(taskManager.getTaskCount(), 0);
        assertFalse(taskManager.getTasks().contains(task));
    }

    public void addStateListener() {
        // Arrange
        Task task = TestTasks.createTaskMock("taskId", Task.Status.NotStarted);
        TaskManager.TaskManagerListener listener1 = Mockito.mock(TaskManager.TaskManagerListener.class);
        TaskManager.TaskManagerListener listener2 = Mockito.mock(TaskManager.TaskManagerListener.class);

        // Act
        taskManager.addListener(listener1);
        taskManager.addListener(listener2);
        taskManager.addTask(task);

        // Verify
        Mockito.verify(listener1).onTaskAdded(taskManager, task, false);
        Mockito.verify(listener1).onTaskAdded(taskManager, task, true);
        Mockito.verify(listener2).onTaskAdded(taskManager, task, false);
        Mockito.verify(listener2).onTaskAdded(taskManager, task, true);
    }

    public void removeStateListener() {
        // Arrange
        Task task = TestTasks.createTaskMock("taskId", Task.Status.NotStarted);
        TaskManager.TaskManagerListener listener1 = Mockito.mock(TaskManager.TaskManagerListener.class);
        TaskManager.TaskManagerListener listener2 = Mockito.mock(TaskManager.TaskManagerListener.class);

        // Act
        taskManager.addListener(listener1);
        taskManager.addListener(listener2);
        taskManager.removeListener(listener1);
        taskManager.removeListener(listener2);
        taskManager.addTask(task);

        // Verify
        Mockito.verify(listener1, Mockito.never()).onTaskAdded(taskManager,task,false);
        Mockito.verify(listener1, Mockito.never()).onTaskAdded(taskManager, task, true);
        Mockito.verify(listener2, Mockito.never()).onTaskAdded(taskManager, task, false);
        Mockito.verify(listener2, Mockito.never()).onTaskAdded(taskManager, task, true);
    }

    public void removeTask() {
        // Arrange
        Task task = TestTasks.createTaskMock("taskId", Task.Status.NotStarted);
        TaskManager.TaskManagerListener listener = Mockito.mock(TaskManager.TaskManagerListener.class);
        TaskPrivate taskPrivate = task.getPrivate();

        // Act
        taskManager.addListener(listener);
        taskManager.addTask(task);
        taskManager.removeTask(task);

        // Verify
        Mockito.verify(listener).onTaskAdded(taskManager, task, false);
        Mockito.verify(listener).onTaskAdded(taskManager, task, true);
        Mockito.verify(listener).onTaskRemoved(taskManager, task, false);
        Mockito.verify(taskPrivate).cancelTask(null);

        assertEquals(1, taskManager.getTaskCount());
    }

    public void removeUnknownTask() {
        // Arrange
        Task task1 = TestTasks.createTaskMock("taskId", Task.Status.NotStarted);
        Task task2 = TestTasks.createTaskMock("taskId", Task.Status.NotStarted);
        TaskManager.TaskManagerListener listener = Mockito.mock(TaskManager.TaskManagerListener.class);

        // Act
        taskManager.addListener(listener);
        taskManager.addTask(task1);
        taskManager.removeTask(task2);

        // Verify
        Mockito.verify(listener).onTaskAdded(taskManager, task1, false);
        Mockito.verify(listener).onTaskAdded(taskManager, task1, true);
        Mockito.verify(listener).onTaskRemoved(taskManager, task1, false);
        Mockito.verify(listener, Mockito.never()).onTaskRemoved(taskManager, task1, true);
        Mockito.verify(listener, Mockito.never()).onTaskAdded(taskManager, task2, false);
        Mockito.verify(listener, Mockito.never()).onTaskAdded(taskManager, task2, true);
        Mockito.verify(listener, Mockito.never()).onTaskRemoved(taskManager, task2, false);
        Mockito.verify(listener, Mockito.never()).onTaskRemoved(taskManager, task2, true);

        assertEquals(1, taskManager.getTaskCount());
    }

    public void checkTaskRemovingAfterFinishing() {
        // Arrange
        TestTask testTask = new TestTask();
        TaskPool taskPoolMock = Mockito.spy(taskManager);

        // Act
        taskPoolMock.addTask(testTask);
        testTask.getPrivate().setTaskStatus(Task.Status.Finished);

        // Verify
        assertEquals(0, taskManager.getTaskCount());
    }

    // Tools

    private TaskProvider createTaskProviderMock(String id, TaskManager taskManager) {
        TaskProvider provider = Mockito.mock(TaskProvider.class);
        Mockito.when(provider.getTaskProviderId()).thenReturn(id);
        Mockito.when(provider.getHandler()).thenReturn(taskManager.getHandler());
        return provider;
    }
}
