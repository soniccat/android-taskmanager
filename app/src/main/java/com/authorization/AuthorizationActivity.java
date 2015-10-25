package com.authorization;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rssclient.controllers.R;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.PocketApi20;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;
import org.scribe.oauth.OAuthWebClient;

/**
 * Created by alexeyglushkov on 24.10.15.
 */
public class AuthorizationActivity extends ActionBarActivity implements OAuthWebClient {

    private static final String CALLBACK_URL = "http://ya.ru";

    private WebView webView;
    OAuthService service;
    OAuthWebClient.Callback webCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        webView = (WebView)findViewById(R.id.web_view);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(CALLBACK_URL)) {
                    webCallback.onResult(url);
                    return true;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                webCallback.onReceivedError(new Error("AuthorizationActivity webView error " + errorCode + " " + description));
            }
        });

        startAutorization();
    }

    @Override
    public void loadUrl(final String url, final Callback callback) {
        AuthorizationActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(url);
                webCallback = callback;
            }
        });
    }

    private void startAutorization() {
        getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Object>() {
            @Override
            public Loader<Object> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<Object>(AuthorizationActivity.this) {
                    @Override
                    public Object loadInBackground() {
                        authorize();
                        return null;
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<Object> loader, Object data) {

            }

            @Override
            public void onLoaderReset(Loader<Object> loader) {

            }
        }).forceLoad();
    }

    private void authorize() {
        String apiKey = "12158-1a847cd6722e23d04c5007db";
        this.service = new ServiceBuilder()
                .provider(PocketApi20.class)
                .apiKey(apiKey)
                .callback(CALLBACK_URL)
                .webClient(this)
                .build();

        service.authorize(new OAuthService.OAuthServiceCompletion() {
            @Override
            public void onReceivedRequestToken(Token requestToken) {
                Log.d("tag", requestToken.getToken());
            }

            @Override
            public void onReceivedAccessToken(Token accessToken) {
                Log.d("tag", accessToken.getToken());
            }

            @Override
            public void onReceivedError(Error error) {
                Log.d("tag", "error" + error.getMessage());
            }
        });
    }
}
