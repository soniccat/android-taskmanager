package com.example.alexeyglushkov.authtaskmanager

import com.example.alexeyglushkov.authorization.Auth.ServiceCommand
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder
import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler

/**
 * Created by alexeyglushkov on 04.11.15.
 */
open class ServiceTaskProvider : ServiceCommandProvider {
    override fun <T> getServiceCommand(builder: HttpUrlConnectionBuilder, handler: ByteArrayHandler<T>): ServiceCommand<T> {
        return HttpServiceCommand(builder, handler)
    }
}