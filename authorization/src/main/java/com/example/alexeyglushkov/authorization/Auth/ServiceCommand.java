package com.example.alexeyglushkov.authorization.Auth;

import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;

/**
 * Created by alexeyglushkov on 31.10.15.
 */

// A Service command represents an url request.
// The command can be started only once. If you want to repeat it you should create new one.

// It could be a great idea to consider to implement also Task interface to be able to use it with
// TaskManager

    //TODO: implement simple subclass
public interface ServiceCommand extends ServiceCommandProxy{
    HttpUrlConnectionBuilder getConnectionBulder();
    String getResponse();

    Error getCommandError();
    boolean isCancelled();
    void setServiceCommandCallback(Callback callback);

    // TODO: need to pass an error or a way to get it
    interface Callback {
        void onCompleted();
    }
}
