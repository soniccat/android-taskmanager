package com.example.alexeyglushkov.taskmanager.task

import kotlinx.coroutines.runBlocking

class InstantThreadRunner: ThreadRunner {
    override fun setup() {
    }

    override fun launch(block: () -> Unit) {
        block()
    }

//    override fun <T> run(block: () -> T): T {
//        return block()
//    }

    override fun <T> run(block: suspend () -> T): T {
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