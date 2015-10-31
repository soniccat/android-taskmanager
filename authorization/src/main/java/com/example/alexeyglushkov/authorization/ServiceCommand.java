package com.example.alexeyglushkov.authorization;

import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;

/**
 * Created by alexeyglushkov on 31.10.15.
 */
public interface ServiceCommand {
    HttpUrlConnectionBuilder getConnectionBulder();
}
