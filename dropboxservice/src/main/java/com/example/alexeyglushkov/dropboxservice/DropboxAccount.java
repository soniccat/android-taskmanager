package com.example.alexeyglushkov.dropboxservice;

import com.dropbox.client2.android.AndroidAuthSession;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.SimpleAccount;
import com.example.alexeyglushkov.authorization.OAuth.OAuthCredentials;

/**
 * Created by alexeyglushkov on 10.07.16.
 */
public class DropboxAccount extends SimpleAccount {
    private static final long serialVersionUID = 2583651658110836477L;

    public DropboxAccount(int serviceType) {
        super(serviceType);
    }

    // need to be called outside to finish authorization
    public void onResume() {
        DropboxAuthorizer authorizer = (DropboxAuthorizer)getAuthorizer();
        authorizer.onResume();
    }

    @Override
    public void setAuthorizer(Authorizer authorizer) {
        super.setAuthorizer(authorizer);

        OAuthCredentials credentials = (OAuthCredentials)getCredentials();
        if (credentials != null) {
            getSession().setOAuth2AccessToken(credentials.getAccessToken());
        }
    }

    public AndroidAuthSession getSession() {
        return ((DropboxAuthorizer)getAuthorizer()).getSession();
    }
}
