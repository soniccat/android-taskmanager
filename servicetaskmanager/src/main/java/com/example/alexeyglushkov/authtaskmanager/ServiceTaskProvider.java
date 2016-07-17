package com.example.alexeyglushkov.authtaskmanager;

import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider;
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;

/**
 * Created by alexeyglushkov on 04.11.15.
 */
public class ServiceTaskProvider implements ServiceCommandProvider {
    @Override
    public ServiceCommand getServiceCommand(HttpUrlConnectionBuilder builder) {
        HttpServiceTask httpServiceTask = new HttpServiceTask();
        httpServiceTask.setConnectionBuilder(builder);
        return httpServiceTask;
    }
}
