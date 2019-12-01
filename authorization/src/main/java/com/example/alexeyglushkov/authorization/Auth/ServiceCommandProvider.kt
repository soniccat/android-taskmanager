package com.example.alexeyglushkov.authorization.Auth

import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder
import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler

/**
 * Created by alexeyglushkov on 01.11.15.
 */
// TODO: it seems we can remove that class
interface ServiceCommandProvider {
    fun <T> getServiceCommand(builder: HttpUrlConnectionBuilder, handler: ByteArrayHandler<T>): ServiceCommand<T>
}