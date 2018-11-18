package com.example.alexeyglushkov.authorization.Auth;

import io.reactivex.Single;

/**
 * Created by alexeyglushkov on 01.11.15.
 */
public interface ServiceCommandRunner {
    Single<ServiceCommand> run(ServiceCommand command);
    void cancel(ServiceCommand command);
}
