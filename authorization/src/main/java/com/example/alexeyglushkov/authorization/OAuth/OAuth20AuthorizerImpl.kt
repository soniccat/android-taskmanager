package com.example.alexeyglushkov.authorization.OAuth

import android.net.Uri
import com.example.alexeyglushkov.authorization.Api.DefaultApi20
import com.example.alexeyglushkov.authorization.Auth.AuthCredentials
import com.example.alexeyglushkov.authorization.Auth.Authorizer.AuthError
import com.example.alexeyglushkov.authorization.Auth.Authorizer.AuthError.Reason
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder
import com.example.alexeyglushkov.streamlib.convertors.BytesStringConverter
import com.example.alexeyglushkov.tools.CancelError
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.functions.Function

class OAuth20AuthorizerImpl(private val api: DefaultApi20,
                            private val config: OAuthConfig): OAuth20Authorizer {
    override var webClient: OAuthWebClient? = null
    private var commandRunner: ServiceCommandRunner? = null // TODO: move optionals into the constructor to make them nonoptional
    private var commandProvider: ServiceCommandProvider? = null

    override val authorizationUrl: String
        get() {
            return api.getAuthorizationUrl(config)
        }

    override fun setServiceCommandRunner(runner: ServiceCommandRunner) {
        commandRunner = runner
    }

    override fun setServiceCommandProvider(provider: ServiceCommandProvider) {
        commandProvider = provider
    }

// TODO: parse code here instead of retrieveAccessToken
// TODO: use Single<String> instead of Single<ServiceCommand<String>>
    override fun retrieveAccessToken(code: String): Single<ServiceCommand<String>> {
        val commandProvider = commandProvider
        val commandRunner = commandRunner
        checkNotNull(commandProvider)
        checkNotNull(commandRunner)

        val builder = HttpUrlConnectionBuilder()
        api.fillAccessTokenConnectionBuilder(builder, config, code)
        val command = commandProvider.getServiceCommand<String>(builder, BytesStringConverter())

        return commandRunner.run(command).onErrorResumeNext {
            val err: Error = AuthError("OAuth20AuthorizerImpl authorize: Can't receive AccessToken", Reason.InnerError, it)
            Single.error(err)
        }
    }

    override fun authorize(): Single<AuthCredentials> {
        return webAuthorization().flatMap {
            extractAccessToken(it)
        }
    }

    private fun extractAccessToken(code: String?): Single<out AuthCredentials> {
        return if (code == null) {
            Single.error(Error("OAuth20AuthorizerImpl authorize: empty code"))
        } else {
            retrieveAccessToken(code).flatMap { command ->
                val response = command.response

                if (response != null) {
                    val authCredentials = api.createCredentials(response)
                    val result: Single<out AuthCredentials>
                    result = if (authCredentials != null && authCredentials.isValid) {
                        Single.just(authCredentials)
                    } else {
                        val localError = AuthError("OAuth20AuthorizerImpl authorize: Can't parse AccessToken", Reason.UnknownError, null)
                        Single.error(localError)
                    }
                    return@flatMap result
                } else {
                    val localError = AuthError("OAuth20AuthorizerImpl authorize: Empty response", Reason.UnknownError, null)
                    Single.error<AuthCredentials>(localError)
                }
            }
        }
    }

    private fun webAuthorization(): Single<String> {
        val webClient = webClient
        val callback = config.callback
        checkNotNull(webClient)
        checkNotNull(callback)

        val url = authorizationUrl
        return webClient.loadUrl(url, callback).flatMap {
            val uri = Uri.parse(url)
            var code: String? = null
            var error: AuthError? = null
            if (isCancelled(uri)) {
                error = AuthError(Reason.Cancelled, null)
            } else {
                code = getCode(uri)
                if (code == null) {
                    error = AuthError("OAuth20AuthorizerImpl authorize: Can't parse code", Reason.UnknownError, null)
                }
            }

            if (error != null) {
                Single.error<String>(error)
            } else {
                Single.just<String>(code)
            }
        }.onErrorResumeNext({ throwable ->
            val reason: Reason
            reason = if (throwable is CancelError) {
                Reason.Cancelled
            } else {
                Reason.InnerError
            }

            val authError = AuthError(reason, throwable)
            Single.error<String>(authError)
        })
    }

    private fun getCode(uri: Uri): String? {
        return uri.getQueryParameter("code")
    }

    private fun isCancelled(uri: Uri): Boolean {
        val error = uri.getQueryParameter("error")
        return error != null && error == "access_denied"
    }

    override fun signCommand(command: ServiceCommand<*>, credentials: AuthCredentials) {
        val oAuthCredentials = credentials as OAuthCredentials
        api.signCommand(command, oAuthCredentials)
    }

    companion object {
        /**
         * {@inheritDoc}
         */
        const val version = "2.0"
    }
}