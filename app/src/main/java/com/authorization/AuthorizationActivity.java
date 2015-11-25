package com.authorization;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.alexeyglushkov.authcachemanager.AccountCacheStore;
import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.AuthCredentials;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Api.Foursquare2Api;
import com.example.alexeyglushkov.authorization.Auth.SimpleAccount;
import com.example.alexeyglushkov.authorization.OAuth.OAuth20Authorizer;
import com.example.alexeyglushkov.authorization.OAuth.OAuthWebClient;
import com.example.alexeyglushkov.authorization.OAuth.OAuthAuthorizerBuilder;
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskProvider;
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskRunner;
import com.main.MainApplication;
import com.main.Networks;
import com.rssclient.controllers.R;

import java.io.File;

/**
 * Created by alexeyglushkov on 24.10.15.
 */
public class AuthorizationActivity extends ActionBarActivity implements OAuthWebClient {
    private static final String TAG = "AuthorizationActivity";
    public static final String LOAD_URL = "LOAD_URL";

    private WebView webView;
    private OAuthWebClient.Callback webCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        webView = (WebView)findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(Networks.CALLBACK_URL)) {
                    webCallback.onResult(url);
                    finish();
                    return true;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                webCallback.onReceivedError(new Error("AuthorizationActivity webView error " + errorCode + " " + description));
                finish();
            }
        });

        String url = getIntent().getExtras().getString(LOAD_URL);
        loadUrl(url, AuthActivityProxy.getCurrentCallback());
    }

    @Override
    public void finish() {
        super.finish();

        AuthActivityProxy.setCurrentActivity(null);
        AuthActivityProxy.setCurrentCallback(null);
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
}
