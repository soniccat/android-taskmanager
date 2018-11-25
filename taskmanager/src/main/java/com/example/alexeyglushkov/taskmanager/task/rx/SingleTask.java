package com.example.alexeyglushkov.taskmanager.task.rx;

import com.example.alexeyglushkov.taskmanager.task.TaskImpl;

import org.junit.Assert;

import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;

public class SingleTask<T> extends TaskImpl {
    private @NonNull
    Single<T> single;

    public SingleTask(@NonNull Single<T> single) {
        this.single = single;
    }

    @Override
    public void startTask(Callback callback) {
        super.startTask(callback);

        final AtomicBoolean finishedFlag = new AtomicBoolean();
        single.subscribe(new Consumer<T>() {
            @Override
            public void accept(T t) {
                getPrivate().setTaskResult(t);
                finishedFlag.set(true);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                getPrivate().setTaskError(new Error(throwable));
                finishedFlag.set(true);
            }
        });

        Assert.assertTrue("SingleTask: Single must be a sync task", finishedFlag.get());
        getPrivate().handleTaskCompletion(callback);
    }
}
