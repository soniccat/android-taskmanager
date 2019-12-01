package com.example.alexeyglushkov.authorization.Auth

import io.reactivex.Single

/**
 * Created by alexeyglushkov on 31.10.15.
 */
interface Authorizer {
    fun authorize(): Single<AuthCredentials>
    fun signCommand(command: ServiceCommand<*>, credentials: AuthCredentials)
    fun setServiceCommandProvider(provider: ServiceCommandProvider)
    fun setServiceCommandRunner(runner: ServiceCommandRunner)

    class AuthError : Error {
        enum class Reason {
            InnerError,
            UnknownError,
            Cancelled,
            NotAuthorized // for services
        }

        var reason: Reason
            protected set

        constructor(reason: Reason, throwable: Throwable?) : super(throwable) {
            this.reason = reason
        }

        constructor(detailMessage: String?, reason: Reason, throwable: Throwable?) : super(detailMessage, throwable) {
            this.reason = reason
        }

        companion object {
            private const val serialVersionUID = 6206983256074915330L
        }
    }
}