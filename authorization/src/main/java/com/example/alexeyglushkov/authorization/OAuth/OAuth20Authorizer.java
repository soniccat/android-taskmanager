package com.example.alexeyglushkov.authorization.OAuth;

import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * The com.example.alexeyglushkov.wordteacher.main Scribe object.
 * 
 * A facade responsible for the retrieval of request and access tokens and for the signing of HTTP requests.  
 * 
 * @author Pablo Fernandez
 */
public interface OAuth20Authorizer extends Authorizer
{
  Single<ServiceCommand> retrieveAccessToken(String code);
  String getAuthorizationUrl();
  void setWebClient(OAuthWebClient webClient);
}
