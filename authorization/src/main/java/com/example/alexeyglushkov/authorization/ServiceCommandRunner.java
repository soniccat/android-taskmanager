package com.example.alexeyglushkov.authorization;

import com.example.alexeyglushkov.authorization.ServiceCommand;

import java.net.URLConnection;

/**
 * Created by alexeyglushkov on 01.11.15.
 */
public interface ServiceCommandRunner {
    void run(ServiceCommand command);
    void cancel(ServiceCommand command);
}
