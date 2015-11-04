package com.example.alexeyglushkov.authorization.Api;

import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Tools.AccessTokenExtractor;
import com.example.alexeyglushkov.authorization.OAuth.OAuth20AuthorizerImpl;
import com.example.alexeyglushkov.authorization.OAuth.OAuthConfig;
import com.example.alexeyglushkov.authorization.OAuth.TokenExtractor20Impl;
import com.example.alexeyglushkov.authorization.requestbuilder.Verb;

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
   * Returns the access token extractor.
   * 
   * @return access token extractor
   */
  public AccessTokenExtractor getAccessTokenExtractor()
  {
    return new TokenExtractor20Impl();
  }
	
  /**
   * Returns the verb for the access token endpoint (defaults to GET)
   * 
   * @return access token endpoint verb
   */
  public Verb getAccessTokenVerb()
  {
    return Verb.GET;
  }
	
  /**
   * Returns the URL that receives the access token requests.
   * 
   * @return access token URL
   */
  public abstract String getAccessTokenEndpoint();
	
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

}
