import com.ga.task.Task;
import com.ga.task.TaskManager;
import com.ga.task.TaskPool;
import com.ga.task.TaskPrivate;

import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

        // Act


        // Verify

    }

    public void addTask(){
        // Arrange
        Task task = createTaskMock();
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
        Task task = createTaskMock(null, Task.Status.Started);
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
        Task task = createTaskMock("taskId", Task.Status.NotStarted);
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
        Task task = Mockito.mock(Task.class);
        TaskPrivate taskPrivate1 = Mockito.mock(TaskPrivate.class);

        Mockito.when(task.getTaskStatus()).thenReturn(Task.Status.NotStarted);
        Mockito.when(task.getTaskId()).thenReturn("taskId");
        Mockito.when(task.getPrivate()).thenReturn(taskPrivate1);

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
        Mockito.verify(listener1, Mockito.never()).onTaskAdded(taskManager,task,true);
        Mockito.verify(listener2, Mockito.never()).onTaskAdded(taskManager,task,false);
        Mockito.verify(listener2, Mockito.never()).onTaskAdded(taskManager,task,true);
    }

    public void removeTask() {
        // Arrange
        Task task = Mockito.mock(Task.class);
        TaskPrivate taskPrivate = Mockito.mock(TaskPrivate.class);
        TaskManager.TaskManagerListener listener = Mockito.mock(TaskManager.TaskManagerListener.class);

        Mockito.when(task.getTaskStatus()).thenReturn(Task.Status.NotStarted);
        Mockito.when(task.getTaskId()).thenReturn("taskId");
        Mockito.when(task.getPrivate()).thenReturn(taskPrivate);

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
        Task task1 = Mockito.mock(Task.class);
        TaskPrivate taskPrivate1 = Mockito.mock(TaskPrivate.class);
        TaskManager.TaskManagerListener listener = Mockito.mock(TaskManager.TaskManagerListener.class);

        Mockito.when(task1.getTaskStatus()).thenReturn(Task.Status.NotStarted);
        Mockito.when(task1.getTaskId()).thenReturn("taskId");
        Mockito.when(task1.getPrivate()).thenReturn(taskPrivate1);

        Task task2 = Mockito.mock(Task.class);
        TaskPrivate taskPrivate2 = Mockito.mock(TaskPrivate.class);

        Mockito.when(task2.getTaskStatus()).thenReturn(Task.Status.NotStarted);
        Mockito.when(task2.getTaskId()).thenReturn("taskId");
        Mockito.when(task2.getPrivate()).thenReturn(taskPrivate2);

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

    private Task createTaskMock() {
        return createTaskMock(null, Task.Status.NotStarted);
    }

    private Task createTaskMock(String id, Task.Status status) {
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
