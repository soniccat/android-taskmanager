package com.rssclient.model

import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder
import com.example.alexeyglushkov.service.SimpleService
import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler
import java.net.URL

class RssFeedService: SimpleService {
    //// Initialization
    constructor(commandProvider: ServiceCommandProvider, commandRunner: ServiceCommandRunner): super() {
        setServiceCommandProvider(commandProvider)
        setServiceCommandRunner(commandRunner)
    }

    suspend fun loadRss(url: URL): RssFeed {
        val builder = HttpUrlConnectionBuilder().setUrl(url)
        val command = commandProvider!!.getServiceCommand(builder, object : ByteArrayHandler<RssFeed> {
            override fun convert(`object`: ByteArray): RssFeed {
                val parser = RssFeedXmlParser(url)
                return parser.parse(`object`)
            }
        })

        return commandRunner!!.run(command)
    }
}