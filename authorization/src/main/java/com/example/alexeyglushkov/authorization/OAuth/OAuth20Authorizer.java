package com.example.alexeyglushkov.authorization.OAuth;

import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;

/**
 * The main Scribe object. 
 * 
 * A facade responsible for the retrieval of request and access tokens and for the signing of HTTP requests.  
 * 
 * @author Pablo Fernandez
 */
public interface OAuth20Authorizer extends Authorizer
{
  /**
   * Retrieve the access token
   * 
   * @param requestToken request token (obtained previously)
   * @param verifier verifier code
   * @return access token
   */
  void retrieveAccessToken(String code, OAuthCompletion completion);
  
  /**
   * Returns the URL where you should redirect your users to authenticate
   * your application.
   * 
   * @param requestToken the request token you need to authorize
   * @return the URL where you should redirect your users
   */
  String getAuthorizationUrl();

  void setWebClient(OAuthWebClient webClient);

  interface OAuthCompletion {
    void onCompleted(ServiceCommand command);
  }
}
