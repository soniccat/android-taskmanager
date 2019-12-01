package com.example.alexeyglushkov.authorization.Api

import com.example.alexeyglushkov.authorization.Auth.Authorizer

/**
 * Contains all the configuration needed to instantiate a valid [OAuthService]
 *
 * @author Pablo Fernandez
 */
interface Api {
    fun createAuthorizer(): Authorizer
}