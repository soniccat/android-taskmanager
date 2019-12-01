package com.example.alexeyglushkov.authorization.Api

import com.example.alexeyglushkov.authorization.OAuth.OAuthConfig
import com.example.alexeyglushkov.authorization.OAuth.OAuthConstants
import com.example.alexeyglushkov.authorization.OAuth.OAuthCredentials
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder
import org.junit.Assert

// TODO: refactor as QuizletApi
class Foursquare2Api : DefaultApi20() {
    override fun getAuthorizationUrl(config: OAuthConfig): String {
        val callback = getEncodedCallback(config)
        checkNotNull(config.callback)

        return String.format(AUTHORIZATION_URL, config.apiKey, callback)
    }

    override fun fillAccessTokenConnectionBuilder(builder: HttpUrlConnectionBuilder, config: OAuthConfig, code: String) {
        checkNotNull(config.callback)

        builder.setUrl("https://foursquare.com/oauth2/access_token?grant_type=authorization_code")
        builder.addQuerystringParameter(OAuthConstants.CLIENT_ID, config.apiKey)
        builder.addQuerystringParameter(OAuthConstants.CLIENT_SECRET, config.apiSecret)
        builder.addQuerystringParameter(OAuthConstants.CODE, code)
        builder.addQuerystringParameter(OAuthConstants.REDIRECT_URI, config.callback)

        if (config.scope != null) {
            builder.addQuerystringParameter(OAuthConstants.SCOPE, config.scope)
        }
    }

    override fun createCredentials(response: String): OAuthCredentials {
        TODO("Need to implement")
    }

    companion object {
        private const val AUTHORIZATION_URL = "https://foursquare.com/oauth2/authenticate?client_id=%s&response_type=code&redirect_uri=%s"
    }
}