package com.example.alexeyglushkov.authorization;

/**
 * The main Scribe object. 
 * 
 * A facade responsible for the retrieval of request and access tokens and for the signing of HTTP requests.  
 * 
 * @author Pablo Fernandez
 */
public interface OAuthAuthorizer extends Authorizer
{
  /**
   * Retrieve the request token.
   * 
   * @return request token
   */
  public Token retrieveRequestToken();

  /**
   * Retrieve the access token
   * 
   * @param requestToken request token (obtained previously)
   * @param verifier verifier code
   * @return access token
   */
  public Token retrieveAccessToken(Token requestToken);

  /**
   * Signs am OAuth request
   * 
   * @param accessToken access token (obtained previously)
   * @param request request to sign
   */
  public void signCommand(ServiceCommand command);

  /**
   * Returns the OAuth version of the service.
   * 
   * @return oauth version as string
   */
  public String getVersion();
  
  /**
   * Returns the URL where you should redirect your users to authenticate
   * your application.
   * 
   * @param requestToken the request token you need to authorize
   * @return the URL where you should redirect your users
   */
  public String getAuthorizationUrl(Token requestToken);
}
