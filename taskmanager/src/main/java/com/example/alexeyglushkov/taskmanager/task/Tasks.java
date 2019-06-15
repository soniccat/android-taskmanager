package com.example.alexeyglushkov.taskmanager.task;

import android.util.Log;

import com.example.alexeyglushkov.taskmanager.task.rx.CompletableTask;
import com.example.alexeyglushkov.taskmanager.task.rx.MaybeTask;
import com.example.alexeyglushkov.taskmanager.task.rx.SingleTask;
import com.example.alexeyglushkov.tools.HandlerTools;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/**
 * Created by alexeyglushkov on 28.12.14.
 */
public class Tasks {

    // To automatically sync task state with your object state (isLoading for example)
    public static void bindOnTaskCompletion(final Task task, final TaskListener listener) {
        task.addTaskStatusListener(new Task.StatusListener() {
            @Override
            public void onTaskStatusChanged(final Task task, final Task.Status oldStatus, final Task.Status newStatus) {
                Log.d("Bind--", "task " + task + " from " + oldStatus + " to " + newStatus);

                if (newStatus == Task.Status.Finished || newStatus == Task.Status.Cancelled)
                    HandlerTools.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.setTaskCompleted(task);
                        }
                });
            }
        });
    }

    public static boolean isTaskReadyToStart(Task task) {
        Task.Status st = task.getTaskStatus();
        return st != Task.Status.Started && st != Task.Status.Finished && st != Task.Status.Cancelled;
    }

    public static boolean isTaskCompleted(Task task) {
        Task.Status st = task.getTaskStatus();
        return st == Task.Status.Finished || st == Task.Status.Cancelled;
    }

    public static <T> Task fromSingle(final Single<T> single) {
        return new SingleTask<>(single);
    }

    public static <T> Single<T> toSingle(final Task task, final TaskPool taskPool) {
        return Single.create(new SingleOnSubscribe<T>() {
            @Override
            public void subscribe(final SingleEmitter<T> emitter) throws Exception {
                final Task.Callback callback = task.getStartCallback();
                task.setTaskCallback(new Task.Callback() {
                    @Override
                    public void onCompleted(boolean cancelled) {
                        if (callback != null) {
                            callback.onCompleted(cancelled);
                        }

                        Error error = task.getTaskError();
                        if (error != null) {
                            emitter.onError(error);
                        } else {
                            try {
                                T result = (T)task.getTaskResult();
                                emitter.onSuccess(result);
                            } catch (Exception e) {
                                emitter.onError(e);
                            }
                        }
                    }
                });

                taskPool.addTask(task);
            }
        });
    }

    public static <T> Task fromMaybe(final Maybe<T> maybe) {
        return new MaybeTask<>(maybe);
    }

    public static Task fromCompletable(final Completable completable) {
        return new CompletableTask(completable);
    };

    public static Completable toCompletable(final Task task, final TaskPool taskPool) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {
                final Task.Callback callback = task.getStartCallback();
                task.setTaskCallback(new Task.Callback() {
                    @Override
                    public void onCompleted(boolean cancelled) {
                        if (callback != null) {
                            callback.onCompleted(cancelled);
                        }

                        Error error = task.getTaskError();
                        if (error != null) {
                            emitter.onError(error);
                        } else {
                            emitter.onComplete();
                        }
                    }
                });

                taskPool.addTask(task);
            }
        });
    }

    // TODO: think about a better name
    // An implementer should store task and filter setTaskCompleted call with old task
    // This can happen due to task cancellation behavior. When a task was cancelled when completion block
    // was already added to the com.example.alexeyglushkov.wordteacher.main thread
    public interface TaskListener {
        void setTaskInProgress(Task task);
        void setTaskCompleted(Task task);
    }
}
