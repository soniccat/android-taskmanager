package com.example.alexeyglushkov.taskmanager.task.rx

import com.example.alexeyglushkov.taskmanager.task.Task
import com.example.alexeyglushkov.taskmanager.task.TaskImpl

import org.junit.Assert

import java.util.concurrent.atomic.AtomicBoolean
import io.reactivex.Completable
import io.reactivex.disposables.Disposable

class CompletableTask(private val completable: Completable) : TaskImpl() {
    var disposable: Disposable? = null

    override suspend fun startTask() {
        val finishedFlag = AtomicBoolean()
        disposable = completable.subscribe({ finishedFlag.set(true) }, { throwable ->
            private.taskError = Error(throwable)
            finishedFlag.set(true)
        })

        Assert.assertTrue("Completable: Completable must be a sync task", finishedFlag.get())
        private.handleTaskCompletion()
    }

    override fun cancelTask(info: Any?) {
        super.cancelTask(info)
        disposable?.dispose()
    }
}
