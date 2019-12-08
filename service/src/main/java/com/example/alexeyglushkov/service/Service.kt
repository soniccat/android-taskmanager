package com.example.alexeyglushkov.service

import com.example.alexeyglushkov.authorization.Auth.Account
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner
import io.reactivex.Single

/**
 * Created by alexeyglushkov on 26.11.15.
 */
interface Service {
    var account: Account?

    fun setServiceCommandProvider(provider: ServiceCommandProvider) // TODO consider just var
    fun setServiceCommandRunner(runner: ServiceCommandRunner)
    suspend fun <T> runCommand(command: ServiceCommand<T>, canSignIn: Boolean): T
    suspend fun <T> runCommand(command: ServiceCommand<T>): T
    fun cancel(cmd: ServiceCommand<*>)
}