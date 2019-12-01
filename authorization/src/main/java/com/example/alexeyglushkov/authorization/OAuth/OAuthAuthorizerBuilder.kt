package com.example.alexeyglushkov.authorization.OAuth

import com.example.alexeyglushkov.authorization.Api.OAuthApi
import com.example.alexeyglushkov.authorization.Auth.Authorizer
import org.junit.Assert

/**
 * Implementation of the Builder pattern, with a fluent interface that creates a
 * [OAuthService]
 *
 * @author Pablo Fernandez
 */
class OAuthAuthorizerBuilder {
    private var apiKey: String? = null
    private var apiSecret: String? = null
    private var callback: String
    private var scope: String? = null
    private var signatureType: SignatureType

    /**
     * Adds an OAuth callback url
     *
     * @param callback callback url. Must be a valid url or 'oob' for out of band OAuth
     * @return the [OAuthAuthorizerBuilder] instance for method chaining
     */
    fun callback(callback: String): OAuthAuthorizerBuilder {
        Assert.assertNotNull(callback)
        this.callback = callback
        return this
    }

    /**
     * Configures the api key
     *
     * @param apiKey The api key for your application
     * @return the [OAuthAuthorizerBuilder] instance for method chaining
     */
    fun apiKey(apiKey: String?): OAuthAuthorizerBuilder {
        Assert.assertNotNull(apiKey)
        this.apiKey = apiKey
        return this
    }

    /**
     * Configures the api secret
     *
     * @param apiSecret The api secret for your application
     * @return the [OAuthAuthorizerBuilder] instance for method chaining
     */
    fun apiSecret(apiSecret: String?): OAuthAuthorizerBuilder {
        Assert.assertNotNull(apiSecret)
        this.apiSecret = apiSecret
        return this
    }

    /**
     * Configures the OAuth scope. This is only necessary in some APIs (like Google's).
     *
     * @param scope The OAuth scope
     * @return the [OAuthAuthorizerBuilder] instance for method chaining
     */
    fun scope(scope: String?): OAuthAuthorizerBuilder {
        Assert.assertNotNull(scope)
        this.scope = scope
        return this
    }

    fun signatureType(type: SignatureType): OAuthAuthorizerBuilder {
        Assert.assertNotNull(type)
        signatureType = type
        return this
    }

    fun build(api: OAuthApi): Authorizer? {
        val apiKey = apiKey
        val apiSecret = apiSecret
        checkNotNull(apiKey)
        checkNotNull(apiSecret)
        val config = OAuthConfig(apiKey, apiSecret, callback, signatureType, scope)
        api.config = config
        return api.createAuthorizer()
    }

    /**
     * Default constructor
     */
    init {
        callback = OAuthConstants.OUT_OF_BAND
        signatureType = SignatureType.Header
    }
}