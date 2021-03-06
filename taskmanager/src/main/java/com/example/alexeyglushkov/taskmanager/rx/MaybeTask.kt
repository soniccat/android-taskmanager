package com.example.alexeyglushkov.taskmanager.rx

import com.example.alexeyglushkov.taskmanager.task.TaskImpl

import org.junit.Assert

import java.util.concurrent.atomic.AtomicBoolean
import io.reactivex.Maybe
import io.reactivex.disposables.Disposable
import java.lang.Exception

class MaybeTask<T>(private val maybe: Maybe<T>) : TaskImpl() {
    var disposable: Disposable? = null

    override suspend fun startTask() {
        val finishedFlag = AtomicBoolean()
        disposable = maybe.subscribe({ t ->
            private.taskResult = t
            finishedFlag.set(true)
        }, { throwable ->
            private.taskError = Exception(throwable)
            finishedFlag.set(true)
        }, { finishedFlag.set(true) })

        Assert.assertTrue("MaybeTask: Maybe must be a sync task", finishedFlag.get())
    }

    override fun cancelTask(info: Any?) {
        super.cancelTask(info)
        disposable?.dispose()
    }
}
