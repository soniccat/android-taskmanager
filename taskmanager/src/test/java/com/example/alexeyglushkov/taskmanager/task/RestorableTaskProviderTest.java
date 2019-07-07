package com.example.alexeyglushkov.taskmanager.task;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Created by alexeyglushkov on 19.08.17.
 */

public class RestorableTaskProviderTest {
    @Before
    public void setUp() throws Exception {
    }

    @NonNull
    protected TaskProvider prepareTaskProvider(TaskProvider taskProvider) {
        return new RestorableTaskProvider(taskProvider);
    }

    @Test
    public void testAddTaskWhenProviderDoesNothing() {
        // When
        TaskProvider taskProvider = Mockito.mock(TaskProvider.class);
        RestorableTaskProvider restorableTaskProvider = new RestorableTaskProvider(taskProvider);
        Task task = Mockito.spy(new TestTask());

        // Then
        restorableTaskProvider.addTask(task);

        // Assert
        Mockito.verify(task, Mockito.never()).addTaskStatusListener(restorableTaskProvider);
    }

    @Test
    public void testAddTaskWhenProviderStartsTask() {
        // When
        TaskProvider taskProvider = Mockito.mock(TaskProvider.class);
        Mockito.doReturn(new Handler(Looper.myLooper())).when(taskProvider).getHandler();
        RestorableTaskProvider restorableTaskProvider = new RestorableTaskProvider(taskProvider);
        final Task task = Mockito.spy(new TestTask());

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                task.getPrivate().setTaskStatus(Task.Status.Waiting);
                return null;
            }
        }).when(taskProvider).addTask(task);

        // Then
        restorableTaskProvider.addTask(task);

        // Assert
        Mockito.verify(task).addTaskStatusListener(restorableTaskProvider);
    }

    @Test
    public void testTakeTopTask() {
        // When
        Task task = Mockito.mock(Task.class);

        TaskProvider taskProvider = Mockito.mock(TaskProvider.class);
        Mockito.doReturn(new Handler(Looper.myLooper())).when(taskProvider).getHandler();
        Mockito.doReturn(task).when(taskProvider).takeTopTask(null);

        RestorableTaskProvider restorableTaskProvider = new RestorableTaskProvider(taskProvider);

        restorableTaskProvider.addTask(task);

        // Then
        Task takenTask = restorableTaskProvider.takeTopTask(null);

        // Assert
        Assert.assertEquals(task, takenTask);
        Assert.assertEquals(1, restorableTaskProvider.getActiveTasks().size());
        Assert.assertEquals(takenTask, restorableTaskProvider.getActiveTasks().get(0));
    }

    @Test
    public void testOnTaskStatusChangedWhenRecording() {
        // When
        TaskProvider taskProvider = Mockito.mock(TaskProvider.class);
        Mockito.doReturn(new Handler(Looper.myLooper())).when(taskProvider).getHandler();

        RestorableTaskProvider restorableTaskProvider = new RestorableTaskProvider(taskProvider);
        restorableTaskProvider.setRecording(true);

        Task task = Mockito.mock(Task.class);
        Mockito.doReturn(Task.Status.Finished).when(task).getTaskStatus();

        restorableTaskProvider.getActiveTasks().add(task);

        // Then
        restorableTaskProvider.onTaskStatusChanged(task, Task.Status.Started, Task.Status.Finished);

        // Assert
        Assert.assertEquals(0, restorableTaskProvider.getActiveTasks().size());
        Assert.assertEquals(1, restorableTaskProvider.getCompletedTasks().size());
        Assert.assertEquals(task, restorableTaskProvider.getCompletedTasks().get(0));
    }

    @Test
    public void testOnTaskStatusChangedWhenNotRecording() {
        // When
        TaskProvider taskProvider = Mockito.mock(TaskProvider.class);
        Mockito.doReturn(new Handler(Looper.myLooper())).when(taskProvider).getHandler();

        RestorableTaskProvider restorableTaskProvider = new RestorableTaskProvider(taskProvider);
        restorableTaskProvider.setRecording(false);

        Task task = Mockito.mock(Task.class);
        Mockito.doReturn(Task.Status.Finished).when(task).getTaskStatus();

        restorableTaskProvider.getActiveTasks().add(task);

        // Then
        restorableTaskProvider.onTaskStatusChanged(task, Task.Status.Started, Task.Status.Finished);

        // Assert
        Assert.assertEquals(0, restorableTaskProvider.getActiveTasks().size());
        Assert.assertEquals(0, restorableTaskProvider.getCompletedTasks().size());
    }

    @Test
    public void testRestoreTaskCompletionWithActiveTasks() {
        // When
        TaskProvider taskProvider = Mockito.mock(TaskProvider.class);
        Mockito.doReturn(new Handler(Looper.myLooper())).when(taskProvider).getHandler();

        RestorableTaskProvider restorableTaskProvider = new RestorableTaskProvider(taskProvider);

        Task task = Mockito.spy(new TestTask());
        Mockito.doReturn("TestId").when(task).getTaskId();
        Mockito.doReturn(Task.Status.Started).when(task).getTaskStatus();

        restorableTaskProvider.getActiveTasks().add(task);

        Task.Callback callback = Mockito.mock(Task.Callback.class);

        // Then
        restorableTaskProvider.restoreTaskCompletion("TestId", callback);

        // Assert
        Assert.assertEquals(callback, task.getPrivate().getTaskCallback());
        Mockito.verify(callback, Mockito.never()).onCompleted(false);
    }

    @Test
    public void testRestoreTaskCompletionWithCompletedTasks() {
        // When
        TaskProvider taskProvider = Mockito.mock(TaskProvider.class);
        Mockito.doReturn(new Handler(Looper.myLooper())).when(taskProvider).getHandler();

        RestorableTaskProvider restorableTaskProvider = new RestorableTaskProvider(taskProvider);

        Task task = Mockito.spy(new TestTask());
        Mockito.doReturn("TestId").when(task).getTaskId();
        Mockito.doReturn(Task.Status.Finished).when(task).getTaskStatus();

        restorableTaskProvider.getCompletedTasks().add(task);

        Task.Callback callback = Mockito.mock(Task.Callback.class);

        // Then
        restorableTaskProvider.restoreTaskCompletion("TestId", callback);

        // Assert
        Assert.assertNull(task.getPrivate().getTaskCallback());
        Mockito.verify(callback).onCompleted(false);
    }
}
