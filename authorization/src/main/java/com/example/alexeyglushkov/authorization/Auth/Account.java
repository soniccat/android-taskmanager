package com.example.alexeyglushkov.authorization.Auth;

/**
 * Created by alexeyglushkov on 15.11.15.
 */
public interface Account {
    String getId();
    int getServiceType();

    AuthCredentials getCredentials();
    boolean isAuthorized();
    void logout();

    void setAuthorizer(Authorizer authorizer);
    void setAuthCredentialStore(AuthCredentialStore store);

    void authorize(final Authorizer.AuthorizerCompletion completion);

    void signCommand(ServiceCommand command);
}
