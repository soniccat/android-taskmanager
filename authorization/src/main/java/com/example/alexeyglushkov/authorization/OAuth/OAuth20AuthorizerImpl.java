package com.example.alexeyglushkov.authorization.OAuth;

import android.net.Uri;

import com.example.alexeyglushkov.authorization.Api.DefaultApi20;
import com.example.alexeyglushkov.authorization.Auth.AuthCredentials;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.tools.CancelError;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

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
	public Single<ServiceCommand> retrieveAccessToken(String code) {
		HttpUrlConnectionBuilder builder = new HttpUrlConnectionBuilder();
		api.fillAccessTokenConnectionBuilder(builder, config, code);

		final ServiceCommand command = commandProvider.getServiceCommand(builder);
//		command.setServiceCommandCallback(new ServiceCommand.CommandCallback() {
//			@Override
//			public void onCompleted(ServiceCommand command, Error error) {
//                if (error != null) {
//                    error = new AuthError("OAuthPocketServiceImpl authorize: Can't receive AccessToken", AuthError.Reason.InnerError, error);
//
//                }
//
//				completion.onCompleted(command, (AuthError)error);
//			}
//		});

		return commandRunner.run(command).onErrorResumeNext(new Function<Throwable, SingleSource<ServiceCommand>>() {
			@Override
			public SingleSource<ServiceCommand> apply(Throwable throwable) throws Exception {
				Error err = new AuthError("OAuth20AuthorizerImpl authorize: Can't receive AccessToken", AuthError.Reason.InnerError, throwable);
				return Single.error(err);
			}
		});
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
	public Single<AuthCredentials> authorize() {
//		webAuthorization(new WebAuthCallback() {
//            @Override
//            public void onFinished(String code, AuthError error) {
//                onWebAuthFinished(code, error);
//            }
//        });

		return webAuthorization().flatMap(new Function<String, SingleSource<? extends AuthCredentials>>() {
			@Override
			public SingleSource<? extends AuthCredentials> apply(String s) throws Exception {
				return onWebAuthFinished(s);
			}
		});
	}

    private Single<? extends AuthCredentials> onWebAuthFinished(String code) {
        if (code == null) {
            return Single.error(new Error("OAuth20AuthorizerImpl authorize: empty code"));

        } else {
//            retrieveAccessToken(code, new OAuthCompletion() {
//                @Override
//                public void onCompleted(ServiceCommand command, AuthError error) {
//                    if (error == null) {
//                        String response = command.getResponse();
//                        OAuthCredentials authCredentials = api.createCredentials(response);
//                        if (authCredentials != null && authCredentials.isValid()) {
//                            completion.onFinished(authCredentials, null);
//                        } else {
//                            AuthError localError = new AuthError("OAuthPocketServiceImpl authorize: Can't parse AccessToken", AuthError.Reason.UnknownError, null);
//                            completion.onFinished(null, localError);
//                        }
//                    } else {
//                        completion.onFinished(null, error);
//                    }
//                }
//            });

			return retrieveAccessToken(code).flatMap(new Function<ServiceCommand, SingleSource<? extends AuthCredentials>>() {
				@Override
				public SingleSource<? extends AuthCredentials> apply(ServiceCommand command) throws Exception {
					String response = command.getResponse();
					OAuthCredentials authCredentials = api.createCredentials(response);

					Single<? extends AuthCredentials> result;
					if (authCredentials != null && authCredentials.isValid()) {
						result = Single.just(authCredentials);
					} else {
						AuthError localError = new AuthError("OAuth20AuthorizerImpl authorize: Can't parse AccessToken", AuthError.Reason.UnknownError, null);
						result = Single.error(localError);
					}

					return result;
				}
			});
        }
    }

	private Single<String> webAuthorization() {
//		OAuthWebClient.Callback callback = new OAuthWebClient.Callback() {
//			@Override
//			public void onReceivedError(Error error) {
//				AuthError.Reason reason;
//				if (error instanceof CancelError) {
//					reason = AuthError.Reason.Cancelled;
//				} else {
//					reason = AuthError.Reason.InnerError;
//				}
//
//				AuthError authError = new AuthError(reason, error);
//                onFinished(null, authError);
//			}
//
//			@Override
//			public void onResult(String resultUrl) {
//                Uri uri = Uri.parse(resultUrl);
//
//                String code = null;
//                AuthError error = null;
//                if (isCancelled(uri)) {
//                    error = new AuthError(AuthError.Reason.Cancelled, null);
//
//                } else {
//                    code = getCode(uri);
//                    if (code == null) {
//                        error = new AuthError("OAuthPocketServiceImpl authorize: Can't parse code", AuthError.Reason.UnknownError, null);
//                    }
//                }
//
//                onFinished(code, error);
//			}
//
//            private void onFinished(String code, AuthError error) {
//                authCallback.onFinished(code, error);
//            }
//		};

        String url = getAuthorizationUrl();
		return getWebClient().loadUrl(url).flatMap(new Function<String, SingleSource<? extends String>>() {
			@Override
			public SingleSource<? extends String> apply(String url) throws Exception {
				Uri uri = Uri.parse(url);

				String code = null;
				AuthError error = null;
				if (isCancelled(uri)) {
					error = new AuthError(AuthError.Reason.Cancelled, null);

				} else {
					code = getCode(uri);
					if (code == null) {
						error = new AuthError("OAuth20AuthorizerImpl authorize: Can't parse code", AuthError.Reason.UnknownError, null);
					}
				}

				if (error != null) {
					return Single.error(error);
				} else {
					return Single.just(code);
				}
			}
		}).onErrorResumeNext(new Function<Throwable, SingleSource<? extends String>>() {
			@Override
			public SingleSource<? extends String> apply(Throwable throwable) throws Exception {
				AuthError.Reason reason;
				if (throwable instanceof CancelError) {
					reason = AuthError.Reason.Cancelled;
				} else {
					reason = AuthError.Reason.InnerError;
				}

				AuthError authError = new AuthError(reason, throwable);
                return Single.error(authError);
			}
		});
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

//	private interface WebAuthCallback {
//		void onFinished(String code, AuthError error);
//	}
}
