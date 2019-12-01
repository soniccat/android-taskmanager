package com.example.alexeyglushkov.authorization.OAuth

import com.example.alexeyglushkov.authorization.Auth.Authorizer
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand

/**
 * The com.example.alexeyglushkov.wordteacher.main Scribe object.
 *
 * A facade responsible for the retrieval of request and access tokens and for the signing of HTTP requests.
 *
 * @author Pablo Fernandez
 */
interface OAuth10Authorizer : Authorizer {
    fun retrieveRequestToken(completion: OAuthCompletion?)
    fun retrieveAccessToken(code: String?, completion: OAuthCompletion?)
    fun signCommand(command: ServiceCommand<*>?)
    val version: String?
    val authorizationUrl: String?

    interface OAuthCompletion {
        fun onCompleted(error: Error?)
    }
}