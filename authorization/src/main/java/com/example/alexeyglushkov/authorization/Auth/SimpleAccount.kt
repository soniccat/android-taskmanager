package com.example.alexeyglushkov.authorization.Auth

import com.example.alexeyglushkov.authorization.Auth.Authorizer.AuthError
import com.example.alexeyglushkov.authorization.Auth.Authorizer.AuthError.Reason
import io.reactivex.Single
import io.reactivex.functions.Function
import java.io.Serializable

/**
 * Created by alexeyglushkov on 15.11.15.
 */
open class SimpleAccount(override val serviceType: Int) : Account, Serializable {
    override var id = 0
    override var credentials: AuthCredentials? = null
    @Transient override var authorizer: Authorizer? = null
    @Transient private var accountStore: AccountStore? = null

    override val isAuthorized: Boolean
    get() {
        // isValid should be called manually
        return credentials != null
    }

    @Throws(Exception::class)
    override fun logout() {
        if (credentials != null) {
            accountStore?.removeAccount(id)
        }
    }

    override fun setAuthCredentialStore(store: AccountStore) {
        accountStore = store
    }

    override suspend fun authorize(): AuthCredentials { //        authorizer.authorize(new Authorizer.AuthorizerCompletion() {
//            @Override
//            public void onFinished(AuthCredentials credentials, Authorizer.AuthError error) {
//                if (credentials != null && error == null) {
//                    try {
//                        updateCredentials(credentials);
//
//                    } catch (Exception e) {
//                        error = new Authorizer.AuthError(Authorizer.AuthError.Reason.InnerError, e);
//                    }
//                }
//
//                if (completion != null) {
//                    completion.onFinished(credentials, error);
//                }
//            }
//        });
        val authorizer = authorizer
        checkNotNull(authorizer)

        return try {
            val authCredentials = authorizer.authorize()
            updateCredentials(authCredentials)
            authCredentials
        } catch (e: Exception) {
            val error: Error = AuthError(Reason.InnerError, e)
            throw error
        }
    }

    @Throws(Exception::class)
    private fun updateCredentials(creds: AuthCredentials) {
        credentials = creds
        store()
    }

    @Throws(Exception::class)
    override fun store() {
        val accountStore = accountStore
        checkNotNull(accountStore)

        if (id == 0) { // for a new account
            id = accountStore.maxAccountId + 1
        }

        accountStore.putAccount(this)
    }

    override fun signCommand(command: ServiceCommand<*>) {
        val authorizer = authorizer
        val credentials = credentials
        checkNotNull(authorizer)
        checkNotNull(credentials)

        authorizer.signCommand(command, credentials)
    }

    companion object {
        private const val serialVersionUID = -6196365168677255570L
    }

}