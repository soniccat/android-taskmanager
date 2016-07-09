package com.example.alexeyglushkov.authorization.OAuth;

import android.net.Uri;

import com.example.alexeyglushkov.authorization.Api.DefaultApi20;
import com.example.alexeyglushkov.authorization.Auth.AuthCredentials;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.authorization.requestbuilder.Verb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class OAuth20AuthorizerImpl implements OAuth20Authorizer
{
  private static final String VERSION = "2.0";

  private final DefaultApi20 api;
  private final OAuthConfig config;
  private OAuthWebClient webClient;
  private ServiceCommandRunner commandRunner;
  private ServiceCommandProvider commandProvider;

  @Override
  public void setServiceCommandRunner(ServiceCommandRunner runner) {
    this.commandRunner = runner;
  }

  @Override
  public void setServiceCommandProvider(ServiceCommandProvider provider) {
    this.commandProvider = provider;
  }

  public void setWebClient(OAuthWebClient webClient) {
    this.webClient = webClient;
  }

  public OAuthWebClient getWebClient() {
    return webClient;
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
    HttpUrlConnectionBuilder builder = new HttpUrlConnectionBuilder();
    api.fillAccessTokenConnectionBuilder(builder, config, code);

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
  public String getVersion()
  {
    return VERSION;
  }

  @Override
  public String getAuthorizationUrl() {
    return api.getAuthorizationUrl(config);
  }

  @Override
  public void authorize(final AuthorizerCompletion completion) {
    String code = webAuthorization();
    if (code == null) {
      completion.onFinished(null, new Error("OAuthPocketServiceImpl authorize: Can't receive code"));

    } else {
      retrieveAccessToken(code, new OAuthCompletion() {
        @Override
        public void onCompleted(ServiceCommand command) {
          String response = command.getResponse();
          OAuthCredentials authCredentials = api.createCredentials(response);
          if (authCredentials != null && authCredentials.isValid()) {
            completion.onFinished(authCredentials, null);
          } else {
            completion.onFinished(null, new Error("OAuthPocketServiceImpl authorize: Can't receive requestToken"));
          }
        }
      });
    }
  }

  // TODO: return error and provide custom auth error
  private String webAuthorization() {
    String url = getAuthorizationUrl();

    final Semaphore waitSemaphore = new Semaphore(0);
    final List<Error> errorList = new ArrayList<>();
    final List<String> codeList = new ArrayList<>();

    OAuthWebClient.Callback callback = new OAuthWebClient.Callback() {
      @Override
      public void onReceivedError(Error error) {
        errorList.add(error);
        waitSemaphore.release();
      }

      @Override
      public void onResult(String resultUrl) {
        String code = getCode(resultUrl);
        codeList.add(code);

        waitSemaphore.release();
      }
    };

    getWebClient().loadUrl(url, callback);

    String resultCode = null;
    try {
      waitSemaphore.acquire();
    } catch (InterruptedException e) {
    }

    if (codeList != null && codeList.size() > 0) {
      resultCode = codeList.get(0);
    }

    return resultCode;
  }

  private String getCode(String resultUrl) {
    Uri uri = Uri.parse(resultUrl);
    return uri.getQueryParameter("code");
  }

  public void signCommand(ServiceCommand command, AuthCredentials credentials) {
    OAuthCredentials oAuthCredentials = (OAuthCredentials)credentials;
    api.signCommand(command, oAuthCredentials);
  }
}
