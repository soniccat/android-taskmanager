package com.aglushkov.taskmanager_http.loader.http

import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by alexeyglushkov on 02.08.15.
 */
interface HttpURLConnectionProvider {
    val url: URL
    fun getUrlConnection(): HttpURLConnection
}