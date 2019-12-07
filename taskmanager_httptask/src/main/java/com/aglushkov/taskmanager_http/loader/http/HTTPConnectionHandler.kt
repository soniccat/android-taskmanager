package com.aglushkov.taskmanager_http.loader.http

import java.net.HttpURLConnection

/**
 * Created by alexeyglushkov on 26.11.15.
 */
interface HTTPConnectionHandler {
    fun handleConnectionResponse(connection: HttpURLConnection)
}