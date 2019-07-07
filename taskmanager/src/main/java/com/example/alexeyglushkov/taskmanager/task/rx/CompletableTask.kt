package com.example.alexeyglushkov.taskmanager.task.rx

import com.example.alexeyglushkov.taskmanager.task.TaskImpl

import org.junit.Assert

import java.util.concurrent.atomic.AtomicBoolean
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

class CompletableTask(private val completable: Completable) : TaskImpl() {

    override fun startTask(callback: Task.Callback) {
        super.startTask(callback)

        val finishedFlag = AtomicBoolean()
        completable.subscribe({ finishedFlag.set(true) }, { throwable ->
            private.taskError = Error(throwable)
            finishedFlag.set(true)
        })

        Assert.assertTrue("Completable: Completable must be a sync task", finishedFlag.get())
        private.handleTaskCompletion(callback)
    }
}
