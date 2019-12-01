package com.example.alexeyglushkov.service

import android.os.HandlerThread
import com.example.alexeyglushkov.authorization.Auth.*
import com.example.alexeyglushkov.authorization.Auth.Authorizer.AuthError
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import tools.RxTools

/**
 * Created by alexeyglushkov on 26.11.15.
 */
open class SimpleService : Service {
    override var account: Account? = null
    protected var commandProvider: ServiceCommandProvider? = null
    protected var commandRunner: ServiceCommandRunner? = null
    // to run authorization
    private var authThread: HandlerThread? = null


    override fun setServiceCommandProvider(provider: ServiceCommandProvider) {
        commandProvider = provider
    }

    override fun setServiceCommandRunner(runner: ServiceCommandRunner) {
        commandRunner = runner
    }

    override fun <T> runCommand(command: ServiceCommand<T>, canSignIn: Boolean): Single<T> {
        return runCommandInternal(command, canSignIn).flatMap { cmd -> RxTools.justOrError(cmd.response) }
    }

    private fun <T : ServiceCommand<*>> runCommandInternal(command: T, canSignIn: Boolean): Single<T> {
        val account = account
        checkNotNull(account)

        return if (!account.isAuthorized) {
            if (canSignIn) {
                authorizeAndRun(command)
            } else {
                val error: Error = AuthError(AuthError.Reason.NotAuthorized, null)
                Single.error(error)
            }
        } else {
            account.signCommand(command)
            runCommandInternal(command)
        }
    }

    override fun <T> runCommand(command: ServiceCommand<T>): Single<T> {
        return runCommandInternal(command).flatMap { cmd -> RxTools.justOrError(cmd.response) }
    }

    private fun <T : ServiceCommand<*>> runCommandInternal(command: T): Single<T> {
        val commandRunner = commandRunner
        checkNotNull(commandRunner)

        return commandRunner.run(command)
                .onErrorResumeNext { throwable ->
                    if (command.responseCode == 401) {
                        command.clear()
                        authorizeAndRun(command)
                    } else {
                        Single.error(throwable)
                    }
                }
    }

    fun <T : ServiceCommand<*>> authorizeAndRun(command: T): Single<T> {
        return authorize().flatMap {
            runCommandInternal(command, false)
        }
    }

    fun authorizeIfNeeded(): Single<AuthCredentials> {
        val account = account
        checkNotNull(account)

        return if (!account.isAuthorized) {
            authorize()
        } else {
            RxTools.justOrError(account.credentials)
        }
    }

    fun authorize(): Single<AuthCredentials> {
        val account = account
        val authThread = authThread
        checkNotNull(account)
        checkNotNull(authThread)

        startAuthThreadIfNeeded()
        return account.authorize().subscribeOn(AndroidSchedulers.from(authThread.looper))
    }

    private fun startAuthThreadIfNeeded() {
        if (authThread == null) {
            authThread = HandlerThread("SimpleService Auth Thread").apply {
                start()
            }
        }
    }

    override fun cancel(cmd: ServiceCommand<*>) {
        val commandRunner = commandRunner
        checkNotNull(commandRunner)

        commandRunner.cancel(cmd)
    }
}