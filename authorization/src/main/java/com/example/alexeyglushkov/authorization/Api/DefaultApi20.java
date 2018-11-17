package com.example.alexeyglushkov.authorization.Api;

import androidx.annotation.Nullable;

import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.OAuth.OAuth20AuthorizerImpl;
import com.example.alexeyglushkov.authorization.OAuth.OAuthConfig;
import com.example.alexeyglushkov.authorization.OAuth.OAuthConstants;
import com.example.alexeyglushkov.authorization.OAuth.OAuthCredentials;
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Default implementation of the OAuth protocol, version 2.0 (draft 11)
 *
 * This class is meant to be extended by concrete implementations of the API,
 * providing the endpoints and endpoint-http-verbs.
 *
 * If your Api adheres to the 2.0 (draft 11) protocol correctly, you just need to extend
 * this class and define the getters for your endpoints.
 *
 * If your Api does something a bit different, you can override the different
 * extractors or services, in order to fine-tune the process. Please read the
 * javadocs of the interfaces to get an idea of what to do.
 *
 * @author Diego Silveira
 *
 */
public abstract class DefaultApi20 implements OAuthApi
{
    private OAuthConfig config;

    @Override
    public void setOAuthConfig(OAuthConfig config) {
        this.config = config;
    }

    @Override
    public OAuthConfig getOAuthConfig() {
        return config;
    }

    /**
     * Returns the verb for the access token endpoint (defaults to GET)
     *
     * @return access token endpoint verb
     */
    public abstract void fillAccessTokenConnectionBuilder(HttpUrlConnectionBuilder builder, OAuthConfig config, String code);

    /**
     * Returns the URL where you should redirect your users to authenticate
     * your application.
     *
     * @param config OAuth 2.0 configuration param object
     * @return the URL where you should redirect your users
     */
    public abstract String getAuthorizationUrl(OAuthConfig config);

    /**
     * {@inheritDoc}
     */
    public Authorizer createAuthorizer()
    {
        return new OAuth20AuthorizerImpl(this, config);
    }

    public abstract OAuthCredentials createCredentials(String response);

    public void signCommand(ServiceCommand command, OAuthCredentials credentials) {
        command.getConnectionBulder().addQuerystringParameter(OAuthConstants.TOKEN, credentials.getAccessToken());
    }

    @Nullable
    protected String getEncodedCallback(OAuthConfig config) {
        String callback = null;
        try {
            callback = URLEncoder.encode(config.getCallback(), "UTF-8");
        } catch (UnsupportedEncodingException exception) {
            return null;
        }
        return callback;
    }
}
