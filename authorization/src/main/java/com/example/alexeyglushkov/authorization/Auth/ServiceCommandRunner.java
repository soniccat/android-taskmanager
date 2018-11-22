package com.example.alexeyglushkov.authorization.Auth;

import io.reactivex.Single;

/**
 * Created by alexeyglushkov on 01.11.15.
 */
public interface ServiceCommandRunner {
    <T extends ServiceCommand<?>> Single<T> run(T command);
    <T extends ServiceCommand<?>> void run(T command, Callback callback);
    <T extends ServiceCommand<?>> void cancel(T command);

    interface Callback {
        void onCompleted(Error error, boolean isCancelled);
    }
}
