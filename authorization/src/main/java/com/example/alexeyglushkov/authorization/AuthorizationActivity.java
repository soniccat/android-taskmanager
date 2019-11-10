package com.example.alexeyglushkov.authorization;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.Single;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.alexeyglushkov.authorization.OAuth.OAuthWebClient;
import com.example.alexeyglushkov.tools.CancelError;

/**
 * Created by alexeyglushkov on 24.10.15.
 */
public class AuthorizationActivity extends AppCompatActivity /*implements OAuthWebClient*/ {
    private static final String TAG = "AuthorizationActivity";
    public static final String LOAD_URL = "LOAD_URL";
    public static final String CALLBACK_URL = "CALLBACK_URL";

    private WebView webView;
    private boolean isHandled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String callback = getIntent().getExtras().getString(CALLBACK_URL);
                if (url.startsWith(callback)) {
                    AuthActivityProxy.finish(url, null);
                    if (!isHandled) {
                        isHandled = true;
                        finish();
                    }
                    return true;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Error error = new Error("AuthorizationActivity webView error " + errorCode + " " + description);
                if (!isHandled) {
                    AuthActivityProxy.finish(null, error);
                    isHandled = true;
                    finish();
                }
            }
        });

        final String url = getIntent().getExtras().getString(LOAD_URL);
        AuthorizationActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(url);
            }
        });
    }

    @Override
    public void finish() {
        if (!isHandled) {
            AuthActivityProxy.finish(null, new CancelError());
            isHandled = true;
        }

        webView.setWebViewClient(null);
        super.finish();

        if (AuthActivityProxy.getCurrentActivity() == this) {
            AuthActivityProxy.setCurrentActivity(null);
        }
    }

    public Single<String> loadUrl(final String url) {
        AuthorizationActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(url);
            }
        });

        return AuthActivityProxy.getAuthResult();
    }
}
