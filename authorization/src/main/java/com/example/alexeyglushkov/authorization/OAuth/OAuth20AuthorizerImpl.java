package com.example.alexeyglushkov.authorization.OAuth;

import android.net.Uri;

import com.example.alexeyglushkov.authorization.Api.DefaultApi20;
import com.example.alexeyglushkov.authorization.Auth.AuthCredentials;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;

import java.util.ArrayList;
import java.util.List;
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
		command.setServiceCommandCallback(new ServiceCommand.CommandCallback() {
			@Override
			public void onCompleted(Error error) {
                if (error != null) {
                    error = new AuthError("OAuthPocketServiceImpl authorize: Can't receive AccessToken", AuthError.Reason.InnerError, error);
                }

				completion.onCompleted(command, (AuthError)error);
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
		webAuthorization(new WebAuthCallback() {
            @Override
            public void onFinished(String code, AuthError error) {
                onWebAuthFinished(code, error, completion);
            }
        });
	}

    private void onWebAuthFinished(String code, AuthError error, final AuthorizerCompletion completion) {
        if (code == null || error != null) {
            completion.onFinished(null, error);

        } else {
            retrieveAccessToken(code, new OAuthCompletion() {
                @Override
                public void onCompleted(ServiceCommand command, AuthError error) {
                    if (error == null) {
                        String response = command.getResponse();
                        OAuthCredentials authCredentials = api.createCredentials(response);
                        if (authCredentials != null && authCredentials.isValid()) {
                            completion.onFinished(authCredentials, null);
                        } else {
                            AuthError localError = new AuthError("OAuthPocketServiceImpl authorize: Can't parse AccessToken", AuthError.Reason.UnknownError, null);
                            completion.onFinished(null, localError);
                        }
                    } else {
                        completion.onFinished(null, error);
                    }
                }
            });
        }
    }

	private void webAuthorization(final WebAuthCallback authCallback) {
		OAuthWebClient.Callback callback = new OAuthWebClient.Callback() {
			@Override
			public void onReceivedError(Error error) {
				AuthError authError = new AuthError(AuthError.Reason.InnerError, error);
                onFinished(null, authError);
			}

			@Override
			public void onResult(String resultUrl) {
                Uri uri = Uri.parse(resultUrl);

                String code = null;
                AuthError error = null;
                if (isCancelled(uri)) {
                    error = new AuthError(AuthError.Reason.Cancelled, null);

                } else {
                    code = getCode(uri);
                    if (code == null) {
                        error = new AuthError("OAuthPocketServiceImpl authorize: Can't parse code", AuthError.Reason.UnknownError, null);
                    }
                }

                onFinished(code, error);
			}

            private void onFinished(String code, AuthError error) {
                authCallback.onFinished(code, error);
            }
		};

        String url = getAuthorizationUrl();
		getWebClient().loadUrl(url, callback);
	}

	private String getCode(Uri uri) {
		return uri.getQueryParameter("code");
	}

    private boolean isCancelled(Uri uri) {
        String error = uri.getQueryParameter("error");
        return error != null && error.equals("access_denied");
    }

	public void signCommand(ServiceCommand command, AuthCredentials credentials) {
		OAuthCredentials oAuthCredentials = (OAuthCredentials)credentials;
		api.signCommand(command, oAuthCredentials);
	}

	private interface WebAuthCallback {
		void onFinished(String code, AuthError error);
	}
}
