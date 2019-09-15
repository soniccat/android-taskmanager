package com.example.alexeyglushkov.taskmanager.task

interface ThreadRunner {
    fun setup()
    fun launch(block: () -> Unit)
    fun <T> run(block: () -> T): T

    fun isOnThread(): Boolean
    fun checkThread()
}