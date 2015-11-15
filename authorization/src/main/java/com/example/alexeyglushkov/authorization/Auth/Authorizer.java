package com.example.alexeyglushkov.authorization.Auth;

/**
 * Created by alexeyglushkov on 31.10.15.
 */
public interface Authorizer {
    void authorize(AuthorizerCompletion completion);

    void setServiceCommandProvider(ServiceCommandProvider provider);
    void setServiceCommandRunner(ServiceCommandRunner runner);

    interface AuthorizerCompletion {
        void onFinished(AuthCredentials credentials, Error error);
    }
}
