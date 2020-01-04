package com.example.alexeyglushkov.taskmanager.rx

import com.example.alexeyglushkov.taskmanager.task.TaskImpl

import org.junit.Assert

import java.util.concurrent.atomic.AtomicBoolean
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import java.lang.Exception

class SingleTask<T>(private val single: Single<T>) : TaskImpl() {
    var disposable: Disposable? = null

    override suspend fun startTask() {
        val finishedFlag = AtomicBoolean()
        disposable = single.subscribe({ t ->
            private.taskResult = t
            finishedFlag.set(true)
        }, { throwable ->
            private.taskError = Exception(throwable)
            finishedFlag.set(true)
        })

        Assert.assertTrue("SingleTask: Single must be a sync task", finishedFlag.get())
    }

    override fun cancelTask(info: Any?) {
        super.cancelTask(info)
        disposable?.dispose()
    }
}
