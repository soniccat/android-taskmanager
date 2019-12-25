package com.example.alexeyglushkov.authorization.Auth

import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder
import com.example.alexeyglushkov.streamlib.progress.ProgressListener
import java.lang.Exception

/**
 * Created by alexeyglushkov on 31.10.15.
 */
// A Service command represents an url request.
// If you want to repeat a command you should call clear when it finishes and start it again.
interface ServiceCommand<T> {
    // TODO: it seems these 3 methods could be not necessary (see dropbox upload command)
    val connectionBuilder: HttpUrlConnectionBuilder
    var progressListener: ProgressListener?

    val response: T?
    val responseCode: Int
    val commandError: Exception?
    val isCancelled: Boolean
    fun clear()
}