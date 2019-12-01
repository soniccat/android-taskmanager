package com.example.alexeyglushkov.authorization.Auth

import io.reactivex.Single

/**
 * Created by alexeyglushkov on 01.11.15.
 */
interface ServiceCommandRunner {
    fun <T : ServiceCommand<*>> run(command: T): Single<T>
    fun <T : ServiceCommand<*>> run(command: T, callback: Callback)
    fun <T : ServiceCommand<*>> cancel(command: T)
    interface Callback {
        fun onCompleted(error: Error?, isCancelled: Boolean)
    }
}