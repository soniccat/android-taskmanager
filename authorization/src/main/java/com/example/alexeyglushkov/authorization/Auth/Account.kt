package com.example.alexeyglushkov.authorization.Auth

import io.reactivex.Single

/**
 * Created by alexeyglushkov on 15.11.15.
 */
// TODO: think about account manager and account, extract properties in another object
interface Account {
    val id: Int
    val serviceType: Int
    val credentials: AuthCredentials?
    val isAuthorized: Boolean
    @Throws(Exception::class)
    fun logout()

    var authorizer: Authorizer?
    fun setAuthCredentialStore(store: AccountStore)
    @Throws(Exception::class)
    fun store()

    // Method should be called not on main thread to be able show auth activity for OAuth20AuthorizerImpl
    fun authorize(): Single<AuthCredentials>

    fun signCommand(command: ServiceCommand<*>)
}