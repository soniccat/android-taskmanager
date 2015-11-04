package com.example.alexeyglushkov.authorization.OAuth;

import com.example.alexeyglushkov.authorization.Authorizer;

/**
 * Contains all the configuration needed to instantiate a valid {@link OAuthService}
 * 
 * @author Pablo Fernandez
 *
 */
public interface Api
{
  Authorizer createAuthorizer();
}
