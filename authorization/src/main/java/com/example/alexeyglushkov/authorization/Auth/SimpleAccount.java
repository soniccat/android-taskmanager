package com.example.alexeyglushkov.authorization.Auth;

import com.example.alexeyglushkov.authorization.OAuth.OAuthConstants;
import com.example.alexeyglushkov.authorization.OAuth.OAuthCredentials;

/**
 * Created by alexeyglushkov on 15.11.15.
 */
public class SimpleAccount implements Account {

    private String id;
    private int serviceType;

    private Authorizer authorizer;
    private AuthCredentials credentials;
    private AuthCredentialStore credentialStore;

    public SimpleAccount(int serviceType) {
        this.serviceType = serviceType;
    }

    @Override
    public String getId() {
        return null;
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
            credentialStore.removeCredentials(getCredentials().getId());
        }
    }

    @Override
    public void setAuthCredentialStore(AuthCredentialStore store) {
        this.credentialStore = store;
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

        id = Integer.toString(credentialStore.getCredentials().size());
        credentials.setId(id);

        credentialStore.putCredentials(credentials);
    }

    @Override
    public void signCommand(ServiceCommand command) {
        //command.getConnectionBulder().addQuerystringParameter(OAuthConstants.ACCESS_TOKEN, getOAuthCredentials().getAccessToken());
    }

}
