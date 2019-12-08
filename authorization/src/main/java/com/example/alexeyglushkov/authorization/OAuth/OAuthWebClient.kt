package com.example.alexeyglushkov.authorization.OAuth

import io.reactivex.Single

/**
 * Created by alexeyglushkov on 24.10.15.
 */
interface OAuthWebClient {
    suspend fun loadUrl(url: String, callback: String): String
}