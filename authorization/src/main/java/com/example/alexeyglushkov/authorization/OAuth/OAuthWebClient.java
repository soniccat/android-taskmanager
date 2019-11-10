package com.example.alexeyglushkov.authorization.OAuth;

import io.reactivex.Single;

/**
 * Created by alexeyglushkov on 24.10.15.
 */

public interface OAuthWebClient {
    Single<String> loadUrl(String url, String callback);
}
