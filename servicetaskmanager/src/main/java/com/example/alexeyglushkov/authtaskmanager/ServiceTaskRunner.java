package com.example.alexeyglushkov.authtaskmanager;

import android.os.Handler;

import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.taskmanager.task.PriorityTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.StackTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;
import com.example.alexeyglushkov.taskmanager.task.TaskPool;
import com.example.alexeyglushkov.taskmanager.task.TaskProvider;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.functions.Action;

/**
 * Created by alexeyglushkov on 04.11.15.
 */
public class ServiceTaskRunner implements ServiceCommandRunner {
    private TaskManager taskManager;
    private TaskProvider taskProvider;

    public ServiceTaskRunner(TaskManager taskManager, String id) {
        this.taskManager = taskManager;
        this.taskProvider = new StackTaskProvider(true, this.taskManager.getHandler(), id);
        this.taskManager.addTaskProvider(this.taskProvider);
    }

    @Override
    public <T extends ServiceCommand> Single<T> run(final T command) {
        final IServiceTask serviceTask = (IServiceTask)command;

        return Single.create(new SingleOnSubscribe<T>() {
            @Override
            public void subscribe(final SingleEmitter<T> emitter) throws Exception {
                serviceTask.setTaskCallback(new Task.Callback() {
                    @Override
                    public void onCompleted(boolean cancelled) {
                        Error error = command.getCommandError();
                        if (error != null) {
                            emitter.onError(error);
                        } else {
                            emitter.onSuccess(command);
                        }
                    }
                });
                taskProvider.addTask(serviceTask);
            }
        }).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                cancel(serviceTask);
            }
        });
    }

    @Override
    public <T extends ServiceCommand> void cancel(T command) {
        IServiceTask serviceTask = (IServiceTask)command;
        taskManager.cancel(serviceTask, null);
    }
}
