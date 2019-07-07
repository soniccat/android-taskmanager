package com.example.alexeyglushkov.taskmanager.task.rx

import com.example.alexeyglushkov.taskmanager.task.TaskImpl

import org.junit.Assert

import java.util.concurrent.atomic.AtomicBoolean
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

class MaybeTask<T>(private val maybe: Maybe<T>) : TaskImpl() {

    override fun startTask(callback: Task.Callback) {
        super.startTask(callback)

        val finishedFlag = AtomicBoolean()
        maybe.subscribe({ t ->
            private.taskResult = t
            finishedFlag.set(true)
        }, { throwable ->
            private.taskError = Error(throwable)
            finishedFlag.set(true)
        }, { finishedFlag.set(true) })

        Assert.assertTrue("MaybeTask: Maybe must be a sync task", finishedFlag.get())
        private.handleTaskCompletion(callback)
    }
}
