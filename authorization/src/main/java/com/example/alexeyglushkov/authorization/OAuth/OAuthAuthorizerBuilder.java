package com.example.alexeyglushkov.authorization.OAuth;

import com.example.alexeyglushkov.authorization.Api.OAuthApi;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;

import junit.framework.Assert;

/**
 * Implementation of the Builder pattern, with a fluent interface that creates a
 * {@link OAuthService}
 * 
 * @author Pablo Fernandez
 *
 */
public class OAuthAuthorizerBuilder
{
  private String apiKey;
  private String apiSecret;
  private String callback;
  private String scope;
  private SignatureType signatureType;
  private OAuthWebClient webClient;
  
  /**
   * Default constructor
   */
  public OAuthAuthorizerBuilder()
  {
    this.callback = OAuthConstants.OUT_OF_BAND;
    this.signatureType = SignatureType.Header;
  }

  /**
   * Adds an OAuth callback url
   * 
   * @param callback callback url. Must be a valid url or 'oob' for out of band OAuth
   * @return the {@link OAuthAuthorizerBuilder} instance for method chaining
   */
  public OAuthAuthorizerBuilder callback(String callback)
  {
    Assert.assertNotNull(callback);
    this.callback = callback;
    return this;
  }
  
  /**
   * Configures the api key
   * 
   * @param apiKey The api key for your application
   * @return the {@link OAuthAuthorizerBuilder} instance for method chaining
   */
  public OAuthAuthorizerBuilder apiKey(String apiKey)
  {
    Assert.assertNotNull(apiKey);
    this.apiKey = apiKey;
    return this;
  }
  
  /**
   * Configures the api secret
   * 
   * @param apiSecret The api secret for your application
   * @return the {@link OAuthAuthorizerBuilder} instance for method chaining
   */
  public OAuthAuthorizerBuilder apiSecret(String apiSecret)
  {
    Assert.assertNotNull(apiSecret);
    this.apiSecret = apiSecret;
    return this;
  }
  
  /**
   * Configures the OAuth scope. This is only necessary in some APIs (like Google's).
   * 
   * @param scope The OAuth scope
   * @return the {@link OAuthAuthorizerBuilder} instance for method chaining
   */
  public OAuthAuthorizerBuilder scope(String scope)
  {
    Assert.assertNotNull(scope);
    this.scope = scope;
    return this;
  }

  public OAuthAuthorizerBuilder signatureType(SignatureType type)
  {
    Assert.assertNotNull(type);
    this.signatureType = type;
    return this;
  }

  public OAuthAuthorizerBuilder webClient(OAuthWebClient webClient) {
    this.webClient = webClient;
    return this;
  }

  public Authorizer build(OAuthApi api)
  {
    Assert.assertNotNull(api);
    Assert.assertNotNull(apiKey);

    OAuthConfig config = new OAuthConfig(apiKey, apiSecret, callback, signatureType, scope, webClient);
    api.setOAuthConfig(config);

    return api.createAuthorizer();
  }
}
