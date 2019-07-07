package com.example.alexeyglushkov.taskmanager.task.rx

import com.example.alexeyglushkov.taskmanager.task.Task
import com.example.alexeyglushkov.taskmanager.task.TaskImpl

import org.junit.Assert

import java.util.concurrent.atomic.AtomicBoolean
import io.reactivex.Single
import io.reactivex.functions.Consumer

class SingleTask<T>(private val single: Single<T>) : TaskImpl() {

    override fun startTask(callback: Task.Callback) {
        super.startTask(callback)

        val finishedFlag = AtomicBoolean()
        single.subscribe({ t ->
            private.taskResult = t
            finishedFlag.set(true)
        }, { throwable ->
            private.taskError = Error(throwable)
            finishedFlag.set(true)
        })

        Assert.assertTrue("SingleTask: Single must be a sync task", finishedFlag.get())
        private.handleTaskCompletion(callback)
    }
}
