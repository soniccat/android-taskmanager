package org.scribe.builder.api;

import org.scribe.extractors.RequestTokenExtractor;
import org.scribe.extractors.TokenExtractor20Impl;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuth20ServiceImpl;
import org.scribe.oauth.OAuthPocketServiceImpl;
import org.scribe.oauth.OAuthService;

/**
 * Created by alexeyglushkov on 25.10.15.
 */
public class PocketApi20 extends DefaultApi20 {

    /**
     * Returns the verb for the request token endpoint (defaults to POST)
     *
     * @return request token endpoint verb
     */
    public Verb getRequestTokenVerb()
    {
        return Verb.POST;
    }

    private static final String AUTHORIZATION_URL = "https://getpocket.com/auth/authorize?request_token=%s&redirect_uri=%s";

    @Override
    public String getAccessTokenEndpoint()
    {
        return "https://getpocket.com/v3/oauth/authorize";
    }

    public Verb getAccessTokenVerb()
    {
        return Verb.POST;
    }

    public String getRequestTokenEndpoint()
    {
        return "https://getpocket.com/v3/oauth/request";
    }

    public RequestTokenExtractor getRequestTokenExtractor()
    {
        return new TokenExtractor20Impl("code");
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig config) {
        throw new UnsupportedOperationException("Unsupported operation, please use 'getAuthorizationUrl(Token requestToken, OAuthConfig config)'");
    }

    public String getAuthorizationUrl(Token requestToken, OAuthConfig config) {
        return String.format(AUTHORIZATION_URL, requestToken.getToken(), config.getCallback());
    }

    public OAuthService createService(OAuthConfig config)
    {
        return new OAuthPocketServiceImpl(this, config);
    }
}
