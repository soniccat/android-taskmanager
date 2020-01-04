package com.example.alexeyglushkov.taskmanager.runners

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert

class ScopeThreadRunner(val scope: CoroutineScope, val threadId: String): ThreadRunner {
    private val threadLocal = ThreadLocal<String>()

    override fun setup() {
        threadLocal.set(threadId)
    }

    override fun launch(block: () -> Unit) {
        if (isOnThread()) {
            block()
        } else {
            scope.launch {
                block()
            }
        }
    }

    override fun launchSuspend(block: suspend CoroutineScope.() -> Unit) {
        scope.launch {
            block()
        }
    }

    override fun <T> run(block: () -> T): T {
        if (isOnThread()) {
            return block()
        } else {
            return runBlocking(scope.coroutineContext) {
                block()
            }
        }
    }

    override fun <T> runSuspend(block: suspend CoroutineScope.() -> T): T {
        if (isOnThread()) {
            return runBlocking {
                block()
            }
        } else {
            return runBlocking(scope.coroutineContext) {
                block()
            }
        }
    }

    override fun isOnThread(): Boolean {
        return threadLocal.get() == threadId
    }

    override fun checkThread() {
        Assert.assertTrue("Scope thread is expected", isOnThread())
    }
}