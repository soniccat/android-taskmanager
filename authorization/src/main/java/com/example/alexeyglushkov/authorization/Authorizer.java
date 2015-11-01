package com.example.alexeyglushkov.authorization;

/**
 * Created by alexeyglushkov on 31.10.15.
 */
public interface Authorizer {
    void authorize(AuthorizerCompletion completion);
    AuthCredentials getCredentials();
    boolean isAuthorized();
    void logout();

    void setServiceCommandRunner(ServiceCommandRunner runner);
    void setAuthCredentialStore(AuthCredentialStore store);

    interface AuthorizerCompletion {
        void onAuthorized(Error error);
    }
}
