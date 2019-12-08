package com.example.alexeyglushkov.authorization.Auth

import io.reactivex.Single

/**
 * Created by alexeyglushkov on 01.11.15.
 */
interface ServiceCommandRunner {
    suspend fun <R, C : ServiceCommand<R>> run(command: C): R
    fun <C : ServiceCommand<*>> cancel(command: C)
}