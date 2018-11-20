package com.example.alexeyglushkov.authorization.Auth;

import com.example.alexeyglushkov.authorization.OAuth.OAuthConstants;

import java.io.Serializable;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;

/**
 * Created by alexeyglushkov on 15.11.15.
 */
public class SimpleAccount implements Account, Serializable {

    private static final long serialVersionUID = -6196365168677255570L;

    private int id = 0;
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
    public void logout() throws Exception {
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

    public Single<AuthCredentials> authorize() {
//        authorizer.authorize(new Authorizer.AuthorizerCompletion() {
//            @Override
//            public void onFinished(AuthCredentials credentials, Authorizer.AuthError error) {
//                if (credentials != null && error == null) {
//                    try {
//                        updateCredentials(credentials);
//
//                    } catch (Exception e) {
//                        error = new Authorizer.AuthError(Authorizer.AuthError.Reason.InnerError, e);
//                    }
//                }
//
//                if (completion != null) {
//                    completion.onFinished(credentials, error);
//                }
//            }
//        });

        return authorizer.authorize().flatMap(new Function<AuthCredentials, SingleSource<? extends AuthCredentials>>() {
            @Override
            public SingleSource<? extends AuthCredentials> apply(AuthCredentials authCredentials) throws Exception {
                try {
                    updateCredentials(authCredentials);
                    return Single.just(authCredentials);

                } catch (Exception e) {
                    Error error = new Authorizer.AuthError(Authorizer.AuthError.Reason.InnerError, e);
                    return Single.error(error);
                }
            }
        });
    }

    private void updateCredentials(AuthCredentials creds) throws Exception {
        credentials = creds;
        store();
    }

    public void store() throws Exception {
        if (id == 0) { // for a new account
            id = accountStore.getMaxAccountId() + 1;
        }

        accountStore.putAccount(this);
    }

    @Override
    public void signCommand(ServiceCommand command) {
        authorizer.signCommand(command, getCredentials());
    }

}
