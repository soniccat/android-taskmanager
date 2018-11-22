package com.example.alexeyglushkov.authorization.Auth;

import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;

/**
 * Created by alexeyglushkov on 01.11.15.
 */
// TODO: it seems we can remove that class
public interface ServiceCommandProvider {
    ServiceCommand getServiceCommand(HttpUrlConnectionBuilder builder);
}
