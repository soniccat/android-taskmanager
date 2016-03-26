package com.example.alexeyglushkov.authorization.Auth;

import com.example.alexeyglushkov.authorization.OAuth.OAuthConstants;

import java.io.Serializable;

/**
 * Created by alexeyglushkov on 15.11.15.
 */
public class SimpleAccount implements Account, Serializable {

    private static final long serialVersionUID = -6196365168677255570L;

    private int id;
    private int serviceType;

    private AuthCredentials credentials;
    transient private Authorizer authorizer;
    transient private AccountStore accountStore;

    public SimpleAccount(int serviceType) {
        this.serviceType = serviceType;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getServiceType() {
        return serviceType;
    }

    @Override
    public AuthCredentials getCredentials() {
        return credentials;
    }

    @Override
    public boolean isAuthorized() {
        // isValid should be called manually
        return credentials != null;
    }

    @Override
    public void logout() {
        if (getCredentials() != null) {
            accountStore.removeAccount(getId());
        }
    }

    @Override
    public void setAuthCredentialStore(AccountStore store) {
        this.accountStore = store;
    }

    @Override
    public void setAuthorizer(Authorizer authorizer) {
        this.authorizer = authorizer;
    }

    public Authorizer getAuthorizer() {
        return authorizer;
    }

    public void authorize(final Authorizer.AuthorizerCompletion completion) {
        authorizer.authorize(new Authorizer.AuthorizerCompletion() {
            @Override
            public void onFinished(AuthCredentials credentials, Error error) {
                if (credentials != null) {
                    updateCredentials(credentials);
                }

                if (completion != null) {
                    completion.onFinished(credentials, error);
                }
            }
        });
    }

    private void updateCredentials(AuthCredentials creds) {
        credentials = creds;
        id = accountStore.getMaxAccountId() + 1;

        accountStore.putAccount(this);
    }

    @Override
    public void signCommand(ServiceCommand command) {
        authorizer.signCommand(command, getCredentials());
    }

}
