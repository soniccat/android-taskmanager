package com.example.alexeyglushkov.authorization.Auth;

/**
 * Created by alexeyglushkov on 15.11.15.
 */
public class SimpleAccount implements Account {

    private int id;
    private int serviceType;

    private Authorizer authorizer;
    private AuthCredentials credentials;
    private AccountStore accountStore;

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
        return credentials.isValid();
    }

    @Override
    public void logout() {
        if (getCredentials() != null) {
            accountStore.removeAccount(Integer.toString(getId()));
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

    public void authorize(final Authorizer.AuthorizerCompletion completion) {
        authorizer.authorize(new Authorizer.AuthorizerCompletion() {
            @Override
            public void onFinished(AuthCredentials credentials, Error error) {
                if (credentials != null) {
                    updateCredentials(credentials);
                }

                completion.onFinished(credentials, error);
            }
        });
    }

    private void updateCredentials(AuthCredentials creds) {
        credentials = creds;

        id = accountStore.getMaxAccountId() + 1;
        //credentials.setId(id);

        accountStore.putAccount(this);
    }

    @Override
    public void signCommand(ServiceCommand command) {
        //command.getConnectionBulder().addQuerystringParameter(OAuthConstants.ACCESS_TOKEN, getOAuthCredentials().getAccessToken());
    }

}
