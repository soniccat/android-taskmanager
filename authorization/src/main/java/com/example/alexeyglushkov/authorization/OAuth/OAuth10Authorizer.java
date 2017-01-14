package com.example.alexeyglushkov.authorization.OAuth;

import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;

/**
 * The com.example.alexeyglushkov.wordteacher.main Scribe object.
 * 
 * A facade responsible for the retrieval of request and access tokens and for the signing of HTTP requests.  
 * 
 * @author Pablo Fernandez
 */
public interface OAuth10Authorizer extends Authorizer
{
  /**
   * Retrieve the request token.
   * 
   * @return request token
   */
  void retrieveRequestToken(OAuthCompletion completion);

  /**
   * Retrieve the access token
   * 
   * @param requestToken request token (obtained previously)
   * @param verifier verifier code
   * @return access token
   */
  void retrieveAccessToken(String code, OAuthCompletion completion);

  /**
   * Signs am OAuth request
   * 
   * @param accessToken access token (obtained previously)
   * @param request request to sign
   */
  void signCommand(ServiceCommand command);

  /**
   * Returns the OAuth version of the service.
   * 
   * @return oauth version as string
   */
  String getVersion();
  
  /**
   * Returns the URL where you should redirect your users to authenticate
   * your application.
   * 
   * @param requestToken the request token you need to authorize
   * @return the URL where you should redirect your users
   */
  String getAuthorizationUrl();

  interface OAuthCompletion {
    void onCompleted(Error error);
  }
}
