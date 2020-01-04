package com.example.alexeyglushkov.taskmanager.runners

import kotlinx.coroutines.CoroutineScope

interface ThreadRunner {
    fun setup()
    fun launch(block: () -> Unit)
    fun launchSuspend(block: suspend CoroutineScope.() -> Unit)
    fun <T> run(block: () -> T): T
    fun <T> runSuspend(block: suspend CoroutineScope.() -> T): T

    fun isOnThread(): Boolean
    fun checkThread()
}