package com.example.alexeyglushkov.taskmanager.task.rx;

import com.example.alexeyglushkov.taskmanager.task.TaskImpl;

import org.junit.Assert;

import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class CompletableTask extends TaskImpl {
    private @NonNull Completable completable;

    public CompletableTask(@NonNull Completable completable) {
        this.completable = completable;
    }

    @Override
    public void startTask(Callback callback) {
        super.startTask(callback);

        final AtomicBoolean finishedFlag = new AtomicBoolean();
        completable.subscribe(new Action() {
            @Override
            public void run() throws Exception {
                finishedFlag.set(true);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                getPrivate().setTaskError(new Error(throwable));
                finishedFlag.set(true);
            }
        });

        Assert.assertTrue("Completable: Completable must be a sync task", finishedFlag.get());
        getPrivate().handleTaskCompletion(callback);
    }
}
