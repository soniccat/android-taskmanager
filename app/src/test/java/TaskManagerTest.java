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

    public void setMaxLoadingTasks() {
        // Act
        taskManager.setMaxLoadingTasks(100);

        // Verify
        assertEquals(100, taskManager.getMaxLoadingTasks());
    }

    public void getLoadingTaskCount() {
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

    public void addTaskProvider() {
        // Arrange
        TaskProvider taskProvider1 = createTaskProviderMock("provider1", taskManager);
        taskProvider1.setPriority(20);

        TaskProvider taskProvider2 = createTaskProviderMock("provider2", taskManager);
        taskProvider2.setPriority(10);

        // Act
        taskManager.addTaskProvider(taskProvider1);
        taskManager.addTaskProvider(taskProvider2);

        // Verify
        assertEquals(2, taskManager.getTaskProviders().size());
        assertNotNull(taskManager.getTaskProvider("provider1"));
        assertNotNull(taskManager.getTaskProvider("provider2"));

        assertTrue(taskManager.getTaskProviders().indexOf(taskProvider1) == 0);
        assertTrue(taskManager.getTaskProviders().indexOf(taskProvider2) == 1);
    }

    public void removeTaskProvider() {
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

    public void setTaskProviderPriority() {
        // Arrange
        TaskProvider taskProvider1 = createTaskProviderSpy("taskProvider1", taskManager);
        taskProvider1.setPriority(20);

        TaskProvider taskProvider2 = createTaskProviderSpy("taskProvider2", taskManager);
        taskProvider2.setPriority(10);

        // Act
        taskManager.addTaskProvider(taskProvider1);
        taskManager.addTaskProvider(taskProvider2);
        taskManager.setTaskProviderPriority(taskProvider2, 30);

        // Verify 2
        assertTrue(taskManager.getTaskProviders().indexOf(taskProvider1) == 1);
        assertTrue(taskManager.getTaskProviders().indexOf(taskProvider2) == 0);
    }

    public void startImmediately() {
        // Arrange
        Task task = TestTasks.createTaskMock();
        TaskPrivate taskPrivate = task.getPrivate();
        TaskManager.TaskManagerListener listener = Mockito.mock(TaskManager.TaskManagerListener.class);

        // Act
        taskManager.addListener(listener);
        taskManager.startImmediately(task);

        // Verify
        Mockito.verify(taskPrivate).setTaskStatus(Task.Status.Started);
        Mockito.verify(listener, Mockito.never()).onTaskAdded(taskManager, task, false);
        Mockito.verify(listener).onTaskAdded(taskManager, task, true);
    }

    public void startImmediatelyFinish() {
        // Arrange
        TestTask task = new TestTask();
        TaskManager.TaskManagerListener listener = Mockito.mock(TaskManager.TaskManagerListener.class);

        // Act
        taskManager.addListener(listener);
        taskManager.startImmediately(task);
        task.finish();

        // Verify
        assertEquals(Task.Status.Finished, task.getTaskStatus());
        Mockito.verify(listener, Mockito.never()).onTaskAdded(taskManager, task, false);
        Mockito.verify(listener, Mockito.never()).onTaskRemoved(taskManager, task, false);
        Mockito.verify(listener).onTaskAdded(taskManager, task, true);
        Mockito.verify(listener).onTaskRemoved(taskManager, task, true);
        assertEquals(0, taskManager.getTaskCount());
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

    public void addTheSameTaskWithSkipPolicy() {
        // Arrange
        TaskManager.TaskManagerListener listener = Mockito.mock(TaskManager.TaskManagerListener.class);

        Task task1 = TestTasks.createTestTaskSpy("taskId");
        Task task2 = TestTasks.createTestTaskSpy("taskId");

        // Act
        taskManager.addListener(listener);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        // Verify
        assertEquals(Task.Status.Started, task1.getTaskStatus());
        Mockito.verify(task1, Mockito.atLeastOnce()).getTaskId();
        Mockito.verify(listener).onTaskAdded(taskManager, task1, true);
        Mockito.verify(listener).onTaskAdded(taskManager, task1, false);

        assertEquals(Task.Status.Cancelled, task2.getTaskStatus());
        Mockito.verify(task2, Mockito.atLeastOnce()).getTaskId();
        Mockito.verify(listener, Mockito.never()).onTaskAdded(taskManager, task2, true);
        Mockito.verify(listener, Mockito.never()).onTaskAdded(taskManager, task2, false);

        assertEquals(taskManager.getTaskCount(), 1);
        assertTrue(taskManager.getTasks().contains(task1));
    }

    public void addTheSameTaskWithCancelPolicy() {
        // Arrange
        Task task1 = TestTasks.createTestTaskSpy("taskId");
        Task task2 = TestTasks.createTestTaskSpy("taskId");
        task2.setLoadPolicy(Task.LoadPolicy.CancelAdded);

        // Act
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        // Verify
        assertEquals(Task.Status.Started, task1.getTaskStatus());
        assertTrue(task1.getPrivate().getNeedCancelTask());

        assertEquals(Task.Status.Started, task2.getTaskStatus());
        assertFalse(task2.getPrivate().getNeedCancelTask());

        assertEquals(taskManager.getTaskCount(), 2);
        assertTrue(taskManager.getTasks().contains(task1));
        assertTrue(taskManager.getTasks().contains(task2));
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

    private TaskProvider createTaskProviderSpy(String id, TaskManager taskManager) {
        TestTaskProvider taskProvider = new TestTaskProvider(taskManager.getHandler(), id);
        return Mockito.spy(taskProvider);
    }

    private TaskProvider createTaskProviderMock(String id, TaskManager taskManager) {
        TaskProvider provider = Mockito.mock(TaskProvider.class);
        Mockito.when(provider.getTaskProviderId()).thenReturn(id);
        Mockito.when(provider.getHandler()).thenReturn(taskManager.getHandler());
        return provider;
    }
}
