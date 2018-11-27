package com.example.alexeyglushkov.authtaskmanager;

import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.taskmanager.task.StackTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;
import com.example.alexeyglushkov.taskmanager.task.TaskProvider;
import com.example.alexeyglushkov.taskmanager.task.Tasks;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposables;

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
    public <T extends ServiceCommand<?>> Single<T> run(final T command) {
        return Single.create(new SingleOnSubscribe<T>() {
            @Override
            public void subscribe(final SingleEmitter<T> emitter) throws Exception {
                final ServiceTask serviceTask = (ServiceTask)command;
                emitter.setDisposable(Disposables.fromRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if (!Tasks.isTaskCompleted(serviceTask.getTask())) {
                            cancel(serviceTask);
                        }
                    }
                }));

                run(serviceTask, new Callback() {
                    @Override
                    public void onCompleted(Error error, boolean cancelled) {
                        if (!emitter.isDisposed() && !cancelled) {
                            if (error != null) {
                                emitter.onError(error);
                            } else {
                                emitter.onSuccess(command);
                            }
                        }
                    }
                });
            }
        });
    }

    public <T extends ServiceCommand<?>> void run(final T command, final Callback callback) {
        final ServiceTask serviceTask = (ServiceTask)command;
        Task task = serviceTask.getTask();
        task.setTaskCallback(new Task.Callback() {
            @Override
            public void onCompleted(boolean cancelled) {
                callback.onCompleted(serviceTask.getCommandError(), cancelled);
            }
        });
        taskProvider.addTask(task);

    }

    @Override
    public <T extends ServiceCommand<?>> void cancel(T command) {
        ServiceTask serviceTask = (ServiceTask)command;
        taskManager.cancel(serviceTask.getTask(), null);
    }
}
