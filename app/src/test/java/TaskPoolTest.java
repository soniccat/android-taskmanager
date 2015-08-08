import android.os.Handler;
import android.os.HandlerThread;

import com.ga.task.Task;
import com.ga.task.TaskPool;
import com.ga.task.TaskPrivate;

import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public abstract class TaskPoolTest {

    protected TaskPool taskPool;

    public void before(TaskPool taskPool) {
        this.taskPool = taskPool;
    }

    public void testSetGetHandler() {
        // Arrange
        HandlerThread handlerThread = new HandlerThread("HandlerThread");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());

        // Act
        taskPool.setHandler(handler);

        // Verify
        assertTrue(taskPool.getHandler() != null);
        assertEquals(handler, taskPool.getHandler());
    }

    public void testAddTask(){
        // Arrange
        Task task = Mockito.mock(Task.class);
        TaskPrivate taskPrivate = Mockito.mock(TaskPrivate.class);
        TaskPool.TaskPoolListener listener = Mockito.mock(TaskPool.TaskPoolListener.class);

        Mockito.when(task.getPrivate()).thenReturn(taskPrivate);

        // Act
        taskPool.addListener(listener);
        taskPool.addTask(task);

        // Verify
        Mockito.verify(task, Mockito.atLeastOnce()).getTaskId();
        Mockito.verify(taskPrivate).setTaskStatus(Task.Status.Waiting);
        Mockito.verify(task).addTaskStatusListener(taskPool);
        Mockito.verify(listener).onTaskAdded(taskPool, task);

        assertEquals(taskPool.getTaskCount(), 1);
        assertTrue(taskPool.getTasks().contains(task));
    }

    public void addTheSameTaskWithSkipPolicy() {
        // Arrange
        Task task1 = Mockito.mock(Task.class);
        TaskPrivate taskPrivate1 = Mockito.mock(TaskPrivate.class);
        TaskPool.TaskPoolListener listener = Mockito.mock(TaskPool.TaskPoolListener.class);

        Mockito.when(task1.getTaskId()).thenReturn("taskId");
        Mockito.when(task1.getPrivate()).thenReturn(taskPrivate1);

        Task task2 = Mockito.mock(Task.class);
        TaskPrivate taskPrivate2 = Mockito.mock(TaskPrivate.class);

        Mockito.when(task2.getTaskId()).thenReturn("taskId");
        Mockito.when(task2.getPrivate()).thenReturn(taskPrivate2);
        Mockito.when(task2.getLoadPolicy()).thenReturn(Task.LoadPolicy.SkipIfAdded);
        Mockito.when(task2.getTaskStatus()).thenReturn(Task.Status.NotStarted);

        // Act
        taskPool.addListener(listener);
        taskPool.addTask(task1);
        taskPool.addTask(task2);

        // Verify
        Mockito.verify(task1, Mockito.atLeastOnce()).getTaskId();
        Mockito.verify(taskPrivate1).setTaskStatus(Task.Status.Waiting);
        Mockito.verify(task1).addTaskStatusListener(taskPool);
        Mockito.verify(listener).onTaskAdded(taskPool, task1);

        Mockito.verify(task2, Mockito.atLeastOnce()).getTaskId();
        Mockito.verify(taskPrivate2).setTaskStatus(Task.Status.Waiting);
        Mockito.verify(task2, Mockito.never()).addTaskStatusListener(taskPool);
        Mockito.verify(listener, Mockito.never()).onTaskAdded(taskPool, task2);

        assertEquals(taskPool.getTaskCount(), 1);
        assertTrue(taskPool.getTasks().contains(task1));
    }

    public void addTheSameTaskWithCancelPolicy() {
        // Arrange
        Task task1 = Mockito.mock(Task.class);
        TaskPrivate taskPrivate1 = Mockito.mock(TaskPrivate.class);
        TaskPool.TaskPoolListener listener = Mockito.mock(TaskPool.TaskPoolListener.class);

        Mockito.when(task1.getTaskId()).thenReturn("taskId");
        Mockito.when(task1.getPrivate()).thenReturn(taskPrivate1);

        Task task2 = Mockito.mock(Task.class);
        TaskPrivate taskPrivate2 = Mockito.mock(TaskPrivate.class);

        Mockito.when(task2.getTaskId()).thenReturn("taskId");
        Mockito.when(task2.getPrivate()).thenReturn(taskPrivate2);
        Mockito.when(task2.getLoadPolicy()).thenReturn(Task.LoadPolicy.CancelAdded);

        // Act
        taskPool.addListener(listener);
        taskPool.addTask(task1);
        taskPool.addTask(task2);

        // Verify
        Mockito.verify(task1, Mockito.atLeastOnce()).getTaskId();
        Mockito.verify(taskPrivate1).setTaskStatus(Task.Status.Waiting);
        Mockito.verify(task1).addTaskStatusListener(taskPool);
        Mockito.verify(listener).onTaskAdded(taskPool, task1);
        Mockito.verify(listener).onTaskRemoved(taskPool, task1);

        Mockito.verify(task2, Mockito.atLeastOnce()).getTaskId();
        Mockito.verify(taskPrivate2).setTaskStatus(Task.Status.Waiting);
        Mockito.verify(task2).addTaskStatusListener(taskPool);
        Mockito.verify(listener).onTaskAdded(taskPool, task2);

        assertEquals(taskPool.getTaskCount(), 1);
        assertTrue(taskPool.getTasks().contains(task2));
    }

    public void testGetTask() {
        // Arrange
        Task task = Mockito.mock(Task.class);
        TaskPrivate taskPrivate = Mockito.mock(TaskPrivate.class);

        Mockito.when(task.getTaskId()).thenReturn("taskId");
        Mockito.when(task.getPrivate()).thenReturn(taskPrivate);

        // Act
        taskPool.addTask(task);
        Task returnedTask = taskPool.getTask("taskId");

        // Verify
        assertEquals(task, returnedTask);
    }

    public void testGetTaskCount() {
        // Arrange
        Task task1 = Mockito.mock(Task.class);
        TaskPrivate taskPrivate1 = Mockito.mock(TaskPrivate.class);

        Mockito.when(task1.getTaskId()).thenReturn("taskId");
        Mockito.when(task1.getPrivate()).thenReturn(taskPrivate1);

        Task task2 = Mockito.mock(Task.class);
        TaskPrivate taskPrivate2 = Mockito.mock(TaskPrivate.class);

        Mockito.when(task2.getTaskId()).thenReturn("taskId2");
        Mockito.when(task2.getPrivate()).thenReturn(taskPrivate2);

        // Act
        taskPool.addTask(task1);
        taskPool.addTask(task2);

        // Verify
        assertEquals(2, taskPool.getTaskCount());
    }

    public void testSetGetUserData() {
        // Arrange
        String data = "data";

        // Act
        taskPool.setUserData(data);

        // Verify
        assertEquals(data, taskPool.getUserData());
    }

    public void testAddStateListener() {
        // Arrange
        Task task1 = Mockito.mock(Task.class);
        TaskPrivate taskPrivate1 = Mockito.mock(TaskPrivate.class);

        Mockito.when(task1.getTaskId()).thenReturn("taskId");
        Mockito.when(task1.getPrivate()).thenReturn(taskPrivate1);

        TaskPool.TaskPoolListener listener1 = Mockito.mock(TaskPool.TaskPoolListener.class);
        TaskPool.TaskPoolListener listener2 = Mockito.mock(TaskPool.TaskPoolListener.class);

        // Act
        taskPool.addListener(listener1);
        taskPool.addListener(listener2);
        taskPool.addTask(task1);

        // Verify
        Mockito.verify(listener1).onTaskAdded(taskPool,task1);
        Mockito.verify(listener2).onTaskAdded(taskPool, task1);
    }

    public void testRemoveStateListener() {
        // Arrange
        Task task1 = Mockito.mock(Task.class);
        TaskPrivate taskPrivate1 = Mockito.mock(TaskPrivate.class);

        Mockito.when(task1.getTaskId()).thenReturn("taskId");
        Mockito.when(task1.getPrivate()).thenReturn(taskPrivate1);

        TaskPool.TaskPoolListener listener1 = Mockito.mock(TaskPool.TaskPoolListener.class);
        TaskPool.TaskPoolListener listener2 = Mockito.mock(TaskPool.TaskPoolListener.class);

        // Act
        taskPool.addListener(listener1);
        taskPool.addListener(listener2);
        taskPool.removeListener(listener1);
        taskPool.removeListener(listener2);
        taskPool.addTask(task1);

        // Verify
        Mockito.verify(listener1, Mockito.never()).onTaskAdded(taskPool,task1);
        Mockito.verify(listener2, Mockito.never()).onTaskAdded(taskPool,task1);
    }
}