package org.scribe.oauth;

/**
 * Created by alexeyglushkov on 24.10.15.
 */

public interface OAuthWebClient {
    void loadUrl(String url, Callback callback);

    interface Callback {
        void onReceivedError(Error error);
        void onResult(String result);
    }
}
