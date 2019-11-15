package com.example.alexeyglushkov.taskmanager.task

import kotlinx.coroutines.CoroutineScope

interface ThreadRunner {
    fun setup()
    fun launch(block: () -> Unit)
    fun <T> run(block: () -> T): T
    fun <T> runSuspend(block: suspend () -> T): T

    fun isOnThread(): Boolean
    fun checkThread()
}