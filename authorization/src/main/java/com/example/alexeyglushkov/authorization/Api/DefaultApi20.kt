package com.example.alexeyglushkov.authorization.Api

import com.example.alexeyglushkov.authorization.Auth.Authorizer
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand
import com.example.alexeyglushkov.authorization.OAuth.OAuth20AuthorizerImpl
import com.example.alexeyglushkov.authorization.OAuth.OAuthConfig
import com.example.alexeyglushkov.authorization.OAuth.OAuthConstants
import com.example.alexeyglushkov.authorization.OAuth.OAuthCredentials
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

/**
 * Default implementation of the OAuth protocol, version 2.0 (draft 11)
 *
 * This class is meant to be extended by concrete implementations of the API,
 * providing the endpoints and endpoint-http-verbs.
 *
 * If your Api adheres to the 2.0 (draft 11) protocol correctly, you just need to extend
 * this class and define the getters for your endpoints.
 *
 * If your Api does something a bit different, you can override the different
 * extractors or services, in order to fine-tune the process. Please read the
 * javadocs of the interfaces to get an idea of what to do.
 *
 * @author Diego Silveira
 */
abstract class DefaultApi20 : OAuthApi {
    override lateinit var config: OAuthConfig

    /**
     * Returns the verb for the access token endpoint (defaults to GET)
     *
     * @return access token endpoint verb
     */
    abstract fun fillAccessTokenConnectionBuilder(builder: HttpUrlConnectionBuilder, config: OAuthConfig, code: String)

    /**
     * Returns the URL where you should redirect your users to authenticate
     * your application.
     *
     * @param config OAuth 2.0 configuration param object
     * @return the URL where you should redirect your users
     */
    abstract fun getAuthorizationUrl(config: OAuthConfig): String

    /**
     * {@inheritDoc}
     */
    override fun createAuthorizer(): Authorizer {
        return OAuth20AuthorizerImpl(this, config)
    }

    abstract fun createCredentials(response: String): OAuthCredentials

    open fun signCommand(command: ServiceCommand<*>, credentials: OAuthCredentials) {
        val accessToken = credentials.accessToken
        if (accessToken != null) {
            command.connectionBuilder.addQuerystringParameter(OAuthConstants.TOKEN, accessToken)
        }
    }

    protected fun getEncodedCallback(config: OAuthConfig): String? {
        var callback: String? = null
        callback = try {
            URLEncoder.encode(config.callback, "UTF-8")
        } catch (exception: UnsupportedEncodingException) {
            return null
        }
        return callback
    }
}