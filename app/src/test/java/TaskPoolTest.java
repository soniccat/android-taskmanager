import android.os.Handler;
import android.os.HandlerThread;

import com.ga.task.Task;
import com.ga.task.TaskManager;
import com.ga.task.TaskPool;
import com.ga.task.TaskPrivate;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TaskPoolTest {

    protected TaskPool taskPool;

    public void before(TaskPool taskPool) {
        this.taskPool = taskPool;
    }

    public void setGetHandler() {
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

    public void addTask(){
        // Arrange
        Task task = TestTasks.createTaskMock();
        TaskPrivate taskPrivate = task.getPrivate();
        TaskPool.TaskPoolListener listener = Mockito.mock(TaskPool.TaskPoolListener.class);

        // Act
        taskPool.addListener(listener);
        taskPool.addTask(task);

        // Verify
        Mockito.verify(taskPrivate).setTaskStatus(Task.Status.Waiting);
        Mockito.verify(task).addTaskStatusListener(taskPool);
        Mockito.verify(listener).onTaskAdded(taskPool, task);

        assertEquals(taskPool.getTaskCount(), 1);
        assertTrue(taskPool.getTasks().contains(task));
    }

    public void addStartedTask() {
        // Arrange
        Task task = TestTasks.createTaskMock(null, Task.Status.Started);
        TaskPrivate taskPrivate = task.getPrivate();
        TaskPool.TaskPoolListener listener = Mockito.mock(TaskPool.TaskPoolListener.class);

        // Act
        taskPool.addListener(listener);
        taskPool.addTask(task);

        // Verify
        Mockito.verify(taskPrivate, Mockito.never()).setTaskStatus(Task.Status.Waiting);
        Mockito.verify(task, Mockito.never()).addTaskStatusListener(taskPool);
        Mockito.verify(listener, Mockito.never()).onTaskAdded(taskPool, task);

        assertEquals(taskPool.getTaskCount(), 0);
        assertFalse(taskPool.getTasks().contains(task));
    }

    public void removeTask() {
        // Arrange
        Task task = TestTasks.createTaskMock("taskId", Task.Status.NotStarted);
        TaskPool.TaskPoolListener listener = Mockito.mock(TaskPool.TaskPoolListener.class);

        // Act
        taskPool.addListener(listener);
        taskPool.addTask(task);
        taskPool.removeTask(task);

        // Verify
        Mockito.verify(listener).onTaskAdded(taskPool, task);
        Mockito.verify(listener).onTaskRemoved(taskPool, task);

        assertEquals(0, taskPool.getTaskCount());
    }

    public void removeUnknownTask() {
        // Arrange
        Task task1 = TestTasks.createTaskMock("taskId", Task.Status.NotStarted);
        Task task2 = TestTasks.createTaskMock("taskId", Task.Status.NotStarted);
        TaskPool.TaskPoolListener listener = Mockito.mock(TaskPool.TaskPoolListener.class);

        // Act
        taskPool.addListener(listener);
        taskPool.addTask(task1);
        taskPool.removeTask(task2);

        // Verify
        Mockito.verify(listener).onTaskAdded(taskPool, task1);
        Mockito.verify(listener, Mockito.never()).onTaskRemoved(taskPool, task1);
        Mockito.verify(listener, Mockito.never()).onTaskAdded(taskPool, task2);
        Mockito.verify(listener, Mockito.never()).onTaskRemoved(taskPool, task2);

        assertEquals(1, taskPool.getTaskCount());
    }

    public void getTask() {
        // Arrange
        Task task = TestTasks.createTaskMock("taskId", Task.Status.NotStarted);

        // Act
        taskPool.addTask(task);
        Task returnedTask = taskPool.getTask("taskId");

        // Verify
        assertEquals(task, returnedTask);
    }

    public void getUnknownTask() {
        // Arrange
        Task task = TestTasks.createTaskMock("taskId", Task.Status.NotStarted);

        // Act
        taskPool.addTask(task);
        Task returnedTask = taskPool.getTask("taskId2");

        // Verify
        assertEquals(null, returnedTask);

    }

    public void getTaskCount() {
        // Arrange
        Task task1 = TestTasks.createTaskMock("taskId1", Task.Status.NotStarted);
        Task task2 = TestTasks.createTaskMock("taskId2", Task.Status.NotStarted);

        // Act
        taskPool.addTask(task1);
        taskPool.addTask(task2);

        // Verify
        assertEquals(2, taskPool.getTaskCount());
    }

    public void getTaskCount2() {
        // Arrange
        Task task1 = TestTasks.createTaskMock("taskId1", Task.Status.NotStarted, 0, 30);
        Task task2 = TestTasks.createTaskMock("taskId2", Task.Status.NotStarted, 0, 10);
        Task task3 = TestTasks.createTaskMock("taskId3", Task.Status.NotStarted, 0, 15);

        // Act
        taskPool.addTask(task1);
        taskPool.addTask(task2);
        taskPool.addTask(task3);

        // Verify
        assertEquals(3, taskPool.getTaskCount());
    }

    public void setGetUserData() {
        // Arrange
        String data = "data";

        // Act
        taskPool.setUserData(data);

        // Verify
        assertEquals(data, taskPool.getUserData());
    }

    public void addStateListener() {
        // Arrange
        Task task = TestTasks.createTaskMock("taskId", Task.Status.NotStarted);
        TaskPool.TaskPoolListener listener1 = Mockito.mock(TaskPool.TaskPoolListener.class);
        TaskPool.TaskPoolListener listener2 = Mockito.mock(TaskPool.TaskPoolListener.class);

        // Act
        taskPool.addListener(listener1);
        taskPool.addListener(listener2);
        taskPool.addTask(task);

        // Verify
        Mockito.verify(listener1).onTaskAdded(taskPool,task);
        Mockito.verify(listener2).onTaskAdded(taskPool, task);
    }

    public void removeStateListener() {
        // Arrange
        Task task = TestTasks.createTaskMock("taskId", Task.Status.NotStarted);
        TaskPool.TaskPoolListener listener1 = Mockito.mock(TaskPool.TaskPoolListener.class);
        TaskPool.TaskPoolListener listener2 = Mockito.mock(TaskPool.TaskPoolListener.class);

        // Act
        taskPool.addListener(listener1);
        taskPool.addListener(listener2);
        taskPool.removeListener(listener1);
        taskPool.removeListener(listener2);
        taskPool.addTask(task);

        // Verify
        Mockito.verify(listener1, Mockito.never()).onTaskAdded(taskPool,task);
        Mockito.verify(listener2, Mockito.never()).onTaskAdded(taskPool,task);
    }

    public void changeTaskStatus() {
        // Arrange
        TestTask testTask = new TestTask();
        TaskPool taskPoolMock = Mockito.spy(taskPool);

        // Act
        taskPoolMock.addTask(testTask);
        testTask.getPrivate().setTaskStatus(Task.Status.Started);

        // Verify
        Mockito.verify(taskPoolMock).onTaskStatusChanged(testTask, Task.Status.Waiting, Task.Status.Started);

        assertEquals(1, taskPool.getTaskCount());
    }

    public void checkTaskRemovingAfterFinishing() {
        // Arrange
        TestTask testTask = new TestTask();
        TaskPool taskPoolMock = Mockito.spy(taskPool);

        // Act
        taskPoolMock.addTask(testTask);
        testTask.getPrivate().setTaskStatus(Task.Status.Finished);

        // Verify
        Mockito.verify(taskPoolMock).onTaskStatusChanged(testTask, Task.Status.Waiting, Task.Status.Finished);

        assertEquals(0, taskPool.getTaskCount());
    }
}