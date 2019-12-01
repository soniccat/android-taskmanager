package com.example.alexeyglushkov.authorization.OAuth

import com.example.alexeyglushkov.authorization.Auth.Authorizer
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand
import io.reactivex.Single

interface OAuth20Authorizer : Authorizer {
    val authorizationUrl: String
    var webClient: OAuthWebClient?

    fun retrieveAccessToken(code: String): Single<ServiceCommand<String>>
}