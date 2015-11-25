package com.example.alexeyglushkov.authorization.Auth;

/**
 * Created by alexeyglushkov on 15.11.15.
 */
public interface Account {
    int getId();
    int getServiceType();

    AuthCredentials getCredentials();
    boolean isAuthorized();
    void logout();

    void setAuthorizer(Authorizer authorizer);
    Authorizer getAuthorizer();
    void setAuthCredentialStore(AccountStore store);

    void authorize(final Authorizer.AuthorizerCompletion completion);

    void signCommand(ServiceCommand command);
}
