package com.example.alexeyglushkov.authorization.OAuth;

import android.support.annotation.NonNull;

import com.example.alexeyglushkov.authorization.AuthCredentialStore;
import com.example.alexeyglushkov.authorization.AuthCredentials;
import com.example.alexeyglushkov.authorization.Authorizer;
import com.example.alexeyglushkov.authorization.ServiceCommand;
import com.example.alexeyglushkov.authorization.ServiceCommandProvider;
import com.example.alexeyglushkov.authorization.ServiceCommandRunner;
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class OAuth20AuthorizerImpl implements OAuth20Authorizer
{
  private static final String VERSION = "2.0";

  private final DefaultApi20 api;
  private final OAuthConfig config;
  private ServiceCommandRunner commandRunner;
  private ServiceCommandProvider commandProvider;

  private AuthCredentials authCredentials;
  private AuthCredentialStore credentialStore;

  @Override
  public void setServiceCommandRunner(ServiceCommandRunner runner) {
    this.commandRunner = runner;
  }

  @Override
  public void setServiceCommandProvider(ServiceCommandProvider provider) {
    this.commandProvider = provider;
  }

  @Override
  public void setAuthCredentialStore(AuthCredentialStore store) {
    credentialStore = store;
  }

  @Override
  public AuthCredentials getCredentials() {
    return authCredentials;
  }

  @Override
  public boolean isAuthorized() {
    OAuthCredentials oAuthCredentials = getOAuthCredentials();
    return oAuthCredentials != null && oAuthCredentials.getAccessToken() != null && !oAuthCredentials.isExpired();
  }

  @Override
  public void logout() {
    if (getOAuthCredentials() != null) {
      credentialStore.removeCredentials(getOAuthCredentials().getId());
    }
  }

  private OAuthCredentials getOAuthCredentials() {
    return (OAuthCredentials)getCredentials();
  }

  /**
   * Default constructor
   *
   * @param api OAuth2.0 api information
   * @param config OAuth 2.0 configuration param object
   */
  public OAuth20AuthorizerImpl(DefaultApi20 api, OAuthConfig config)
  {
    this.api = api;
    this.config = config;
  }

  @Override
  public void retrieveAccessToken(String code, final OAuthCompletion completion) {
    HttpUrlConnectionBuilder builder = new HttpUrlConnectionBuilder(api.getAccessTokenEndpoint())
            .addQuerystringParameter(OAuthConstants.CLIENT_ID, config.getApiKey())
            .addQuerystringParameter(OAuthConstants.CLIENT_SECRET, config.getApiSecret())
            .addQuerystringParameter(OAuthConstants.CODE, code);
    if(config.hasScope()) {
      builder.addQuerystringParameter(OAuthConstants.SCOPE, config.getScope());
    }

    final ServiceCommand command = commandProvider.getServiceCommand(builder);
    command.setServiceCommandCallback(new ServiceCommand.Callback() {
      @Override
      public void onCompleted() {
        completion.onCompleted(command);
      }
    });

    commandRunner.run(command);
  }

  /**
   * {@inheritDoc}
   */
  public Token getRequestToken()
  {
    throw new UnsupportedOperationException("Unsupported operation, please use 'getAuthorizationUrl' and redirect your users there");
  }

  /**
   * {@inheritDoc}
   */
  public String getVersion()
  {
    return VERSION;
  }

  @Override
  public void signCommand(ServiceCommand command) {
    command.getConnectionBulder().addQuerystringParameter(OAuthConstants.ACCESS_TOKEN, getOAuthCredentials().getAccessToken());
  }

  @Override
  public String getAuthorizationUrl() {
    return api.getAuthorizationUrl(config);
  }

  @Override
  public void authorize(final AuthorizerCompletion completion) {
    Error webError = webAuthorization();

    if (webError != null) {
      completion.onFinished(webError);

    } else {
      retrieveAccessToken("", new OAuthCompletion() {
        @Override
        public void onCompleted(ServiceCommand command) {
          Token accessToken = api.getAccessTokenExtractor().extract(command.getResponse());

          if (accessToken != null) {
            storeAccessToken(accessToken);
            completion.onFinished(null);
          } else {
            completion.onFinished(new Error("OAuthPocketServiceImpl authorize: Can't receive requestToken"));
          }
        }
      });
    }
  }

  private void storeAccessToken(Token accessToken) {
    getOAuthCredentials().setAccessToken(accessToken.getToken());
    credentialStore.putCredentials(getOAuthCredentials());
  }

  @NonNull
  private Error webAuthorization() {
    String url = getAuthorizationUrl();

    final Semaphore waitSemaphore = new Semaphore(0);
    final List<Error> errorList = new ArrayList<>();

    OAuthWebClient.Callback callback = new OAuthWebClient.Callback() {
      @Override
      public void onReceivedError(Error error) {
        errorList.add(error);
        waitSemaphore.release();
      }

      @Override
      public void onResult(String result) {
        waitSemaphore.release();
      }
    };

    config.getWebClient().loadUrl(url, callback);

    Error resultError = null;
    try {
      waitSemaphore.acquire();
    } catch (InterruptedException e) {
      resultError = new Error("OAuthPocketServiceImpl authorize InterruptedException: " + e.getMessage());
    }

    if (resultError == null && errorList.size() > 0) {
      resultError = errorList.get(0);
    }

    return resultError;
  }
}
