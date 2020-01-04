package com.example.alexeyglushkov.taskmanager.runners

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

class InstantThreadRunner: ThreadRunner {
    override fun setup() {
    }

    override fun launch(block: () -> Unit) {
        block()
    }

    override fun launchSuspend(block: suspend CoroutineScope.() -> Unit) {
        runBlocking {
            block()
        }
    }

    override fun <T> run(block: () -> T): T {
        return block()
    }

    override fun <T> runSuspend(block: suspend CoroutineScope.() -> T): T {
        return runBlocking {
            block()
        }
    }

    override fun isOnThread(): Boolean {
        return true
    }

    override fun checkThread() {
    }
}