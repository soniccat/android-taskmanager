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
  void retrieveRequestToken(OAuthCompletion completion);
  void retrieveAccessToken(String code, OAuthCompletion completion);
  void signCommand(ServiceCommand command);
  String getVersion();
  String getAuthorizationUrl();

  interface OAuthCompletion {
    void onCompleted(Error error);
  }
}
