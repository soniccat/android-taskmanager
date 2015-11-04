package com.example.alexeyglushkov.authorization.OAuth;

/**
 * Created by alexeyglushkov on 04.11.15.
 */
public interface OAuthApi extends Api {
    void setOAuthConfig(OAuthConfig config);
    OAuthConfig getOAuthConfig();
}
