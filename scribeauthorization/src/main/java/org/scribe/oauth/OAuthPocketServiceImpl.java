package org.scribe.oauth;

import android.support.annotation.NonNull;
import android.util.Log;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.builder.api.PocketApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.RequestTuner;
import org.scribe.model.Response;
import org.scribe.model.TimeoutTuner;
import org.scribe.model.Token;
import org.scribe.model.Verifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by alexeyglushkov on 25.10.15.
 */
public class OAuthPocketServiceImpl extends OAuth20ServiceImpl {
    public static final String POCKET_REQUEST_TOKEN = "request_token";
    public static final String POCKET_CONSUMER_KEY = "consumer_key";

    protected final PocketApi20 api;
    protected final OAuthConfig config;

    /**
     * Default constructor
     *
     * @param api OAuth2.0 api information
     * @param config OAuth 2.0 configuration param object
     */
    public OAuthPocketServiceImpl(PocketApi20 api, OAuthConfig config)
    {
        super(api, config);
        this.api = api;
        this.config = config;
    }

    /**
     * {@inheritDoc}
     */
    public Token getAccessToken(Token requestToken, Verifier verifier)
    {
        OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(), api.getAccessTokenEndpoint());
        request.addBodyParameter(POCKET_CONSUMER_KEY, config.getApiKey());
        request.addBodyParameter(OAuthConstants.CODE, verifier.getValue());

        Response response = request.send();
        return api.getAccessTokenExtractor().extract(response.getBody());
    }

    public String getAuthorizationUrl(Token requestToken)
    {
        return api.getAuthorizationUrl(requestToken, config);
    }

    public Token getRequestToken(int timeout, TimeUnit unit)
    {
        return getRequestToken(new TimeoutTuner(timeout, unit));
    }

    public Token getRequestToken()
    {
        return getRequestToken(2, TimeUnit.SECONDS);
    }

    /**
     * {@inheritDoc}
     */
    public Token getRequestToken(RequestTuner tuner)
    {
        config.log("obtaining request token from " + api.getRequestTokenEndpoint());
        OAuthRequest request = new OAuthRequest(api.getRequestTokenVerb(), api.getRequestTokenEndpoint());

        config.log("setting oauth_callback to " + config.getCallback());
        request.addBodyParameter(POCKET_CONSUMER_KEY, config.getApiKey());
        request.addBodyParameter(OAuthConstants.REDIRECT_URI, config.getCallback());

        config.log("sending request...");
        Response response = request.send(tuner);
        String body = response.getBody();

        config.log("response status code: " + response.getCode());
        Log.d("body", "response body: " + body);
        return api.getRequestTokenExtractor().extract(body);
    }

    @Override
    public void authorize(final OAuthServiceCompletion completion) {
        Token requestToken = getRequestToken();
        if (requestToken.getToken() != null) {
            completion.onReceivedRequestToken(requestToken);
            authorize(requestToken, completion);
        } else {
            completion.onReceivedError(new Error("OAuthPocketServiceImpl authorize: Can't receive requestToken"));
        }
    }

    private void authorize(Token requestToken, final OAuthServiceCompletion completion) {
        Error webError = webAuthorization(requestToken);

        if (webError != null) {
            completion.onReceivedError(webError);

        } else {
            Token accessToken = getAccessToken(null, new Verifier(requestToken.getToken()));
            if (accessToken != null) {
                completion.onReceivedAccessToken(accessToken);
            } else {
                completion.onReceivedError(new Error("OAuthPocketServiceImpl authorize: Can't receive requestToken"));
            }
        }
    }

    @NonNull
    private Error webAuthorization(Token requestToken) {
        String url = getAuthorizationUrl(requestToken);

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
