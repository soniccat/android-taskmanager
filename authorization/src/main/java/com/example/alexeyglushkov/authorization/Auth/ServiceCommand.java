package com.example.alexeyglushkov.authorization.Auth;

import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;

import androidx.annotation.Nullable;

/**
 * Created by alexeyglushkov on 31.10.15.
 */

// A Service command represents an url request.
// If you want to repeat a command you should call clear when it finishes and starting it again.

public interface ServiceCommand<T> {
    // TODO: it seems these 3 methods could be not necessary (see dropbox upload command)
    HttpUrlConnectionBuilder getConnectionBuilder();
    @Nullable T getResponse();
    int getResponseCode();

    @Nullable Error getCommandError();
    boolean isCancelled();

    void clear();
}
