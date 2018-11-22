package com.example.alexeyglushkov.authtaskmanager;

import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider;
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler;

/**
 * Created by alexeyglushkov on 04.11.15.
 */
public class ServiceTaskProvider implements ServiceCommandProvider {
    @Override
    public <T> ServiceCommand<T> getServiceCommand(HttpUrlConnectionBuilder builder, ByteArrayHandler<T> handler) {
        HttpServiceCommand<T> httpServiceCommand = new HttpServiceCommand<>(handler);
        httpServiceCommand.setConnectionBuilder(builder);
        return httpServiceCommand;
    }
}
