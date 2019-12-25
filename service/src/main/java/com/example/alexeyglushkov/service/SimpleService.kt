package com.example.alexeyglushkov.service

import android.os.HandlerThread
import com.example.alexeyglushkov.authorization.Auth.*
import com.example.alexeyglushkov.authorization.Auth.Authorizer.AuthError
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import tools.RxTools
import java.lang.Exception

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

    override suspend fun <T> runCommand(command: ServiceCommand<T>, canSignIn: Boolean): T {
        return runCommandInternal(command, canSignIn)
    }

    private suspend fun <R, T : ServiceCommand<R>> runCommandInternal(command: T, canSignIn: Boolean): R {
        val account = account
        checkNotNull(account)

        return if (!account.isAuthorized) {
            if (canSignIn) {
                authorizeAndRun(command)
            } else {
                throw AuthError(AuthError.Reason.NotAuthorized, null)
            }
        } else {
            account.signCommand(command)
            runCommandInternal(command)
        }
    }

    override suspend fun <T> runCommand(command: ServiceCommand<T>): T {
        return runCommandInternal(command)
    }

    private suspend fun <R, T : ServiceCommand<R>> runCommandInternal(command: T): R {
        val commandRunner = commandRunner
        checkNotNull(commandRunner)

        return try {
            commandRunner.run(command)
        } catch (ex: Exception) {
            if (command.responseCode == 401) {
                command.clear()
                authorizeAndRun(command)
            } else {
                throw ex
            }
        }
    }

    suspend fun <R, T : ServiceCommand<R>> authorizeAndRun(command: T): R {
        val response = authorize()
        return runCommandInternal(command, false)
    }

    suspend fun authorizeIfNeeded(): AuthCredentials {
        val account = account
        val credentials = account?.credentials
        checkNotNull(account)

        return if (!account.isAuthorized) {
            authorize()
        } else if (credentials != null) {
            credentials
        } else {
            throw Error("Empty credentials")
        }
    }

    suspend fun authorize(): AuthCredentials {
        val account = account
        val authThread = authThread
        checkNotNull(account)
        checkNotNull(authThread)

        startAuthThreadIfNeeded()
        return account.authorize()
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