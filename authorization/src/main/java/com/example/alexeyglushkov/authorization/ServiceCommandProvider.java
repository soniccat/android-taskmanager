package com.example.alexeyglushkov.authorization;

import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;

/**
 * Created by alexeyglushkov on 01.11.15.
 */
public interface ServiceCommandProvider {
    ServiceCommand getServiceCommand(HttpUrlConnectionBuilder builder);
}
