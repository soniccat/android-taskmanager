package com.example.alexeyglushkov.dropboxservice;

import android.support.annotation.NonNull;

import com.dropbox.client2.android.AndroidAuthSession;
import com.example.alexeyglushkov.authorization.Auth.AuthCredentials;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.authorization.OAuth.OAuthCredentials;
import com.example.alexeyglushkov.tools.ContextProvider;

/**
 * Created by alexeyglushkov on 10.07.16.
 */
public class DropboxAuthorizer implements Authorizer {
    private AndroidAuthSession session;
    private ContextProvider contextProvider;

    private AuthorizerCompletion completion;

    public DropboxAuthorizer(AndroidAuthSession session, ContextProvider contextProvider) {
        this.session = session;
        this.contextProvider = contextProvider;
    }

    public AndroidAuthSession getSession() {
        return session;
    }

    @Override
    public void authorize(AuthorizerCompletion completion) {
        this.completion = completion;
        session.startOAuth2Authentication(contextProvider.getContext());
    }

    // need to be called outside to finish authorization
    public void onResume() {
        if (completion == null) {
            return;
        }

        if (session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();

                OAuthCredentials credentials = createCredentials();
                completion.onFinished(credentials, null);

            } catch (IllegalStateException e) {
                completion.onFinished(null, new AuthError(AuthError.Reason.InnerError, e));

            } finally {
                completion = null;
            }
        } else if (completion != null) {
            completion.onFinished(null, new AuthError("authenticationSuccessful returns false", AuthError.Reason.UnknownError, null));
            completion = null;
        }
    }

    @NonNull
    private OAuthCredentials createCredentials() {
        String oauth2AccessToken = session.getOAuth2AccessToken();
        OAuthCredentials credentials = new OAuthCredentials();
        credentials.setAccessToken(oauth2AccessToken);
        return credentials;
    }

    @Override
    public void signCommand(ServiceCommand command, AuthCredentials credentials) {

    }

    @Override
    public void setServiceCommandProvider(ServiceCommandProvider provider) {

    }

    @Override
    public void setServiceCommandRunner(ServiceCommandRunner runner) {

    }
}
