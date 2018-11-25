package com.example.alexeyglushkov.taskmanager.task.rx;

import com.example.alexeyglushkov.taskmanager.task.TaskImpl;

import org.junit.Assert;

import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class MaybeTask<T> extends TaskImpl {
    private @NonNull Maybe<T> maybe;

    public MaybeTask(@NonNull Maybe<T> maybe) {
        this.maybe = maybe;
    }

    @Override
    public void startTask(Callback callback) {
        super.startTask(callback);

        final AtomicBoolean finishedFlag = new AtomicBoolean();
        maybe.subscribe(new Consumer<T>() {
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
        }, new Action() {
            @Override
            public void run() throws Exception {
                finishedFlag.set(true);
            }
        });

        Assert.assertTrue("MaybeTask: Maybe must be a sync task", finishedFlag.get());
        getPrivate().handleTaskCompletion(callback);
    }
}
