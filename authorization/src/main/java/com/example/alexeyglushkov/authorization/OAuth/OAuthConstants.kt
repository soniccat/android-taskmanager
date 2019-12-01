package com.example.alexeyglushkov.authorization.OAuth

/**
 * This class contains OAuth constants, used project-wide
 *
 * @author Pablo Fernandez
 */
object OAuthConstants {
    const val TIMESTAMP = "oauth_timestamp"
    const val SIGN_METHOD = "oauth_signature_method"
    const val SIGNATURE = "oauth_signature"
    const val CONSUMER_SECRET = "oauth_consumer_secret"
    const val CONSUMER_KEY = "oauth_consumer_key"
    const val CALLBACK = "oauth_callback"
    const val VERSION = "oauth_version"
    const val NONCE = "oauth_nonce"
    const val REALM = "realm"
    const val PARAM_PREFIX = "oauth_"
    const val TOKEN = "oauth_token"
    const val TOKEN_SECRET = "oauth_token_secret"
    const val OUT_OF_BAND = "oob"
    const val VERIFIER = "oauth_verifier"
    const val HEADER = "Authorization"
    val EMPTY_TOKEN = Token("", "")
    const val SCOPE = "scope"
    //OAuth 2.0
    const val ACCESS_TOKEN = "access_token"
    const val CLIENT_ID = "client_id"
    const val CLIENT_SECRET = "client_secret"
    const val REDIRECT_URI = "redirect_uri"
    const val CODE = "code"
}