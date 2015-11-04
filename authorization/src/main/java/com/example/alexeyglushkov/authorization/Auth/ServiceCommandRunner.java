package com.example.alexeyglushkov.authorization.Auth;

/**
 * Created by alexeyglushkov on 01.11.15.
 */
public interface ServiceCommandRunner {
    void run(ServiceCommand command);
    void cancel(ServiceCommand command);
}
