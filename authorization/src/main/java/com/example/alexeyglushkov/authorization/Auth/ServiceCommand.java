package com.example.alexeyglushkov.authorization.Auth;

import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;

import androidx.annotation.Nullable;

/**
 * Created by alexeyglushkov on 31.10.15.
 */

// A Service command represents an url request.
// The command can be started only once. If you want to repeat it you should create new one or call clear before.

// It could be a great idea to consider to implement also Task interface to be able to use it with
// TaskManager

    //TODO: implement simple subclass
public interface ServiceCommand<T> {

    // TODO: it seems these 3 methods could be not necessary (see dropbox upload command)
    HttpUrlConnectionBuilder getConnectionBuilder();
    @Nullable T getResponse();
    int getResponseCode();

    Error getCommandError();
    boolean isCancelled();

    void clear();
}
