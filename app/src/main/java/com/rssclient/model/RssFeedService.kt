package com.rssclient.model

import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskProvider
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskRunner
import com.example.alexeyglushkov.service.SimpleService

class RssFeedService: SimpleService {

    //// Initialization
    constructor(): super() {
        setServiceCommandProvider(ServiceTaskProvider())
        //setServiceCommandRunner(ServiceTaskRunner())
    }
}