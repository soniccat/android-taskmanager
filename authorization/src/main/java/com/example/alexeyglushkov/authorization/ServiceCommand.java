package com.example.alexeyglushkov.authorization;

import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;

/**
 * Created by alexeyglushkov on 31.10.15.
 */

// A Service command represents an url request.
// The command can be started only once. If you want to repeat it you should create new one.

// It could be a great idea to consider to implement also Task interface to be able to use it with
// TaskManager
public interface ServiceCommand {
    HttpUrlConnectionBuilder getConnectionBulder();

    Error getCommandError();
    void setServiceCommandCallback(Callback callback);

    interface Callback {
        void onCompleted(boolean isCancelled);
    }
}
