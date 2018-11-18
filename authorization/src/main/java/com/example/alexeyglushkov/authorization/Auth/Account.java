package com.example.alexeyglushkov.authorization.Auth;

import io.reactivex.Single;

/**
 * Created by alexeyglushkov on 15.11.15.
 */
// TODO: think about account manager and account, extract properties in another object
public interface Account {
    int getId();
    int getServiceType();

    AuthCredentials getCredentials();
    boolean isAuthorized();
    void logout() throws Exception;

    void setAuthorizer(Authorizer authorizer);
    Authorizer getAuthorizer();
    void setAuthCredentialStore(AccountStore store);
    void store() throws Exception;

    // Method should be called not on com.example.alexeyglushkov.wordteacher.main thread to be able show auth activity for OAuth20AuthorizerImpl
    Single<AuthCredentials> authorize();

    void signCommand(ServiceCommand command);
}
