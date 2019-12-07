package com.aglushkov.taskmanager_http.loader.http

import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReader

/**
 * Created by alexeyglushkov on 31.10.15.
 */
interface HTTPConnectionStreamReader<T> : InputStreamDataReader<T>, HTTPConnectionHandler