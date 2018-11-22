package com.example.alexeyglushkov.authorization.Auth;

import io.reactivex.Single;

/**
 * Created by alexeyglushkov on 01.11.15.
 */
public interface ServiceCommandRunner {
    <T extends ServiceCommand<U>, U> Single<T> run(T command);
    <T extends ServiceCommand<U>, U> void run(T command, Callback callback);
    <T extends ServiceCommand<U>, U> void cancel(T command);

    interface Callback {
        void onCompleted(Error error, boolean isCancelled);
    }
}
