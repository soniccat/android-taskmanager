package com.example.alexeyglushkov.wordteacher.authorization;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.alexeyglushkov.authorization.OAuth.OAuthWebClient;
import com.example.alexeyglushkov.tools.CancelError;
import com.example.alexeyglushkov.wordteacher.R;

import com.example.alexeyglushkov.wordteacher.main.Networks;

/**
 * Created by alexeyglushkov on 24.10.15.
 */
public class AuthorizationActivity extends AppCompatActivity implements OAuthWebClient {
    private static final String TAG = "AuthorizationActivity";
    public static final String LOAD_URL = "LOAD_URL";

    private WebView webView;

    // TODO: think about pending intent or something else
    private @Nullable OAuthWebClient.Callback webCallback;

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
                    if (webCallback != null) {
                        webCallback.onResult(url);
                        webCallback = null;
                    }
                    finish();
                    return true;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (webCallback != null) {
                    webCallback.onReceivedError(new Error("AuthorizationActivity webView error " + errorCode + " " + description));
                    webCallback = null;
                }
                finish();
            }
        });

        String url = getIntent().getExtras().getString(LOAD_URL);
        loadUrl(url, AuthActivityProxy.getCurrentCallback());
    }

    @Override
    public void finish() {
        if (webCallback != null) {
            webCallback.onReceivedError(new CancelError());
            webCallback = null;
        }

        super.finish();

        if (AuthActivityProxy.getCurrentActivity() == this) {
            AuthActivityProxy.setCurrentActivity(null);
            AuthActivityProxy.setCurrentCallback(null);
        }
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
